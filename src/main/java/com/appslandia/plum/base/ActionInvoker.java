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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.appslandia.common.base.Out;
import com.appslandia.common.converters.Converter;
import com.appslandia.common.converters.ConverterProvider;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ExceptionUtils;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class ActionInvoker {

    @Inject
    protected ConverterProvider converterProvider;

    @Inject
    protected ModelBinder modelBinder;

    @Inject
    protected JsonProcessor jsonProcessor;

    public Object invokeAction(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext, Object controller) throws Exception {
	List<ParamDesc> paramDescs = requestContext.getActionDesc().getParamDescs();
	Object[] actionArgs = new Object[paramDescs.size()];

	for (int index = 0; index < actionArgs.length; index++) {
	    ParamDesc paramDesc = paramDescs.get(index);

	    if (paramDesc.getParameter().getType() == HttpServletRequest.class) {
		actionArgs[index] = request;
		continue;
	    }
	    if (paramDesc.getParameter().getType() == HttpServletResponse.class) {
		actionArgs[index] = response;
		continue;
	    }
	    if (paramDesc.getParameter().getType() == RequestAccessor.class) {
		actionArgs[index] = new RequestAccessor(request);
		continue;
	    }
	    if (paramDesc.getParameter().getType() == RequestContext.class) {
		actionArgs[index] = requestContext;
		continue;
	    }
	    if (paramDesc.getParameter().getType() == ModelState.class) {
		actionArgs[index] = ServletUtils.getModelState(request);
		continue;
	    }
	    // @Model
	    if (paramDesc.getModel() != null) {
		Object model = null;
		if (paramDesc.getModel().value() == Model.Source.PARAMETER) {
		    model = ReflectionUtils.newInstance(paramDesc.getParameter().getType());
		    String[] excludes = paramDesc.getModel().excludes();
		    if (excludes.length == 0) {
			this.modelBinder.bindModel(request, model, ServletUtils.getModelState(request));
		    } else {
			this.modelBinder.bindModel(request, model, ServletUtils.getModelState(request), p -> Arrays.stream(excludes).anyMatch(path -> p.equals(path)));
		    }
		} else {
		    model = this.jsonProcessor.read(request.getReader(), paramDesc.getParameter().getType());
		    if (model != null) {
			this.modelBinder.validateModel(model, ServletUtils.getModelState(request), requestContext.getResources());
		    }
		}
		actionArgs[index] = model;
		continue;
	    }

	    // Converter
	    Class<?> valueType = ModelBinder.getValueType(paramDesc.getParameter());
	    Converter<Object> converter = (paramDesc.getConverter() != null) ? this.converterProvider.getConverter(paramDesc.getConverter())
		    : this.converterProvider.getConverter(valueType);

	    // paramValue
	    String paramValue = request.getParameter(paramDesc.getParamName());
	    if (StringUtils.isNullOrEmpty(paramValue)) {
		paramValue = paramDesc.getDefaultValue();
	    }
	    Out<String> msgKey = new Out<>();
	    Object parsedValue = ModelBinder.parseValue(paramValue, valueType, msgKey, converter, requestContext.getFormatProvider());
	    actionArgs[index] = (paramDesc.getParameter().getType() != Out.class) ? parsedValue : new Out<Object>(parsedValue);

	    if (msgKey.value != null) {
		Map<String, Object> msgParams = getMsgParams(paramDesc, requestContext.getResources());
		ServletUtils.addError(request, paramDesc.getParamName(), msgKey.value, msgParams);
	    }
	    if (paramDesc.isPathParam()) {
		if ((parsedValue == null) || (msgKey.value != null)) {
		    throw new NotFoundException(requestContext.res(Resources.ERROR_NOT_FOUND)).setTitleKey(Resources.ERROR_NOT_FOUND);
		}
	    }
	}
	// Invoke Action
	try {
	    return requestContext.getActionDesc().getMethod().invoke(controller, actionArgs);
	} catch (Exception ex) {
	    Exception he = (ex instanceof InvocationTargetException) ? ExceptionUtils.tryUnwrap((InvocationTargetException) ex) : ex;

	    if (he instanceof ConstraintViolationException) {
		handleConstraintViolationException(request, requestContext, (ConstraintViolationException) he);
	    }
	    throw he;
	}
    }

    protected void handleConstraintViolationException(HttpServletRequest request, RequestContext requestContext, ConstraintViolationException ex) throws Exception {
	boolean isPathParamError = false;

	for (Object error : ex.getConstraintViolations()) {
	    ConstraintViolation<?> violation = (ConstraintViolation<?>) error;

	    Path.Node paramNode = getParamNode(violation.getPropertyPath());
	    Asserts.notNull(paramNode);
	    ParamDesc paramDesc = requestContext.getActionDesc().getParamDescs().stream().filter(p -> p.getParameter().getName().equals(paramNode.getName())).findFirst().get();

	    // Add Error
	    String msgKey = ModelBinder.getMsgKey(violation);
	    Map<String, Object> msgParams = getMsgParams(violation, paramDesc, requestContext.getResources());
	    ServletUtils.addError(request, paramDesc.getParamName(), msgKey, msgParams);

	    if (paramDesc.isPathParam()) {
		isPathParamError = true;
	    }
	}
	if (isPathParamError) {
	    throw new NotFoundException(requestContext.res(Resources.ERROR_NOT_FOUND), ex).setTitleKey(Resources.ERROR_NOT_FOUND);
	} else {
	    throw new BadRequestException(requestContext.res(Resources.ERROR_BAD_REQUEST), ex).setTitleKey(Resources.ERROR_BAD_REQUEST);
	}
    }

    protected String getDisplayName(ParamDesc paramDesc, Resources resources) {
	return paramDesc.getParamName();
    }

    private Map<String, Object> getMsgParams(ParamDesc paramDesc, Resources resources) {
	Map<String, Object> params = new HashMap<>();
	params.put(Resources.PARAM_FIELD_DN, getDisplayName(paramDesc, resources));
	return params;
    }

    private Map<String, Object> getMsgParams(ConstraintViolation<?> violation, ParamDesc paramDesc, Resources resources) {
	Map<String, Object> params = ModelBinder.buildMsgParams(violation.getConstraintDescriptor());
	params.put(Resources.PARAM_FIELD_DN, getDisplayName(paramDesc, resources));
	return params;
    }

    private static Path.Node getParamNode(Path path) {
	Iterator<Path.Node> iter = path.iterator();
	while (iter.hasNext()) {
	    Path.Node node = iter.next();
	    if (node.getKind() == ElementKind.PARAMETER) {
		return node;
	    }
	}
	return null;
    }
}
