// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.appslandia.common.base.Out;
import com.appslandia.common.converters.ConverterProvider;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ExceptionUtils;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.common.utils.STR;
import com.appslandia.plum.utils.ParamUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
      if (paramType == HttpRequestFacade.class || paramType == HttpServletRequest.class) {
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
        if (paramDesc.getBindModel().value() == BindSource.PARAM) {
          model = paramType.getDeclaredConstructor().newInstance();

          var excl = paramDesc.getBindModel().exclude();
          if (excl.length == 0) {
            modelBinder.bindModel(request, model);
          } else {
            modelBinder.bindModel(request, model, p -> Arrays.stream(excl).anyMatch(path -> p.equals(path)));
          }

        } else {
          // JSON
          Asserts.isTrue(ServletUtils.isJsonMimeType(request.getContentType()));

          model = jsonProcessor.read(request.getReader(), paramType);
          if (model != null) {
            modelBinder.validateModel(model, Default.class, ServletUtils.getModelState(request),
                requestContext.getResources());
          }
        }
        actionArgs[index] = model;
        continue;
      }

      // Converter
      Class<?> valueType = getValueType(paramDesc.getParameter());
      var converter = (paramDesc.getConverter() != null) ? converterProvider.getConverter(paramDesc.getConverter())
          : converterProvider.getConverter(valueType);

      var paramValue = ParamUtils.getString(request, paramDesc.getParamName(), paramDesc.getDefaultValue());
      var msgKey = new Out<String>();

      var parsedValue = ModelBinder.parseValue(paramValue, valueType, msgKey, converter,
          requestContext.getFormatProvider());
      actionArgs[index] = (paramType != Out.class) ? parsedValue : new Out<>(parsedValue);

      // Handle conversion error
      if (msgKey.value != null) {
        var msgParams = getMsgParams(paramDesc, requestContext.getResources());
        var resolvedMsg = requestContext.res(msgKey.value, msgParams);
        ServletUtils.getModelState(request).addError(paramDesc.getParamName(), resolvedMsg);

        if (paramDesc.isPathParam()) {
          throw new NotFoundException("Invalid path parameter: " + paramDesc.getParamName(), Resources.ERROR_NOT_FOUND,
              null);
        } else {
          // Skip BadRequestException caused by parameter type conversion.
        }
      }
    }

    // Invoke Action
    try {
      return requestContext.getActionDesc().getMethod().invoke(controller, actionArgs);
    } catch (Exception ex) {
      var he = (ex instanceof InvocationTargetException ite) ? ExceptionUtils.tryUnwrap(ite) : ex;

      if (he instanceof ConstraintViolationException cve) {
        handleConstraintViolationException(request, requestContext, cve);
      }
      throw he;
    }
  }

  protected void handleConstraintViolationException(HttpServletRequest request, RequestContext requestContext,
      ConstraintViolationException cve) throws Exception {
    Set<String> pathParams = null;
    Map<String, List<String>> errorMap = null;

    for (var violation : cve.getConstraintViolations()) {
      if (!(violation.getRootBean() instanceof ControllerBase)) {
        throw cve;
      }

      var paramNode = getParamNode(violation.getPropertyPath());
      if (paramNode == null) {
        throw cve;
      }

      var paramDesc = requestContext.getActionDesc().getParamDescs().stream()
          .filter(p -> p.getParameter().getName().equals(paramNode.getName())).findFirst().orElse(null);
      Asserts.notNull(paramDesc);

      var msgKey = ModelBinder.getMsgKey(violation);
      var msgParams = getMsgParams(paramDesc, requestContext.getResources());
      var resolvedMsg = requestContext.res(msgKey, msgParams);
      ServletUtils.getModelState(request).addError(paramDesc.getParamName(), resolvedMsg);

      if (paramDesc.isPathParam()) {
        if (pathParams == null) {
          pathParams = new LinkedHashSet<>();
        }
        pathParams.add(paramDesc.getParamName());
      } else {
        if (errorMap == null) {
          errorMap = new LinkedHashMap<>();
        }
        errorMap.computeIfAbsent(paramDesc.getParamName(), p -> new ArrayList<>()).add(resolvedMsg);
      }
    }

    if (pathParams != null) {
      var devMessage = STR.fmt("Path parameters are invalid: {}", String.join(", ", pathParams));
      throw new NotFoundException(devMessage, cve, Resources.ERROR_NOT_FOUND, null);

    } else {
      // errorMap.replaceAll((k, v) -> Collections.unmodifiableList(v));
      var devMessage = STR.fmt("Parameters are invalid: {}", String.join(", ", errorMap.keySet()));
      throw new BadRequestException(devMessage, cve, Resources.ERROR_BAD_REQUEST, null, null, errorMap, null);
    }
  }

  private static String getDisplayName(ParamDesc paramDesc, Resources resources) {
    var bindField = paramDesc.getBindField();
    if (bindField == null || bindField.resKey().isEmpty()) {
      return paramDesc.getParamName();
    }
    return resources.get(bindField.resKey());
  }

  // fieldName: ${0}
  private static Map<String, Object> getMsgParams(ParamDesc paramDesc, Resources resources) {
    var displayName = getDisplayName(paramDesc, resources);
    return Map.of("0", displayName);
  }

  private static Path.Node getParamNode(Path path) {
    for (Node node : path) {
      if (node.getKind() == ElementKind.PARAMETER) {
        return node;
      }
    }
    return null;
  }

  private static Class<?> getValueType(Parameter parameter) {
    if (parameter.getType() == Out.class) {
      Class<?> type = ReflectionUtils.getArgTypes1(parameter.getParameterizedType());
      if (type != null) {
        return type;
      }
    }
    return parameter.getType();
  }
}
