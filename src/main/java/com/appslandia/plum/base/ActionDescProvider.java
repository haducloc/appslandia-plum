// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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

import com.appslandia.common.base.Bind;
import com.appslandia.common.base.CaseInsensitiveMap;
import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.base.Out;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.STR;
import com.appslandia.common.utils.SplitOptions;
import com.appslandia.common.utils.SplitUtils;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.common.utils.ValueUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
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

    protected void addControllerClass(Class<?> controllerClass) {
	String controller = getController(controllerClass);

	// Actions
	for (Method actionMethod : controllerClass.getMethods()) {
	    if (actionMethod.getDeclaringClass() == Object.class) {
		continue;
	    }
	    if (Modifier.isStatic(actionMethod.getModifiers())) {
		continue;
	    }
	    String action = getAction(actionMethod);

	    // ActionDesc
	    ActionDesc actionDesc = new ActionDesc();
	    actionDesc.setController(controller);
	    actionDesc.setAction(action);
	    actionDesc.setMethod(actionMethod);
	    actionDesc.setControllerClass(controllerClass);

	    // Module
	    Controller controllerAnt = controllerClass.getDeclaredAnnotation(Controller.class);
	    String module = controllerAnt.module().isEmpty() ? this.appConfig.getStringReq(AppConfig.CONFIG_DEFAULT_MODULE) : controllerAnt.module();
	    actionDesc.setModule(module);

	    // HTTP Methods
	    Out<Boolean> httpMethod = new Out<>();
	    List<String> allowMethods = parseAllowMethods(actionMethod, httpMethod);

	    // @ChildAction
	    ChildAction childAction = actionMethod.getDeclaredAnnotation(ChildAction.class);
	    if (!httpMethod.value && (childAction == null)) {
		continue;
	    }
	    actionDesc.setChildAction(childAction);

	    // PathParams
	    PathParams pathParams = actionMethod.getDeclaredAnnotation(PathParams.class);
	    List<PathParam> parsedPathParams = (pathParams != null) ? parsePathParams(pathParams.value()) : null;
	    actionDesc.setPathParams(CollectionUtils.unmodifiable(parsedPathParams));

	    // ParamDescs
	    List<ParamDesc> parsedParamDescs = parseParamDescs(actionMethod, actionDesc.getPathParams());
	    actionDesc.setParamDescs(CollectionUtils.unmodifiable(parsedParamDescs));

	    // Accessible?
	    if (actionDesc.getChildAction() == null) {
		actionDesc.setAllowMethods(CollectionUtils.unmodifiable(allowMethods));

		// @ConsumeType
		if (parsedParamDescs.stream().anyMatch(p -> (p.getModel() != null) && p.getModel().value() == Model.Source.JSON_BODY)) {
		    actionDesc.setConsumeType(ConsumeType.IMPL);
		} else {
		    ConsumeType consumeType = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(ConsumeType.class),
			    controllerClass.getDeclaredAnnotation(ConsumeType.class));
		    actionDesc.setConsumeType(consumeType);
		}

		// @EnableFilters
		EnableFilters enableFilters = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(EnableFilters.class),
			controllerClass.getDeclaredAnnotation(EnableFilters.class));
		actionDesc.setEnableFilters(enableFilters);

		// @Authorize
		Authorize authorize = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(Authorize.class), controllerClass.getDeclaredAnnotation(Authorize.class));
		if (authorize == null) {
		    if (this.appConfig.getBool(AppConfig.CONFIG_ENABLE_AUTHORIZE, false)) {
			authorize = Authorize.IMPL;
		    }
		}
		actionDesc.setAuthorize(((authorize != null) && !authorize.removed()) ? authorize : null);

		// @CacheControl
		CacheControl cacheControl = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(CacheControl.class),
			controllerClass.getDeclaredAnnotation(CacheControl.class));
		if (cacheControl != null) {
		    if (!cacheControl.nocache()) {
			actionDesc.setCacheControl(cacheControl);
		    } else {
			actionDesc.setCacheControl(CacheControl.IMPL_NO_CACHE);
		    }
		}

		// @EnableCors
		EnableCors enableCors = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(EnableCors.class), controllerClass.getDeclaredAnnotation(EnableCors.class));
		if (enableCors == null) {
		    if (this.appConfig.getBool(AppConfig.CONFIG_ENABLE_CORS, false)) {
			enableCors = EnableCors.IMPL;
		    }
		}
		actionDesc.setEnableCors(((enableCors != null) && !enableCors.removed()) ? enableCors : null);

		// @EnableGzip
		if (!this.appConfig.getBool(AppConfig.CONFIG_DISABLE_GZIP, true)) {
		    EnableGzip enableGzip = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(EnableGzip.class), controllerClass.getDeclaredAnnotation(EnableGzip.class));
		    actionDesc.setEnableGzip(((enableGzip != null) && !enableGzip.removed()) ? enableGzip : null);
		}

		// @EnableParts
		EnableParts enableParts = actionMethod.getDeclaredAnnotation(EnableParts.class);
		actionDesc.setEnableParts(enableParts);

		// @EnableEtag
		EnableEtag enableEtag = actionMethod.getDeclaredAnnotation(EnableEtag.class);
		actionDesc.setEnableEtag(enableEtag);

		// @EnableCsrf
		EnableCsrf enableCsrf = actionMethod.getDeclaredAnnotation(EnableCsrf.class);
		actionDesc.setEnableCsrf(enableCsrf);

		// @EnableCaptcha
		EnableCaptcha enableCaptcha = actionMethod.getDeclaredAnnotation(EnableCaptcha.class);
		actionDesc.setEnableCaptcha(enableCaptcha);

		// @EnableJsonError
		EnableJsonError enableJsonError = ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(EnableJsonError.class),
			controllerClass.getDeclaredAnnotation(EnableJsonError.class));
		if (enableJsonError == null) {
		    if (this.appConfig.getBool(AppConfig.CONFIG_ENABLE_JSON_ERROR, false)) {
			enableJsonError = EnableJsonError.IMPL;
		    }
		}
		actionDesc.setEnableJsonError(((enableJsonError != null) && !enableJsonError.removed()) ? enableJsonError : null);

		if (actionDesc.getEnableJsonError() == null) {
		    if (!ActionDescUtils.isActionResultOrVoid(actionMethod)) {

			actionDesc.setEnableJsonError(EnableJsonError.IMPL);
		    }
		}
	    }

	    // @Removed
	    if (ValueUtils.valueOrAlt(actionMethod.getDeclaredAnnotation(Removed.class), controllerClass.getDeclaredAnnotation(Removed.class)) != null) {
		continue;
	    }

	    // ActionRoute
	    ActionRoute actionRoute = new ActionRoute(controller, action);
	    Asserts.isTrue(!this.actionDescMap.containsKey(actionRoute),
		    () -> STR.fmt("controller/action is duplicated: controller={}, action={}.", controllerClass, actionMethod));

	    // Index
	    if (action.equalsIgnoreCase(ServletUtils.ACTION_INDEX)) {
		// @Home
		if (controllerClass.getAnnotation(Home.class) != null) {
		    Asserts.isNull(this.homeController, () -> STR.fmt("@Home is duplicated: controller={}.", controllerClass));
		    this.homeController = controller;
		}
	    }

	    // @FormLogin
	    if (actionMethod.getDeclaredAnnotation(FormLogin.class) != null) {
		Asserts.isTrue(!this.formLogins.containsKey(actionDesc.getModule()), "@FormLogin is duplicated.");
		this.formLogins.put(actionDesc.getModule(), actionDesc);
	    }
	    this.actionDescMap.put(actionRoute, actionDesc);
	}
    }

    public static List<ParamDesc> parseParamDescs(Method actionMethod, List<PathParam> pathParams) {
	List<ParamDesc> paramDescs = new ArrayList<>(actionMethod.getParameterCount());
	for (Parameter parameter : actionMethod.getParameters()) {
	    ParamDesc paramDesc = new ParamDesc();
	    paramDesc.setParameter(parameter);
	    paramDescs.add(paramDesc);

	    // HttpServletRequest | HttpServletResponse
	    if ((parameter.getType() == HttpServletRequest.class) || (parameter.getType() == HttpServletResponse.class)) {
		continue;
	    }

	    // RequestAccessor | RequestContext
	    if ((parameter.getType() == RequestAccessor.class) || (parameter.getType() == RequestContext.class)) {
		continue;
	    }

	    // ModelState
	    if (parameter.getType() == ModelState.class) {
		continue;
	    }

	    // @Model
	    Model model = parameter.getDeclaredAnnotation(Model.class);
	    if (model != null) {
		paramDesc.setModel(model);

		Asserts.isNull(parameter.getDeclaredAnnotation(Valid.class), () -> STR.fmt("@Valid is unsupported at this location: {}.", parameter));
		continue;
	    }

	    // @Bind
	    Bind bind = parameter.getDeclaredAnnotation(Bind.class);
	    paramDesc.setBind(bind);

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

    private static boolean parseAllowMethod(Method method, Class<? extends Annotation> httpMethodAntClass, Set<String> allow) {
	if (method.getDeclaredAnnotation(httpMethodAntClass) == null) {
	    return false;
	}
	if (httpMethodAntClass == HttpGetPost.class) {
	    CollectionUtils.toSet(allow, HttpMethod.GET, HttpMethod.POST);
	    return true;
	}
	HttpMethod ant = httpMethodAntClass.getDeclaredAnnotation(HttpMethod.class);
	allow.add(ant.value());
	return true;
    }

    private static final String PARAM_FORMAT = "\\{[a-z\\d._]+}";
    private static final Pattern PARAM_PATTERN = Pattern.compile(PARAM_FORMAT, Pattern.CASE_INSENSITIVE);
    private static final Pattern PATH_PARAMS_PATTERN = Pattern.compile(String.format("(/%s(-%s)*)+", PARAM_FORMAT, PARAM_FORMAT), Pattern.CASE_INSENSITIVE);

    // value: (/{parameter}(-{parameter})*)+
    public static List<PathParam> parsePathParams(String value) {
	Asserts.notNull(value);
	Asserts.isTrue(PATH_PARAMS_PATTERN.matcher(value).matches(), () -> STR.fmt("pathParams '{}' is invalid.", value));

	List<PathParam> pathParams = new ArrayList<>();
	String[] parts = SplitUtils.split(value, '/', SplitOptions.NONE);

	for (String pathItem : parts) {
	    if (pathItem.isEmpty()) {
		continue;
	    }
	    // pathItem: {parameter}
	    if (PARAM_PATTERN.matcher(pathItem).matches()) {
		pathParams.add(new PathParam(pathItem.substring(1, pathItem.length() - 1)));
	    } else {
		// pathItem: {parameter}(-{parameter})+
		List<PathParam> parsedSubParams = parseSubParams(pathItem);
		pathParams.add(new PathParam(Collections.unmodifiableList(parsedSubParams)));
	    }
	}
	return pathParams;
    }

    // value: {parameter}(-{parameter})+
    public static List<PathParam> parseSubParams(String value) {
	List<PathParam> subParams = new ArrayList<>();
	String[] parts = SplitUtils.split(value, '-', SplitOptions.NONE);

	for (String subParam : parts) {

	    // subParam: {parameter}
	    subParams.add(new PathParam(subParam.substring(1, subParam.length() - 1)));
	}
	return subParams;
    }

    public static int getPathParamCount(List<PathParam> pathParams) {
	Queue<PathParam> q = new LinkedList<>();
	for (PathParam pathParam : pathParams) {
	    q.add(pathParam);
	}
	int count = 0;
	while (!q.isEmpty()) {
	    PathParam pathParam = q.remove();
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
	Action action = actionMethod.getDeclaredAnnotation(Action.class);
	if (action == null) {
	    return actionMethod.getName();
	}
	String route = StringUtils.trimToNull(action.value());
	Asserts.notNull(route, () -> STR.fmt("Couldn't determime action route: action={}.", actionMethod));
	return StringUtils.firstLowerCase(route, Locale.ENGLISH);
    }

    public static String getController(Class<?> clazz) {
	Controller controller = clazz.getDeclaredAnnotation(Controller.class);
	Asserts.notNull(controller, () -> STR.fmt("@Controller is required: controller={}.", clazz));

	String route = StringUtils.trimToNull(controller.value());
	if (route == null) {
	    Asserts.isTrue(StringUtils.endsWith(clazz.getSimpleName(), "Controller"), () -> STR.fmt("Couldn't determime controller route: controller={}.", clazz));

	    route = clazz.getSimpleName().substring(0, clazz.getSimpleName().length() - 10);
	    Asserts.isTrue(!route.isEmpty(), () -> STR.fmt("Controller is invalid: controller={}.", clazz));
	}
	return StringUtils.firstLowerCase(route, Locale.ENGLISH);
    }
}
