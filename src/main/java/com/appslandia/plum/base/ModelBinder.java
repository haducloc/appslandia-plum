// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.appslandia.common.base.FormatProvider;
import com.appslandia.common.base.NotBind;
import com.appslandia.common.base.Out;
import com.appslandia.common.converters.Converter;
import com.appslandia.common.converters.ConverterException;
import com.appslandia.common.converters.ConverterProvider;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.CharsetUtils;
import com.appslandia.common.utils.ModelUtils;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.common.utils.SplitUtils;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.common.utils.TypeUtils;
import com.appslandia.common.validators.MultiInt;
import com.appslandia.common.validators.MultiString;
import com.appslandia.plum.utils.ParamUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Path.Node;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class ModelBinder {

  @Inject
  protected ConverterProvider converterProvider;

  @Inject
  protected Validator validator;

  @Inject
  protected JsonProcessor jsonProcessor;

  public ModelState bindModel(HttpServletRequest request, Object model) throws Exception {
    return bindModel(request, model, ServletUtils.getModelState(request), null);
  }

  public ModelState bindModel(HttpServletRequest request, Object model, Function<String, Boolean> excludePaths)
      throws Exception {
    return bindModel(request, model, ServletUtils.getModelState(request), excludePaths);
  }

  public ModelState bindModel(HttpServletRequest request, Object model, ModelState modelState) throws Exception {
    return bindModel(request, model, modelState, null);
  }

  public ModelState bindModel(HttpServletRequest request, Object model, ModelState modelState,
      Function<String, Boolean> excludePaths) throws Exception {

    var requestContext = ServletUtils.getRequestContext(request);
    Queue<BindingNode> nq = new LinkedList<>();
    nq.add(new BindingNode(model, null));

    while (!nq.isEmpty()) {
      var bindNode = nq.poll();

      var modelBI = ModelUtils.getBeanInfo(bindNode.model.getClass());
      for (PropertyDescriptor property : modelBI.getPropertyDescriptors()) {
        if ("class".equals(property.getName())) {
          continue;
        }

        // Field
        var field = ReflectionUtils.findField(bindNode.model.getClass(), property.getName());
        if ((field == null) || (field.getDeclaredAnnotation(NotBind.class) != null)) {
          continue;
        }

        // FieldDesc
        var fieldDesc = new FieldDesc(field);

        // propertyPath
        var propertyPath = (bindNode.path != null) ? (bindNode.path + "." + fieldDesc.getFieldName())
            : fieldDesc.getFieldName();
        if ((excludePaths != null) && (excludePaths.apply(propertyPath))) {
          continue;
        }

        // If propertyPath matched
        if (request.getParameterMap().containsKey(propertyPath) || (fieldDesc.getDefaultValue() != null)) {
          Asserts.notNull(property.getReadMethod());
          Asserts.notNull(property.getWriteMethod());

          // @MultiValue
          var multiValue = hasMultiValue(field);

          // multiValue | Array
          if (multiValue || field.getType().isArray()) {
            Class<?> elementType = null;

            if (multiValue) {
              Asserts.isTrue(field.getType() == String.class);
              elementType = String.class;
            } else {
              elementType = field.getType().getComponentType();
            }

            // Converter
            var converter = (fieldDesc.getConverter() != null)
                ? converterProvider.getConverter(fieldDesc.getConverter())
                : converterProvider.getConverter(elementType);

            // paramValues
            var paramValues = request.getParameterValues(propertyPath);
            if ((paramValues == null) && (fieldDesc.getDefaultValue() != null)) {
              paramValues = SplitUtils.splitByComma(fieldDesc.getDefaultValue());
            }

            var msgKey = new Out<String>();
            var parsedValue = parseArray(paramValues, elementType, msgKey, converter,
                requestContext.getFormatProvider());

            if (multiValue) {
              property.getWriteMethod().invoke(bindNode.model, toMultiValue(parsedValue));
            } else {
              property.getWriteMethod().invoke(bindNode.model, parsedValue);
            }

            // Add conversion error
            if (msgKey.value != null) {
              var msgParams = getMsgParams(field, bindNode.model.getClass(), requestContext.getResources());
              var resolvedMsg = requestContext.res(msgKey.value, msgParams);

              modelState.addError(propertyPath, resolvedMsg);
            }
            continue;
          }

          // Converter
          Class<?> valueType = getValueType(field);
          var converter = (fieldDesc.getConverter() != null) ? converterProvider.getConverter(fieldDesc.getConverter())
              : converterProvider.getConverter(valueType);

          var paramValue = ParamUtils.getString(request, propertyPath, fieldDesc.getDefaultValue());
          var msgKey = new Out<String>();

          var parsedValue = parseValue(paramValue, valueType, msgKey, converter, requestContext.getFormatProvider());

          if (field.getType() != Out.class) {
            property.getWriteMethod().invoke(bindNode.model, parsedValue);
          } else {
            property.getWriteMethod().invoke(bindNode.model, new Out<>(parsedValue));
          }

          // Add conversion error
          if (msgKey.value != null) {
            var msgParams = getMsgParams(field, bindNode.model.getClass(), requestContext.getResources());
            var resolvedMsg = requestContext.res(msgKey.value, msgParams);

            modelState.addError(propertyPath, resolvedMsg);
          }
          continue;
        }

        // List
        if (List.class.isAssignableFrom(field.getType())) {
          Class<?> elementType = ReflectionUtils.getArgTypes1(field.getGenericType());

          // List<SubModel> supported?
          if (elementType == null) {
            continue;
          }
          var subIndexes = getSubIndexes(request, propertyPath);
          if (subIndexes == 0) {
            continue;
          }

          // List<SubModel>
          Asserts.notNull(property.getReadMethod());
          List<Object> subModel = ObjectUtils.cast(property.getReadMethod().invoke(bindNode.model));

          var subModelNull = (subModel == null);
          if (subModelNull) {
            subModel = (field.getType() == List.class) ? new ArrayList<>(subIndexes)
                : ObjectUtils.cast(field.getType().getDeclaredConstructor().newInstance());
          }

          var idx = 0;
          var count = 0;

          while (true) {
            var subIndexProp = propertyPath + "[" + idx + "]";
            if (hasSubProperties(request, subIndexProp)) {

              Object elementModel = elementType.getDeclaredConstructor().newInstance();
              subModel.add(elementModel);
              nq.add(new BindingNode(elementModel, subIndexProp));

              if (++count == subIndexes) {
                break;
              }
            }
            idx++;
          }
          if (subModelNull) {
            Asserts.notNull(property.getWriteMethod());
            property.getWriteMethod().invoke(bindNode.model, subModel);
          }
          continue;
        }

        // Map
        if (Map.class.isAssignableFrom(field.getType())) {
          var kvTypes = ReflectionUtils.getArgTypes2(field.getGenericType());

          // Map<String, SubModel> supported?
          if ((kvTypes == null) || (kvTypes[0] != String.class)) {
            continue;
          }
          var subKeys = getSubKeys(request, propertyPath);
          if ((subKeys == null) || subKeys.isEmpty()) {
            continue;
          }

          // Map<String, SubModel>
          Asserts.notNull(property.getReadMethod());
          Map<String, Object> subModel = ObjectUtils.cast(property.getReadMethod().invoke(bindNode.model));

          var subModelNull = (subModel == null);
          if (subModelNull) {
            subModel = (field.getType() == Map.class) ? new HashMap<>()
                : ObjectUtils.cast(field.getType().getDeclaredConstructor().newInstance());
          }

          for (String subKey : subKeys) {
            var subKeyProp = propertyPath + "[" + subKey + "]";

            if (hasSubProperties(request, subKeyProp)) {
              Object valueModel = kvTypes[1].getDeclaredConstructor().newInstance();
              subModel.put(subKey, valueModel);

              nq.add(new BindingNode(valueModel, subKeyProp));
            }
          }

          if (subModelNull) {
            Asserts.notNull(property.getWriteMethod());
            property.getWriteMethod().invoke(bindNode.model, subModel);
          }
          continue;
        }

        // ITERABLE
        if (Iterable.class.isAssignableFrom(field.getType())) {
          continue;
        }

        // Sub-Model
        if (hasSubProperties(request, propertyPath)) {

          Asserts.notNull(property.getReadMethod());
          var subModel = property.getReadMethod().invoke(bindNode.model);

          if (subModel == null) {
            subModel = field.getType().getDeclaredConstructor().newInstance();

            Asserts.notNull(property.getWriteMethod());
            property.getWriteMethod().invoke(bindNode.model, subModel);
          }
          nq.add(new BindingNode(subModel, propertyPath));
        }
      } // Iteration of properties
    }

    // Validate Model
    validateModel(model, Default.class, modelState, requestContext.getResources());
    return modelState;
  }

  public <T> T bindModel(HttpServletRequest request, String partName, Class<T> modelType, ModelState modelState)
      throws Exception {
    // Part
    var part = request.getPart(partName);
    Asserts.notNull(part);
    Asserts.isTrue(ServletUtils.isJsonMimeType(part.getContentType()));

    // Part JSON
    T model = null;
    try (var br = new BufferedReader(
        new InputStreamReader(part.getInputStream(), CharsetUtils.parseCharset(part.getContentType())))) {
      model = jsonProcessor.read(br, modelType);
    }

    // Validate Model
    if (model != null) {
      var requestContext = ServletUtils.getRequestContext(request);
      validateModel(model, Default.class, modelState, requestContext.getResources());
    }
    return model;
  }

  public void validateModel(Object model, Class<?> targetGroup, ModelState modelState, Resources resources) {
    StringBuilder propertyPath = null;
    for (var violation : validator.validate(model, targetGroup)) {

      if (propertyPath == null) {
        propertyPath = new StringBuilder();
      } else {
        propertyPath.setLength(0);
      }

      var fieldName = getLeafProp(violation.getPropertyPath(), propertyPath);
      Asserts.notNull(fieldName);

      // Add validation error
      var msgKey = getMsgKey(violation);
      var msgParams = getMsgParams(violation, fieldName, resources);
      var resolvedMsg = resources.get(msgKey, msgParams);

      modelState.addError(fieldName, resolvedMsg);
    }
  }

  // propertyPath.subProp
  private static boolean hasSubProperties(HttpServletRequest request, String propertyPath) {
    var subProp = propertyPath + ".";
    for (String parameter : request.getParameterMap().keySet()) {
      if (parameter.startsWith(subProp) && (parameter.length() > subProp.length())) {
        return true;
      }
    }
    return false;
  }

  // list[index].subProp
  private static final Pattern SUB_INDEX_PROP_PATTERN = Pattern.compile("\\[\\d+]\\.[^\\s]+");

  private static int getSubIndexes(HttpServletRequest request, String propertyPath) {
    Set<String> indexes = null;
    for (String parameter : request.getParameterMap().keySet()) {

      if (parameter.startsWith(propertyPath)) {
        var subIndexProp = parameter.substring(propertyPath.length());

        if (SUB_INDEX_PROP_PATTERN.matcher(subIndexProp).matches()) {
          if (indexes == null) {
            indexes = new HashSet<>();
          }
          indexes.add(subIndexProp.substring(1, subIndexProp.indexOf(']')));

          Asserts.isTrue(indexes.size() <= 100, "The list parameter is too long.");
        }
      }
    }
    return (indexes != null) ? indexes.size() : 0;
  }

  // map[key].subProp
  private static final Pattern SUB_KEY_PROP_PATTERN = Pattern.compile("\\[[^\\s]+]\\.[^\\s]+");

  private static List<String> getSubKeys(HttpServletRequest request, String propertyPath) {
    List<String> keys = null;
    for (String parameter : request.getParameterMap().keySet()) {

      if (parameter.startsWith(propertyPath)) {
        var subKeyProp = parameter.substring(propertyPath.length());

        if (SUB_KEY_PROP_PATTERN.matcher(subKeyProp).matches()) {
          if (keys == null) {
            keys = new ArrayList<>();
          }
          keys.add(subKeyProp.substring(1, subKeyProp.indexOf(']')));

          Asserts.isTrue(keys.size() <= 100, "The map parameter is too long.");
        }
      }
    }
    return keys;
  }

  private static boolean hasMultiValue(Field field) {
    return field.getDeclaredAnnotation(MultiInt.class) != null
        || field.getDeclaredAnnotation(MultiString.class) != null;
  }

  private static String getLeafProp(Path path, StringBuilder propertyPath) {
    String lastProp = null;
    for (Node node : path) {
      if (node.getIndex() != null) {
        propertyPath.append('[').append(node.getIndex()).append(']');
      }
      if (node.getName() != null) {
        lastProp = node.getName();
        if (propertyPath.length() != 0) {
          propertyPath.append('.');
        }
        propertyPath.append(lastProp);
      }
    }
    return lastProp;
  }

  private static String getDisplayName(Field field, Class<?> modelType, Resources resources) {
    var resPrefix = modelType.getDeclaredAnnotation(ResourcePrefix.class);
    if (resPrefix != null) {
      Asserts.isTrue(!resPrefix.value().isBlank(), "@ResourcePrefix(value) cannot be blank.");
    }

    var keyPrefix = (resPrefix != null) ? resPrefix.value() : StringUtils.toSnakeCase(modelType.getSimpleName());
    var key = keyPrefix + "." + StringUtils.toSnakeCase(field.getName());
    return resources.get(key);
  }

  // fieldName: ${0}
  private static Map<String, Object> getMsgParams(Field field, Class<?> modelType, Resources resources) {
    var displayName = getDisplayName(field, modelType, resources);
    return Map.of("0", displayName);
  }

  private static final Set<String> RESERVED_ATTRIBUTES = Set.of("message", "groups", "payload", "validationAppliesTo");

  // fieldName: ${0}
  private static Map<String, Object> getMsgParams(ConstraintViolation<?> violation, String fieldName,
      Resources resources) {
    var field = ReflectionUtils.findField(violation.getLeafBean().getClass(), fieldName);
    var displayName = getDisplayName(field, violation.getLeafBean().getClass(), resources);

    var params = violation.getConstraintDescriptor().getAttributes().entrySet().stream()
        .filter(e -> !RESERVED_ATTRIBUTES.contains(e.getKey()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, HashMap::new));

    params.put("0", displayName);
    return Collections.unmodifiableMap(params);
  }

  public static String getMsgKey(ConstraintViolation<?> violation) {
    var template = violation.getMessageTemplate();
    Asserts.isTrue(template.startsWith("{") && template.endsWith("}"), "messageTemplate is invalid.");
    return template.substring(1, template.length() - 1);
  }

  public static Object parseArray(String[] paramValues, Class<?> elementType, Out<String> msgKey,
      Converter<?> converter, FormatProvider formatProvider) {
    if (paramValues == null) {
      return null;
    }
    var parsedArray = Array.newInstance(elementType, paramValues.length);
    for (var idx = 0; idx < paramValues.length; idx++) {

      Object element = null;
      try {
        element = converter.parse(paramValues[idx], formatProvider);
        if ((element == null) && (elementType.isPrimitive())) {

          element = TypeUtils.defaultValue(elementType);
          msgKey.value = Resources.ERROR_FIELD_INVALID;
        }
      } catch (ConverterException ex) {
        element = (elementType != String.class) ? TypeUtils.defaultValue(elementType) : paramValues[idx];
        msgKey.value = Resources.ERROR_FIELD_INVALID;
      }
      Array.set(parsedArray, idx, element);
    }
    return parsedArray;
  }

  public static Object parseValue(String paramValue, Class<?> targetType, Out<String> msgKey, Converter<?> converter,
      FormatProvider formatProvider) {
    Object result = null;
    try {
      result = converter.parse(paramValue, formatProvider);
      if ((result == null) && (targetType.isPrimitive())) {

        result = TypeUtils.defaultValue(targetType);
        msgKey.value = Resources.ERROR_FIELD_REQUIRED;
      }
    } catch (ConverterException ex) {
      result = (targetType != String.class) ? TypeUtils.defaultValue(targetType) : paramValue;
      msgKey.value = ex.getMsgKey();
    }
    return result;
  }

  public static String toMultiValue(Object parsedValue) {
    if (parsedValue == null) {
      return null;
    }
    var len = Array.getLength(parsedValue);
    var sb = new StringBuilder(8 * len);

    for (var i = 0; i < len; i++) {
      var value = Array.get(parsedValue, i);
      if (value == null) {
        continue;
      }
      if (sb.length() > 0) {
        sb.append(',');
      }
      sb.append(value);
    }
    return sb.length() > 0 ? sb.toString() : null;
  }

  private static Class<?> getValueType(Field field) {
    if (field.getType() == Out.class) {
      Class<?> type = ReflectionUtils.getArgTypes1(field.getGenericType());
      if (type != null) {
        return type;
      }
    }
    return field.getType();
  }

  private static class BindingNode {
    final Object model;
    final String path;

    public BindingNode(Object model, String path) {
      this.model = model;
      this.path = path;
    }
  }

  private static class FieldDesc {
    final Field field;
    final BindField bindField;

    public FieldDesc(Field field) {
      this.field = field;
      bindField = field.getDeclaredAnnotation(BindField.class);
    }

    public String getFieldName() {
      return (bindField != null && !bindField.name().isEmpty()) ? bindField.name() : field.getName();
    }

    public String getConverter() {
      return ((bindField != null) && !bindField.converter().isEmpty()) ? bindField.converter() : null;
    }

    public String getDefaultValue() {
      return ((bindField != null) && !bindField.defaultValue().isEmpty()) ? bindField.defaultValue() : null;
    }
  }
}
