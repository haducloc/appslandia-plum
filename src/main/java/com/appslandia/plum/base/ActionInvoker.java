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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import com.appslandia.common.base.Out;
import com.appslandia.common.converters.ConverterProvider;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ExceptionUtils;
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
import jakarta.validation.Path.Node;
import jakarta.validation.groups.Default;

/**
 *
 * @author Loc Ha
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

  public Object invokeAction(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext,
      Object controller) throws Exception {

    var paramDescs = requestContext.getActionDesc().getParamDescs();
    var actionArgs = new Object[paramDescs.size()];

    for (var index = 0; index < actionArgs.length; index++) {
      var paramDesc = paramDescs.get(index);
      var paramType = paramDesc.getParameter().getType();

      // Known Types
      if (paramType == RequestWrapper.class || paramType == HttpServletRequest.class) {
        actionArgs[index] = request;
        continue;
      }
      if (paramType == HttpServletResponse.class) {
        actionArgs[index] = response;
        continue;
      }
      if (paramType == RequestContext.class) {
        actionArgs[index] = requestContext;
        continue;
      }
      if (paramType == ModelState.class) {
        actionArgs[index] = ServletUtils.getModelState(request);
        continue;
      }

      // @BindModel
      if (paramDesc.getBindModel() != null) {
        Object model = null;
        if (paramDesc.getBindModel().value() == ModelSource.PARAM) {
          model = paramType.getDeclaredConstructor().newInstance();

          var excl = paramDesc.getBindModel().excl();
          if (excl.length == 0) {
            this.modelBinder.bindModel(request, model);
          } else {
            this.modelBinder.bindModel(request, model, p -> Arrays.stream(excl).anyMatch(path -> p.equals(path)));
          }

        } else {
          // JSON
          model = this.jsonProcessor.read(request.getReader(), paramType);
          if (model != null) {
            this.modelBinder.validateModel(model, Default.class, ServletUtils.getModelState(request),
                requestContext.getResources());
          }
        }
        actionArgs[index] = model;
        continue;
      }

      // Converter
      Class<?> valueType = ModelBinder.getValueType(paramDesc.getParameter());
      var converter = (paramDesc.getConverter() != null) ? this.converterProvider.getConverter(paramDesc.getConverter())
          : this.converterProvider.getConverter(valueType);

      // paramValue
      var paramValue = request.getParameter(paramDesc.getParamName());
      if (StringUtils.isNullOrEmpty(paramValue)) {
        paramValue = paramDesc.getDefval();
      }

      var msgKey = new Out<String>();
      var parsedValue = ModelBinder.parseValue(paramValue, valueType, msgKey, converter,
          requestContext.getFormatProvider());
      actionArgs[index] = (paramType != Out.class) ? parsedValue : new Out<>(parsedValue);

      // Handle error
      if (msgKey.value != null) {
        var msgParams = getMsgParams(paramDesc, requestContext.getResources());
        ServletUtils.addError(request, paramDesc.getParamName(), msgKey.value, msgParams);
      }

      if (paramDesc.isPathParam()) {
        if ((parsedValue == null) || (msgKey.value != null)) {
          throw new NotFoundException(requestContext.res(Resources.ERROR_NOT_FOUND))
              .setTitleKey(Resources.ERROR_NOT_FOUND);
        }
      }
    }

    // Invoke Action
    try {
      return requestContext.getActionDesc().getMethod().invoke(controller, actionArgs);
    } catch (Exception ex) {
      var he = (ex instanceof InvocationTargetException ite) ? ExceptionUtils.tryUnwrap(ite) : ex;

      if (he instanceof ConstraintViolationException) {
        handleConstraintViolationException(request, requestContext, (ConstraintViolationException) he);
      }
      throw he;
    }
  }

  protected void handleConstraintViolationException(HttpServletRequest request, RequestContext requestContext,
      ConstraintViolationException ex) throws Exception {
    var isPathParamError = false;

    for (Object error : ex.getConstraintViolations()) {
      ConstraintViolation<?> violation = (ConstraintViolation<?>) error;

      // paramNode
      var paramNode = getParamNode(violation.getPropertyPath());
      Asserts.notNull(paramNode);

      // paramDesc
      var paramDesc = requestContext.getActionDesc().getParamDescs().stream()
          .filter(p -> p.getParameter().getName().equals(paramNode.getName())).findFirst().get();

      // Add Error
      var msgKey = ModelBinder.getMsgKey(violation);
      var msgParams = getMsgParams(paramDesc, requestContext.getResources());
      ServletUtils.addError(request, paramDesc.getParamName(), msgKey, msgParams);

      if (paramDesc.isPathParam()) {
        isPathParamError = true;
      }
    }
    if (isPathParamError) {
      throw new NotFoundException(requestContext.res(Resources.ERROR_NOT_FOUND), ex)
          .setTitleKey(Resources.ERROR_NOT_FOUND);
    } else {
      throw new BadRequestException(requestContext.res(Resources.ERROR_BAD_REQUEST), ex)
          .setTitleKey(Resources.ERROR_BAD_REQUEST);
    }
  }

  protected String getDisplayName(ParamDesc paramDesc, Resources resources) {
    var param = paramDesc.getBindValue();
    if (param == null || param.res().isEmpty()) {
      return paramDesc.getParamName();
    }
    return resources.get(param.res());
  }

  private Object[] getMsgParams(ParamDesc paramDesc, Resources resources) {
    var displayName = getDisplayName(paramDesc, resources);
    return new Object[] { displayName };
  }

  private static Path.Node getParamNode(Path path) {
    for (Node node : path) {
      if (node.getKind() == ElementKind.PARAMETER) {
        return node;
      }
    }
    return null;
  }
}
