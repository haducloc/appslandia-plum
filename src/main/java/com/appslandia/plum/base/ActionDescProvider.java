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
import java.lang.reflect.Modifier;
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
import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.base.Out;
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
public abstract class ActionDescProvider extends InitializeObject {

  private Map<ActionRoute, ActionDesc> actionDescMap = new HashMap<>();
  private String homeController;

  private Map<String, ActionDesc> formLogins = new CaseInsensitiveMap<>();

  @Inject
  protected AppConfig appConfig;

  @Override
  protected void init() throws Exception {
    this.actionDescMap = Collections.unmodifiableMap(this.actionDescMap);
    this.formLogins = Collections.unmodifiableMap(this.formLogins);
  }

  public Map<ActionRoute, ActionDesc> getActionDescMap() {
    this.initialize();
    return this.actionDescMap;
  }

  public ActionDesc getActionDesc(String controller, String action) {
    this.initialize();
    return this.actionDescMap.get(new ActionRoute(controller, action));
  }

  public ActionDesc getHomeDesc() {
    this.initialize();
    if (this.homeController == null) {
      return null;
    }
    return this.actionDescMap.get(new ActionRoute(this.homeController, ServletUtils.ACTION_INDEX));
  }

  public String getHomeController() {
    this.initialize();
    return this.homeController;
  }

  public ActionDesc getFormLogin(String module) {
    this.initialize();
    return this.formLogins.get(module);
  }

  protected void registerController(Class<?> controllerClass) {
    var controller = getController(controllerClass);

    // Actions
    for (Method actionMethod : controllerClass.getMethods()) {
      if ((actionMethod.getDeclaringClass() == Object.class) || Modifier.isStatic(actionMethod.getModifiers())) {
        continue;
      }
      var action = getAction(actionMethod);

      // ActionDesc
      var actionDesc = new ActionDesc();
      actionDesc.setController(controller);
      actionDesc.setAction(action);
      actionDesc.setMethod(actionMethod);
      actionDesc.setControllerClass(controllerClass);

      // Module
      var module = controllerClass.getDeclaredAnnotation(Module.class);
      var moduleId = ((module == null) || module.value().isEmpty())
          ? this.appConfig.getStringReq(AppConfig.CONFIG_DEFAULT_MODULE)
          : module.value();
      actionDesc.setModule(moduleId);

      // HTTP Methods
      var httpMethod = new Out<Boolean>();
      var allowMethods = parseAllowMethods(actionMethod, httpMethod);
      actionDesc.setAllowMethods(CollectionUtils.unmodifiable(allowMethods));

      // PathParams
      var pathParams = actionMethod.getDeclaredAnnotation(PathParams.class);
      var parsedPathParams = (pathParams != null) ? parsePathParams(pathParams.value()) : null;
      actionDesc.setPathParams(CollectionUtils.unmodifiable(parsedPathParams));

      // ParamDescs
      var parsedParamDescs = parseParamDescs(actionMethod, actionDesc.getPathParams());
      actionDesc.setParamDescs(CollectionUtils.unmodifiable(parsedParamDescs));

      // @ConsumeType
      if (parsedParamDescs.stream()
          .anyMatch(p -> (p.getBindModel() != null) && p.getBindModel().value() == ModelSource.JSON_BODY)) {
        actionDesc.setConsumeType(ConsumeType.APP_JSON);
      } else {
        var consumeType = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(ConsumeType.class),
            controllerClass.getDeclaredAnnotation(ConsumeType.class));
        actionDesc.setConsumeType(consumeType);
      }

      // @EnableFilters
      var enableFilters = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(EnableFilters.class),
          controllerClass.getDeclaredAnnotation(EnableFilters.class));
      actionDesc.setEnableFilters(enableFilters);

      // @Authorize
      var authorize = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(Authorize.class),
          controllerClass.getDeclaredAnnotation(Authorize.class));
      actionDesc.setAuthorize(authorize);

      // @CacheControl
      var cacheControl = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(CacheControl.class),
          controllerClass.getDeclaredAnnotation(CacheControl.class));
      if (cacheControl != null) {
        if (cacheControl.nocache()) {
          actionDesc.setCacheControl(CacheControl.NO_CACHE);
        } else {
          actionDesc.setCacheControl(cacheControl);
        }
      }

      // @EnableCors
      var enableCors = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(EnableCors.class),
          controllerClass.getDeclaredAnnotation(EnableCors.class));
      actionDesc.setEnableCors(enableCors);

      // @EnableEncoding
      if (!this.appConfig.getBool(AppConfig.CONFIG_DISABLE_ENCODING)) {
        var enableEncoding = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(EnableEncoding.class),
            controllerClass.getDeclaredAnnotation(EnableEncoding.class));
        actionDesc.setEnableEncoding(enableEncoding);
      }

      // @EnableParts
      var enableParts = actionMethod.getDeclaredAnnotation(EnableParts.class);
      actionDesc.setEnableParts(enableParts);

      // @EnableAsync
      var enableAsync = actionMethod.getDeclaredAnnotation(EnableAsync.class);
      actionDesc.setEnableAsync(enableAsync);

      // @EnableEtag
      var enableEtag = actionMethod.getDeclaredAnnotation(EnableEtag.class);
      actionDesc.setEnableEtag(enableEtag);

      // @EnableCsrf
      var enableCsrf = actionMethod.getDeclaredAnnotation(EnableCsrf.class);
      actionDesc.setEnableCsrf(enableCsrf);

      // @EnableCaptcha
      var enableCaptcha = actionMethod.getDeclaredAnnotation(EnableCaptcha.class);
      actionDesc.setEnableCaptcha(enableCaptcha);

      // @EnableJsonError
      var enableJsonError = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(EnableJsonError.class),
          controllerClass.getDeclaredAnnotation(EnableJsonError.class));

      if ((enableJsonError == null) && !ActionDescUtils.isActionResultOrVoid(actionMethod)) {
        enableJsonError = EnableJsonError.IMPL;
      }
      actionDesc.setEnableJsonError(enableJsonError);

      // @BypassAuthorization
      var bypassAuthorization = actionMethod.getDeclaredAnnotation(BypassAuthorization.class);
      actionDesc.setBypassAuthorization(bypassAuthorization);

      // @Removed
      if (ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(Removed.class),
          controllerClass.getDeclaredAnnotation(Removed.class)) != null) {
        continue;
      }

      // ActionRoute
      var actionRoute = new ActionRoute(controller, action);
      Asserts.isTrue(!this.actionDescMap.containsKey(actionRoute),
          "controller/action is already registered: controller={}, action={}.", controllerClass, actionMethod);

      // Index
      if (action.equalsIgnoreCase(ServletUtils.ACTION_INDEX)) {
        // @Home
        if (controllerClass.getAnnotation(Home.class) != null) {
          Asserts.isNull(this.homeController, "@Home is already registered: controller={}.", controllerClass);
          this.homeController = controller;
        }
      }

      // @FormLogin
      if (actionMethod.getDeclaredAnnotation(FormLogin.class) != null) {
        Asserts.isTrue(!this.formLogins.containsKey(actionDesc.getModule()),
            "@FormLogin is already registered: module={}.", actionDesc.getModule());
        this.formLogins.put(actionDesc.getModule(), actionDesc);
      }
      this.actionDescMap.put(actionRoute, actionDesc);
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

  public static List<String> parseAllowMethods(Method method, Out<Boolean> httpMethod) {
    Set<String> allow = new LinkedHashSet<>();

    if (parseAllowMethod(method, HttpGet.class, allow)) {
      allow.add(HttpMethod.HEAD);
    }
    if (parseAllowMethod(method, HttpGetPost.class, allow)) {
      allow.add(HttpMethod.HEAD);
    }

    parseAllowMethod(method, HttpPost.class, allow);
    parseAllowMethod(method, HttpPut.class, allow);
    parseAllowMethod(method, HttpDelete.class, allow);
    parseAllowMethod(method, HttpPatch.class, allow);

    httpMethod.value = (allow.size() > 0);

    if (method.getDeclaredAnnotation(EnableCors.class) != null) {
      allow.add(HttpMethod.OPTIONS);
    }
    return new ArrayList<>(allow);
  }

  private static boolean parseAllowMethod(Method method, Class<? extends Annotation> httpMethodAntClass,
      Set<String> allow) {
    if (method.getDeclaredAnnotation(httpMethodAntClass) == null) {
      return false;
    }
    if (httpMethodAntClass == HttpGetPost.class) {
      CollectionUtils.toSet(allow, HttpMethod.GET, HttpMethod.POST);
      return true;
    }
    var ant = httpMethodAntClass.getDeclaredAnnotation(HttpMethod.class);
    allow.add(ant.value());
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

    for (String subParam : params) {

      // subParam: {parameter}
      pars.add(new PathParam(subParam.substring(1, subParam.length() - 1)));
    }
    return pars;
  }

  public static int getPathParamCount(List<PathParam> pathParams) {
    Queue<PathParam> que = new LinkedList<>();
    for (PathParam pathParam : pathParams) {
      que.add(pathParam);
    }
    var count = 0;
    while (!que.isEmpty()) {
      var pathParam = que.remove();

      if (pathParam.getParamName() != null) {
        count++;
      } else {
        for (PathParam subParam : pathParam.getSubParams()) {
          que.add(subParam);
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
    Asserts.notNull(route, "Couldn't determime action route: action={}.", actionMethod);
    return StringUtils.firstLowerCase(route, Locale.ENGLISH);
  }

  public static String getController(Class<?> clazz) {
    var controller = clazz.getDeclaredAnnotation(Controller.class);
    Asserts.notNull(controller, "@Controller is required: controller={}.", clazz);

    var route = StringUtils.trimToNull(controller.value());
    if (route == null) {
      Asserts.isTrue(StringUtils.endsWith(clazz.getSimpleName(), "Controller"),
          "Couldn't determime controller route: controller={}.", clazz);

      route = clazz.getSimpleName().substring(0, clazz.getSimpleName().length() - 10);
    }
    return StringUtils.firstLowerCase(route, Locale.ENGLISH);
  }
}
