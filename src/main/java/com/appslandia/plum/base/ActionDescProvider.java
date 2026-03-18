// The MIT License (MIT)
// Copyright © 2015 Loc Ha

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.appslandia.plum.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Pattern;

import com.appslandia.common.base.CaseInsensitiveMap;
import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.CollectionUtils;
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

  private Map<String, ActionDesc> formLogins = new CaseInsensitiveMap<>();

  private String homeController;
  private Map<ActionRoute, ActionDesc> actionDescMap = new HashMap<>();

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  @Override
  protected void init() throws Exception {
    formLogins = Collections.unmodifiableMap(formLogins);
    actionDescMap = Collections.unmodifiableMap(actionDescMap);
  }

  public Map<ActionRoute, ActionDesc> getActionDescMap() {
    initialize();
    return actionDescMap;
  }

  public ActionDesc getActionDesc(String controller, String action) {
    initialize();
    return actionDescMap.get(new ActionRoute(controller, action));
  }

  public ActionDesc getHomeDesc() {
    initialize();
    if (homeController == null) {
      return null;
    }
    return actionDescMap.get(new ActionRoute(homeController, ServletUtils.ACTION_INDEX));
  }

  public String getHomeController() {
    initialize();
    return homeController;
  }

  public ActionDesc getFormLogin(String module) {
    initialize();
    return formLogins.get(module);
  }

  protected void registerController(Class<?> controllerClass) {
    Arguments.isTrue(ControllerBase.class.isAssignableFrom(controllerClass));

    // @Removed
    if (controllerClass.getDeclaredAnnotation(Removed.class) != null) {
      return;
    }

    // Module
    var moduleName = appConfig.getStringReq(AppConfig.CONFIG_DEFAULT_MODULE);

    var module = controllerClass.getDeclaredAnnotation(Module.class);
    if (module != null) {
      moduleName = StringUtils.trimToNull(module.value());
      Asserts.notNull(moduleName, "Invalid module={}.", controllerClass);
    }

    if (!appConfig.getModules().contains(moduleName)) {
      appLogger.warn("Module '${0}' is unallowed. Skipping: ${1}", moduleName, controllerClass);
      return;
    }

    var controller = getController(controllerClass);

    // Actions
    for (Method actionMethod : controllerClass.getMethods()) {
      if (!ActionDescUtils.isActionMethod(actionMethod)) {
        continue;
      }
      var action = getAction(actionMethod);

      // ActionDesc
      var actionDesc = new ActionDesc();
      actionDesc.setModule(moduleName);
      actionDesc.setController(controller);
      actionDesc.setAction(action);
      actionDesc.setMethod(actionMethod);
      actionDesc.setControllerClass(controllerClass);

      // HTTP Methods
      var httpMethods = parseHttpMethods(actionMethod);
      actionDesc.setHttpMethods(Collections.unmodifiableList(httpMethods));

      // PathParams
      var pathParams = actionMethod.getDeclaredAnnotation(PathParams.class);
      var pathParamsList = (pathParams != null) ? parsePathParams(pathParams.value()) : null;
      actionDesc.setPathParams(
          pathParamsList != null ? Collections.unmodifiableList(pathParamsList) : Collections.emptyList());

      // ParamDescs
      var paramDescsList = parseParamDescs(actionMethod, actionDesc.getPathParams());
      actionDesc.setParamDescs(Collections.unmodifiableList(paramDescsList));

      // @ConsumeType
      if (paramDescsList.stream()
          .anyMatch(p -> (p.getBindModel() != null) && p.getBindModel().value() == ModelSource.JSON_BODY)) {
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

      // @EnableParts
      var enableParts = actionMethod.getDeclaredAnnotation(EnableParts.class);
      actionDesc.setEnableParts(enableParts);

      // @EnableAsync
      var enableAsync = actionMethod.getDeclaredAnnotation(EnableAsync.class);
      actionDesc.setEnableAsync(enableAsync);

      // @EnableCsrf
      var enableCsrf = actionMethod.getDeclaredAnnotation(EnableCsrf.class);
      actionDesc.setEnableCsrf(enableCsrf);

      // @EnableCaptcha
      var enableCaptcha = actionMethod.getDeclaredAnnotation(EnableCaptcha.class);
      actionDesc.setEnableCaptcha(enableCaptcha);

      // @EnableJsonError
      if (ActionDescUtils.isJsonResult(actionMethod)) {
        actionDesc.setEnableJsonError(EnableJsonError.IMPL);
      } else {
        var enableJsonError = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(EnableJsonError.class),
            controllerClass.getDeclaredAnnotation(EnableJsonError.class));
        actionDesc.setEnableJsonError(enableJsonError);
      }

      // ActionRoute
      var actionRoute = new ActionRoute(controller, action);
      Asserts.isTrue(!actionDescMap.containsKey(actionRoute), "Duplicate route: {}/{}", controllerClass, actionMethod);

      // Index
      if (ServletUtils.ACTION_INDEX.equalsIgnoreCase(action)) {

        // @Home
        if (controllerClass.getAnnotation(Home.class) != null) {
          Asserts.isNull(homeController, "Duplicate @Home: {}", controllerClass);
          homeController = controller;
        }
      }

      // @FormLogin
      if (actionMethod.getDeclaredAnnotation(FormLogin.class) != null) {
        Asserts.isTrue(!formLogins.containsKey(moduleName), "Duplicate @FormLogin: {}", moduleName);
        formLogins.put(moduleName, actionDesc);
      }
      actionDescMap.put(actionRoute, actionDesc);
    }
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
          || (paramType == RequestWrapper.class) || (paramType == RequestContext.class)
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

      // @BindValue
      var bindValue = parameter.getDeclaredAnnotation(BindValue.class);
      paramDesc.setBindValue(bindValue);
      paramDesc.setPathParam(pathParams.stream().anyMatch(p -> p.hasPathParam(paramDesc.getParamName())));
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
      CollectionUtils.toSet(httpMethods, HttpMethod.GET, HttpMethod.POST);
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

    List<PathParam> pars = new ArrayList<>();
    var params = SplitUtils.split(pathParams, '/', SplittingBehavior.ORIGINAL);

    for (String param : params) {
      if (param.isEmpty()) {
        continue;
      }
      // {parameter}
      if (PARAM_PATTERN.matcher(param).matches()) {
        pars.add(new PathParam(param.substring(1, param.length() - 1)));

      } else {
        // {parameter}(-{parameter})+
        var parsedSubParams = parseSubParams(param);
        pars.add(new PathParam(Collections.unmodifiableList(parsedSubParams)));
      }
    }
    return pars;
  }

  // subParams: {parameter}(-{parameter})+
  public static List<PathParam> parseSubParams(String subParams) {
    List<PathParam> pars = new ArrayList<>();
    var params = SplitUtils.split(subParams, '-', SplittingBehavior.ORIGINAL);

    // subParam: {parameter}
    for (String subParam : params) {
      pars.add(new PathParam(subParam.substring(1, subParam.length() - 1)));
    }
    return pars;
  }

  public static int getPathParamCount(List<PathParam> pathParams) {
    Queue<PathParam> q = new LinkedList<>();
    for (PathParam pathParam : pathParams) {
      q.add(pathParam);
    }
    var count = 0;
    while (!q.isEmpty()) {
      var pathParam = q.remove();

      if (pathParam.getParamName() != null) {
        count++;
      } else {
        for (PathParam subParam : pathParam.getSubParams()) {
          q.add(subParam);
        }
      }
    }
    return count;
  }

  public static String getAction(Method actionMethod) {
    var action = actionMethod.getDeclaredAnnotation(Action.class);
    if (action == null) {
      return actionMethod.getName();
    }
    var route = StringUtils.trimToNull(action.value());
    Asserts.notNull(route, "Invalid action={}.", actionMethod);
    return StringUtils.firstLowerCase(route, Locale.ENGLISH);
  }

  public static String getController(Class<?> clazz) {
    var controller = clazz.getDeclaredAnnotation(Controller.class);
    if (controller != null) {
      var route = StringUtils.trimToNull(controller.value());
      Asserts.notNull(route, "Invalid controller={}.", clazz);
      return route;
    }

    var className = clazz.getSimpleName();
    Asserts.isTrue(StringUtils.endsWithIgnoreCase(className, "Controller"), "Invalid controller={}.", clazz);

    var route = className.substring(0, clazz.getSimpleName().length() - 10);
    return StringUtils.firstLowerCase(route, Locale.ENGLISH);
  }
}
