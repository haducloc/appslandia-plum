// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.base.TextBuilder;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.STR;
import com.appslandia.common.utils.SplitUtils;
import com.appslandia.common.utils.SplittingBehavior;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.common.utils.ValueUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class ActionDescProvider extends InitializingObject {

  private ActionDesc home;
  private Map<String, Map<ActionRoute, ActionDesc>> actionDescs = new HashMap<>();
  private Map<String, String> controllerModules = new HashMap<>();

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  @Inject
  protected ModuleConfigProvider moduleConfigProvider;

  @Inject
  protected HeaderPolicyProvider headerPolicyProvider;

  @Override
  protected void init() throws Exception {
    Asserts.isTrue(actionDescs.keySet().containsAll(appConfig.getEnableModules()),
        "appConfig.getEnableModules() contains module(s) that are not registered by an active controller.");

    Asserts.isTrue(
        ModuleConfig.MODULE_NONE.equals(moduleConfigProvider.getFallbackModule())
            || appConfig.getEnableModules().contains(moduleConfigProvider.getFallbackModule()),
        "The fallback module must be '{}' or one of the enabled modules.", ModuleConfig.MODULE_NONE);

    actionDescs = Collections.unmodifiableMap(actionDescs);
    controllerModules = Collections.unmodifiableMap(controllerModules);
  }

  public ActionDesc getHomeDesc() {
    initialize();
    return home;
  }

  public Map<String, Map<ActionRoute, ActionDesc>> getActionDescs() {
    initialize();
    return actionDescs;
  }

  public Map<String, String> getControllerModules() {
    return controllerModules;
  }

  protected void registerController(Class<?> controllerClass) {
    Arguments.isTrue(ControllerBase.class.isAssignableFrom(controllerClass));

    // @Removed
    if (controllerClass.getDeclaredAnnotation(Removed.class) != null) {
      return;
    }

    // module
    var module = parseModule(controllerClass, appConfig.getEnableModules());
    if (!appConfig.getEnableModules().contains(module)) {
      return;
    }

    // controller
    var controller = ActionDescUtils.getController(controllerClass);
    Asserts.isTrue(
        !moduleConfigProvider.getModuleConfigs().values().stream().anyMatch(m -> m.getLanguage(controller) != null),
        "A controller route cannot be a language identifier. Controller={}.", controller);

    var mappedModule = controllerModules.computeIfAbsent(controller, c -> module);
    Asserts.isTrue(mappedModule.equals(module),
        "A controller route cannot be mapped to different modules. Controller={}.", controller);

    var actionDescMap = actionDescs.computeIfAbsent(module, mod -> new HashMap<ActionRoute, ActionDesc>());

    // Actions
    for (Method actionMethod : controllerClass.getMethods()) {
      if (!ActionDescUtils.isActionMethod(actionMethod) || actionMethod.getDeclaredAnnotation(Removed.class) != null) {
        continue;
      }
      var action = ActionDescUtils.getAction(actionMethod);

      // ActionDesc
      var actionDesc = new ActionDesc();
      actionDesc.setModule(module);
      actionDesc.setController(controller);
      actionDesc.setAction(action);
      actionDesc.setMvcPath(STR.fmt("/{}/{}", controller, action));

      actionDesc.setMethod(actionMethod);
      actionDesc.setControllerClass(controllerClass);

      // HTTP Methods
      var httpMethods = parseHttpMethods(actionMethod);
      actionDesc.setHttpMethods(httpMethods);

      // PathParams
      var pathParams = actionMethod.getDeclaredAnnotation(PathParams.class);
      Asserts.isTrue(!ServletUtils.ACTION_INDEX.equalsIgnoreCase(action) || pathParams == null,
          "The index action cannot have @PathParams. Action={}.", actionMethod);

      var parsedPathParams = (pathParams != null) ? parsePathParams(pathParams.value()) : null;
      actionDesc.setPathParams(parsedPathParams);

      // ParamDescs
      var parsedParamDescs = parseParamDescs(actionMethod, actionDesc.getPathParams());
      actionDesc.setParamDescs(parsedParamDescs);

      // @ConsumeType
      if (parsedParamDescs.stream()
          .anyMatch(p -> (p.getBindModel() != null) && p.getBindModel().value() == BindSource.JSON_BODY)) {
        actionDesc.setConsumeType(ConsumeType.APP_JSON);
      } else {
        var consumeType = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(ConsumeType.class),
            controllerClass.getDeclaredAnnotation(ConsumeType.class));
        actionDesc.setConsumeType(consumeType);
      }

      // @Authorize
      var authorize = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(Authorize.class),
          controllerClass.getDeclaredAnnotation(Authorize.class));
      actionDesc.setAuthorize(authorize);

      // @EnablePart
      var enablePart = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(EnablePart.class),
          controllerClass.getDeclaredAnnotation(EnablePart.class));
      actionDesc.setEnablePart(enablePart);

      // @EnableAsync
      var enableAsync = actionMethod.getDeclaredAnnotation(EnableAsync.class);
      actionDesc.setEnableAsync(enableAsync);
      final var isJsonValueResult = ActionDescUtils.isJsonValueResult(actionMethod);

      if (enableAsync != null && enableAsync.dispatchToView()) {
        Asserts.isTrue(!isJsonValueResult,
            "Action with @EnableAsync(dispatchToView=true) must not return an json value result. Action={}",
            actionMethod);
      }

      // @EnableCsrf
      var enableCsrf = actionMethod.getDeclaredAnnotation(EnableCsrf.class);
      actionDesc.setEnableCsrf(enableCsrf);

      // @EnableEtag
      var enableEtag = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(EnableEtag.class),
          controllerClass.getDeclaredAnnotation(EnableEtag.class));
      actionDesc.setEnableEtag(enableEtag);

      // @EnableCompress
      var enableCompress = resolveCompression(actionMethod, controllerClass);
      actionDesc.setEnableCompress(enableCompress);

      // @EnableCors
      var enableCors = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(EnableCors.class),
          controllerClass.getDeclaredAnnotation(EnableCors.class));
      actionDesc.setEnableCors(enableCors);

      // @EnableJsonError
      if (isJsonValueResult) {
        actionDesc.setEnableJsonError(EnableJsonError.IMPL);
      } else {
        var enableJsonError = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(EnableJsonError.class),
            controllerClass.getDeclaredAnnotation(EnableJsonError.class));
        actionDesc.setEnableJsonError(enableJsonError);
      }

      // ActionRoute
      var moduleConfig = moduleConfigProvider.getModuleConfig(module);
      var actionRoute = new ActionRoute(null, controller, action);
      Asserts.isTrue(!actionDescMap.containsKey(actionRoute), "Duplicate route: /{}/{}", controller, action);

      actionDescMap.put(actionRoute, actionDesc);
      for (var language : moduleConfig.getLanguages()) {
        actionDescMap.put(new ActionRoute(language.getId(), controller, action), actionDesc);
      }

      // Index
      if (ServletUtils.ACTION_INDEX.equalsIgnoreCase(action)) {

        // @Home
        if (controllerClass.getAnnotation(Home.class) != null) {
          Asserts.isNull(home, "Duplicate @Home annotation.");
          home = actionDesc;
        }
      }
    }
  }

  public String getMvcRoutesDescription() {
    initialize();
    var allDesc = new TextBuilder();

    for (var moduleEntry : actionDescs.entrySet()) {
      allDesc.append("***** ").append(moduleEntry.getKey()).append(" *****");
      allDesc.appendln();

      var count = 0;

      var actionDescs = moduleEntry.getValue().values().stream().distinct()
          .collect(Collectors.toCollection(ArrayList::new));

      actionDescs.sort(Comparator.comparing(ActionDesc::getController).thenComparing((d1, d2) -> {

        if (ServletUtils.ACTION_INDEX.equalsIgnoreCase(d1.getAction())) {
          return -1;
        }
        if (ServletUtils.ACTION_INDEX.equalsIgnoreCase(d2.getAction())) {
          return 1;
        }

        return Integer.compare(d1.getAction().length(), d2.getAction().length());
      }));

      for (var actionDesc : actionDescs) {
        if (++count % 5 == 0) {
          allDesc.appendln();
        }

        var attributes = new LinkedHashSet<String>();

        if (actionDesc.getAuthorize() != null) {
          attributes.add("Authorize");
        }
        if (actionDesc.getEnableCors() != null) {
          attributes.add("Cors");
        }
        if (actionDesc.getEnableCompress() != null) {
          attributes.add("Compress");
        }
        if (actionDesc.getEnableEtag() != null) {
          attributes.add("Etag");
        }
        if (actionDesc.getEnableJsonError() != null) {
          attributes.add("JsonError");
        }
        if (actionDesc.getEnableAsync() != null) {
          attributes.add("Async");
        }
        if (attributes.isEmpty()) {
          attributes.add("___");
        }

        var desc = new TextBuilder();

        if (ServletUtils.ACTION_INDEX.equalsIgnoreCase(actionDesc.getAction())) {
          var handingPathIdx = STR.fmt("/{}", actionDesc.getController());
          addPathInfo(handingPathIdx, desc, actionDesc);
          desc.append(String.join("|", attributes));

          allDesc.appendln();
          allDesc.appendsp(2).append(desc.toString());
        }

        var handingPath = STR.fmt("/{}/{}", actionDesc.getController(), actionDesc.getAction());
        desc.clear();
        addPathInfo(handingPath, desc, actionDesc);
        desc.append(String.join("|", attributes));

        allDesc.appendln();
        allDesc.appendsp(2).append(desc.toString());
      }

      allDesc.appendln();
      allDesc.appendln();
    }
    return allDesc.toString();
  }

  public String getHeaderPolicyDescription() {
    initialize();
    var allDesc = new TextBuilder();

    for (var moduleEntry : actionDescs.entrySet()) {
      allDesc.append("***** ").append(moduleEntry.getKey()).append(" *****");
      allDesc.appendln();

      var count = 0;

      var actionDescs = moduleEntry.getValue().values().stream().distinct()
          .collect(Collectors.toCollection(ArrayList::new));

      actionDescs.sort(Comparator.comparing(ActionDesc::getController).thenComparing((d1, d2) -> {

        if (ServletUtils.ACTION_INDEX.equalsIgnoreCase(d1.getAction())) {
          return -1;
        }
        if (ServletUtils.ACTION_INDEX.equalsIgnoreCase(d2.getAction())) {
          return 1;
        }

        return Integer.compare(d1.getAction().length(), d2.getAction().length());
      }));

      for (var actionDesc : actionDescs) {
        if (++count % 5 == 0) {
          allDesc.appendln();
        }

        var desc = new TextBuilder();
        var policies = new ArrayList<String>();

        if (ServletUtils.ACTION_INDEX.equalsIgnoreCase(actionDesc.getAction())) {
          var handingPathIdx = STR.fmt("/{}", actionDesc.getController());

          headerPolicyProvider.getHeaderPolicies().stream().filter(p -> p.matches(handingPathIdx))
              .forEach(p -> policies.add(p.getName()));

          addPathInfo(handingPathIdx, desc, actionDesc);
          desc.append(String.join(", ", policies));

          allDesc.appendln();
          allDesc.appendsp(2).append(desc.toString());
        }

        var handingPath = STR.fmt("/{}/{}", actionDesc.getController(), actionDesc.getAction());
        policies.clear();
        desc.clear();

        headerPolicyProvider.getHeaderPolicies().stream().filter(p -> p.matches(handingPath))
            .forEach(p -> policies.add(p.getName()));

        addPathInfo(handingPath, desc, actionDesc);
        desc.append(String.join(", ", policies));

        allDesc.appendln();
        allDesc.appendsp(2).append(desc.toString());
      }

      allDesc.appendln();
      allDesc.appendln();
    }
    return allDesc.toString();
  }

  private static void addPathInfo(String handingPath, TextBuilder desc, ActionDesc actionDesc) {
    desc.append(String.join("|", actionDesc.getHttpMethods()));

    if (desc.length() < 32) {
      desc.appendsp(32 - desc.length());
    }
    desc.appendsp();
    desc.append(handingPath);

    if (!actionDesc.getPathParams().isEmpty()) {
      desc.append("/**");
    }

    if (desc.length() < 72) {
      desc.appendsp(72 - desc.length());
    }
    desc.appendsp();
  }

  public static String parseModule(Class<?> controllerClass, Set<String> enabledModules) {
    String module = null;
    var moduleAnn = controllerClass.getDeclaredAnnotation(Module.class);

    if (moduleAnn != null) {
      module = StringUtils.trimToNull(moduleAnn.value());

    } else if (enabledModules.size() == 1) {
      module = enabledModules.iterator().next();
    }
    Asserts.isTrue(module != null, "Couldn't determine the module for controller {}.", controllerClass);
    Asserts.isTrue(!ModuleConfig.MODULE_NONE.equals(module));

    return module;
  }

  static EnableCompress resolveCompression(Method method, Class<?> controllerClass) {
    var enableCompress = method.getDeclaredAnnotation(EnableCompress.class);
    if (enableCompress == null) {
      enableCompress = controllerClass.getDeclaredAnnotation(EnableCompress.class);
    }
    return (enableCompress != null && !enableCompress.remove()) ? enableCompress : null;
  }

  public static List<ParamDesc> parseParamDescs(Method actionMethod, List<PathParam> pathParams) {
    List<ParamDesc> paramDescs = new ArrayList<>(actionMethod.getParameterCount());
    for (Parameter parameter : actionMethod.getParameters()) {

      // paramDesc
      var paramDesc = new ParamDesc();
      paramDesc.setParameter(parameter);
      paramDescs.add(paramDesc);

      var paramType = parameter.getType();
      if ((paramType == HttpServletRequest.class) || (paramType == HttpServletResponse.class)
          || (paramType == HttpActionRequest.class) || (paramType == RequestContext.class)
          || (paramType == ModelState.class)) {
        continue;
      }

      // @BindModel
      var bindModel = parameter.getDeclaredAnnotation(BindModel.class);
      if (bindModel != null) {
        Asserts.isNull(parameter.getDeclaredAnnotation(Valid.class), "@Valid is unsupported: parameter={}.", parameter);
        paramDesc.setBindModel(bindModel);
        continue;
      }

      // @BindField
      var bindField = parameter.getDeclaredAnnotation(BindField.class);
      paramDesc.setBindField(bindField);

      var isPathParam = pathParams.stream().anyMatch(p -> p.hasPathParam(paramDesc.getParamName()));
      paramDesc.setPathParam(isPathParam);
    }
    return paramDescs;
  }

  public static List<String> parseHttpMethods(Method method) {
    Set<String> httpMethods = new LinkedHashSet<>();

    if (parseHttpMethod(method, HttpGet.class, httpMethods)) {
      httpMethods.add(HttpMethod.HEAD);
    }
    if (parseHttpMethod(method, HttpGetPost.class, httpMethods)) {
      httpMethods.add(HttpMethod.HEAD);
    }

    parseHttpMethod(method, HttpPost.class, httpMethods);
    parseHttpMethod(method, HttpPut.class, httpMethods);
    parseHttpMethod(method, HttpDelete.class, httpMethods);
    parseHttpMethod(method, HttpPatch.class, httpMethods);
    parseHttpMethod(method, HttpOptions.class, httpMethods);

    return new ArrayList<>(httpMethods);
  }

  private static boolean parseHttpMethod(Method method, Class<? extends Annotation> httpMethodClass,
      Set<String> httpMethods) {
    if (method.getDeclaredAnnotation(httpMethodClass) == null) {
      return false;
    }
    if (httpMethodClass == HttpGetPost.class) {
      CollectionUtils.addAll(httpMethods, HttpMethod.GET, HttpMethod.POST);
      return true;
    }

    var ant = httpMethodClass.getDeclaredAnnotation(HttpMethod.class);
    httpMethods.add(ant.value());
    return true;
  }

  private static final String PARAM_FORMAT = "\\{[a-z\\d_.]+}";
  private static final Pattern PARAM_PATTERN = Pattern.compile(PARAM_FORMAT, Pattern.CASE_INSENSITIVE);
  private static final Pattern PATH_PARAMS_PATTERN = Pattern
      .compile(String.format("(/%s(-%s)*)+", PARAM_FORMAT, PARAM_FORMAT), Pattern.CASE_INSENSITIVE);

  // pathParams: (/{parameter}(-{parameter})*)+
  public static List<PathParam> parsePathParams(String pathParams) {
    Arguments.notNull(pathParams);
    Arguments.isTrue(PATH_PARAMS_PATTERN.matcher(pathParams).matches(), "pathParams '{}' is invalid.", pathParams);

    List<PathParam> parsedParams = new ArrayList<>();
    var params = SplitUtils.split(pathParams, '/', SplittingBehavior.ORIGINAL);

    for (String param : params) {
      if (param.isEmpty()) {
        continue;
      }
      // {parameter}
      if (PARAM_PATTERN.matcher(param).matches()) {
        parsedParams.add(new PathParam(param.substring(1, param.length() - 1)));

      } else {
        // {parameter}(-{parameter})+
        var parsedSubParams = parseSubParams(param);
        parsedParams.add(new PathParam(parsedSubParams));
      }
    }
    return parsedParams;
  }

  // subParams: {parameter}(-{parameter})+
  public static List<PathParam> parseSubParams(String subParams) {
    List<PathParam> parsedParams = new ArrayList<>();
    var params = SplitUtils.split(subParams, '-', SplittingBehavior.ORIGINAL);

    // subParam: {parameter}
    for (String subParam : params) {
      parsedParams.add(new PathParam(subParam.substring(1, subParam.length() - 1)));
    }
    return parsedParams;
  }

  public static int getPathParamCount(List<PathParam> pathParams) {
    Queue<PathParam> pq = new LinkedList<>();
    for (PathParam pathParam : pathParams) {
      pq.add(pathParam);
    }
    var count = 0;
    while (!pq.isEmpty()) {
      var pathParam = pq.remove();

      if (pathParam.getParamName() != null) {
        count++;
      } else {
        for (PathParam subParam : pathParam.getSubParams()) {
          pq.add(subParam);
        }
      }
    }
    return count;
  }
}
