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

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

import com.appslandia.common.base.Bind;
import com.appslandia.common.base.FormatProvider;
import com.appslandia.common.base.NotBind;
import com.appslandia.common.base.Out;
import com.appslandia.common.converters.Converter;
import com.appslandia.common.converters.ConverterException;
import com.appslandia.common.converters.ConverterProvider;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.CharsetUtils;
import com.appslandia.common.utils.MimeTypes;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.common.utils.SplitUtils;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.common.utils.TypeUtils;
import com.appslandia.common.validators.G1;
import com.appslandia.common.validators.MultiValues;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
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
    Queue<BindingNode> queue = new LinkedList<>();
    queue.add(new BindingNode(model, null));

    while (!queue.isEmpty()) {
      BindingNode bindNode = queue.poll();

      for (PropertyDescriptor property : Introspector.getBeanInfo(bindNode.model.getClass()).getPropertyDescriptors()) {
        if (property.getWriteMethod() == null) {
          continue;
        }
        // Field
        Field field = ReflectionUtils.findField(bindNode.model.getClass(), property.getName());
        if ((field == null) || (field.getDeclaredAnnotation(NotBind.class) != null)) {
          continue;
        }

        // FieldDesc
        FieldDesc fieldDesc = new FieldDesc(field);

        // propertyPath
        String propertyPath = (bindNode.path != null) ? (bindNode.path + "." + fieldDesc.getFieldName())
            : fieldDesc.getFieldName();
        if ((excludePaths != null) && (excludePaths.apply(propertyPath))) {
          continue;
        }

        // Handle Binding?
        if (request.getParameterMap().containsKey(propertyPath) || (fieldDesc.getDefaultValue() != null)) {

          // @MultiValues
          MultiValues multiValues = field.getDeclaredAnnotation(MultiValues.class);

          // @MultiValues | Array
          if ((multiValues != null) || field.getType().isArray()) {
            Class<?> elementType = null;

            if (multiValues != null) {
              Asserts.isTrue(field.getType() == String.class);
              MultiValues.MultiValuesValidator.validate(multiValues);

              elementType = TypeUtils.wrap(multiValues.type());
            } else {
              elementType = field.getType().getComponentType();
            }

            // Converter
            Converter<Object> converter = (fieldDesc.getConverter() != null)
                ? this.converterProvider.getConverter(fieldDesc.getConverter())
                : this.converterProvider.getConverter(elementType);

            // paramValues
            String[] paramValues = request.getParameterValues(propertyPath);

            if ((paramValues == null) || Arrays.stream(paramValues).allMatch(v -> StringUtils.isNullOrEmpty(v))) {
              paramValues = SplitUtils.splitByComma(fieldDesc.getDefaultValue());
            }
            Out<String> msgKey = new Out<>();
            Object parsedValue = parseArray(paramValues, elementType, msgKey, converter,
                ServletUtils.getFormatProvider(request));

            if (multiValues != null) {
              property.getWriteMethod().invoke(bindNode.model, toMultiValues(parsedValue));
            } else {
              property.getWriteMethod().invoke(bindNode.model, parsedValue);
            }

            // Add Error
            if (msgKey.value != null) {
              Object[] msgParams = getMsgParams(field, bindNode.model.getClass(), ServletUtils.getResources(request));
              String msg = ServletUtils.getResources(request).get(msgKey.value, msgParams);

              modelState.addError(propertyPath, msg);
            }
            continue;
          }

          // Converter
          Class<?> valueType = getValueType(field);
          Converter<Object> converter = (fieldDesc.getConverter() != null)
              ? this.converterProvider.getConverter(fieldDesc.getConverter())
              : this.converterProvider.getConverter(valueType);

          String paramValue = request.getParameter(propertyPath);
          if (StringUtils.isNullOrEmpty(paramValue)) {
            paramValue = fieldDesc.getDefaultValue();
          }

          Out<String> msgKey = new Out<>();
          Object parsedValue = parseValue(paramValue, valueType, msgKey, converter,
              ServletUtils.getFormatProvider(request));

          if (field.getType() != Out.class) {
            property.getWriteMethod().invoke(bindNode.model, parsedValue);
          } else {
            property.getWriteMethod().invoke(bindNode.model, new Out<Object>(parsedValue));
          }

          // Add Error
          if (msgKey.value != null) {
            Object[] msgParams = getMsgParams(field, bindNode.model.getClass(), ServletUtils.getResources(request));
            String msg = ServletUtils.getResources(request).get(msgKey.value, msgParams);

            modelState.addError(propertyPath, msg);
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
          int subIndexes = getSubIndexes(request, propertyPath);
          if (subIndexes == 0) {
            continue;
          }

          // List<SubModel>
          List<Object> subModel = (field.getType() == List.class) ? new ArrayList<>(subIndexes)
              : ObjectUtils.cast(field.getType().getDeclaredConstructor().newInstance());

          int idx = 0;
          int count = 0;
          while (true) {
            String subIndexProp = propertyPath + "[" + idx + "]";

            if (hasSubProperties(request, subIndexProp)) {

              Object elementModel = elementType.getDeclaredConstructor().newInstance();
              subModel.add(elementModel);
              queue.add(new BindingNode(elementModel, subIndexProp));

              if (++count == subIndexes) {
                break;
              }
            }
            idx++;
          }
          property.getWriteMethod().invoke(bindNode.model, subModel);
          continue;
        }

        // Map
        if (Map.class.isAssignableFrom(field.getType())) {
          Class<?>[] kvTypes = ReflectionUtils.getArgTypes2(field.getGenericType());

          // Map<String, SubModel> supported?
          if ((kvTypes == null) || (kvTypes[0] != String.class)) {
            continue;
          }
          List<String> subKeys = getSubKeys(request, propertyPath);
          if ((subKeys == null) || subKeys.isEmpty()) {
            continue;
          }

          // Map<String, SubModel>
          Map<String, Object> subModel = (field.getType() == Map.class) ? new HashMap<String, Object>()
              : ObjectUtils.cast(field.getType().getDeclaredConstructor().newInstance());

          for (String subKey : subKeys) {
            String subKeyProp = propertyPath + "[" + subKey + "]";

            if (hasSubProperties(request, subKeyProp)) {
              Object valueModel = kvTypes[1].getDeclaredConstructor().newInstance();
              subModel.put(subKey, valueModel);

              queue.add(new BindingNode(valueModel, subKeyProp));
            }
          }
          property.getWriteMethod().invoke(bindNode.model, subModel);
          continue;
        }

        // ITERABLE
        if (Iterable.class.isAssignableFrom(field.getType())) {
          continue;
        }

        // Sub-Model
        if (hasSubProperties(request, propertyPath)) {
          Object subModel = Asserts.notNull(property.getReadMethod()).invoke(bindNode.model);
          if (subModel == null) {
            subModel = field.getType().getDeclaredConstructor().newInstance();
            property.getWriteMethod().invoke(bindNode.model, subModel);
          }
          queue.add(new BindingNode(subModel, propertyPath));
        }
      } // Iteration of properties
    }

    // Validate Model
    validateModel(model, modelState, ServletUtils.getResources(request));
    return modelState;
  }

  public <T> T bindModel(HttpServletRequest request, String partName, Class<T> modelType, ModelState modelState)
      throws Exception {
    // Part
    Part part = request.getPart(partName);
    Asserts.notNull(part);
    Asserts.isTrue(ServletUtils.isContentSupported(part.getContentType(), MimeTypes.APP_JSON));

    // Part JSON
    T model = null;
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(part.getInputStream(), CharsetUtils.parseCharset(part.getContentType())))) {
      model = this.jsonProcessor.read(br, modelType);
    }

    // Validate Model
    if (model != null) {
      validateModel(model, modelState, ServletUtils.getResources(request));
    }
    return model;
  }

  public void validateModel(Object model, ModelState modelState, Resources resources) {
    StringBuilder propertyPath = null;
    for (Object error : this.validator.validate(model, Default.class, G1.class)) {
      ConstraintViolation<?> violation = (ConstraintViolation<?>) error;

      if (propertyPath == null) {
        propertyPath = new StringBuilder();
      } else {
        propertyPath.setLength(0);
      }
      String fieldName = getLeafProp(violation.getPropertyPath(), propertyPath);
      Asserts.notNull(fieldName);

      // Add Error
      String msgKey = getMsgKey(violation);
      Object[] msgParams = getMsgParams(violation, fieldName, resources);
      String msg = resources.get(msgKey, msgParams);

      modelState.addError(fieldName, msg);
    }
  }

  // propertyPath.subProp
  public static boolean hasSubProperties(HttpServletRequest request, String propertyPath) {
    String subProp = propertyPath + '.';
    for (String parameter : request.getParameterMap().keySet()) {
      if (parameter.startsWith(subProp) && (parameter.length() > subProp.length())) {
        return true;
      }
    }
    return false;
  }

  // list[index].subProp
  private static final Pattern SUB_INDEX_PROP_PATTERN = Pattern.compile("\\[\\d+]\\.[^\\s]+");

  public static int getSubIndexes(HttpServletRequest request, String propertyPath) {
    Set<String> indexes = null;
    for (String parameter : request.getParameterMap().keySet()) {

      if (parameter.startsWith(propertyPath)) {
        String subIndexProp = parameter.substring(propertyPath.length());

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

  public static List<String> getSubKeys(HttpServletRequest request, String propertyPath) {
    List<String> keys = null;
    for (String parameter : request.getParameterMap().keySet()) {

      if (parameter.startsWith(propertyPath)) {
        String subKeyProp = parameter.substring(propertyPath.length());

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

  private static String getLeafProp(Path path, StringBuilder propertyPath) {
    Iterator<Path.Node> iter = path.iterator();
    String lastProp = null;

    while (iter.hasNext()) {
      Path.Node node = (Path.Node) iter.next();
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

  protected String getDisplayName(Field field, Class<?> modelType, Resources resources) {
    String keyPrefix = StringUtils.firstLowerCase(modelType.getSimpleName(), Locale.ENGLISH);
    String key = keyPrefix + "." + new FieldDesc(field).getFieldName();
    return resources.get(key);
  }

  private Object[] getMsgParams(Field field, Class<?> modelType, Resources resources) {
    String displayName = getDisplayName(field, modelType, resources);
    return new Object[] { displayName };
  }

  private Object[] getMsgParams(ConstraintViolation<?> violation, String fieldName, Resources resources) {
    Field field = ReflectionUtils.findField(violation.getLeafBean().getClass(), fieldName);
    String displayName = getDisplayName(field, violation.getLeafBean().getClass(), resources);
    return new Object[] { displayName };
  }

  public static String getMsgKey(ConstraintViolation<?> violation) {
    String template = violation.getMessageTemplate();
    Asserts.isTrue(template.startsWith("{") && template.endsWith("}"), "messageTemplate is invalid.");
    return template.substring(1, template.length() - 1);
  }

  public static Object parseArray(String[] paramValues, Class<?> elementType, Out<String> msgKey,
      Converter<?> converter, FormatProvider formatProvider) {
    if (paramValues == null) {
      return null;
    }
    Object parsedArray = Array.newInstance(elementType, paramValues.length);
    for (int idx = 0; idx < paramValues.length; idx++) {

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

  public static Class<?> getValueType(Field field) {
    if (field.getType() == Out.class) {
      Class<?> type = ReflectionUtils.getArgTypes1(field.getGenericType());
      if (type != null) {
        return type;
      }
    }
    return field.getType();
  }

  public static Class<?> getValueType(Parameter parameter) {
    if (parameter.getType() == Out.class) {
      Class<?> type = ReflectionUtils.getArgTypes1(parameter.getParameterizedType());
      if (type != null) {
        return type;
      }
    }
    return parameter.getType();
  }

  public static String toMultiValues(Object parsedValue) {
    if (parsedValue == null) {
      return null;
    }
    int len = Array.getLength(parsedValue);
    StringBuilder sb = new StringBuilder(8 * len);

    for (int i = 0; i < len; i++) {
      Object value = Array.get(parsedValue, i);
      if (value == null) {
        continue;
      }
      if (sb.length() > 0) {
        sb.append(",");
      }
      sb.append(value);
    }
    return sb.length() > 0 ? sb.toString() : null;
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
    final Bind bind;

    public FieldDesc(Field field) {
      this.field = field;
      this.bind = field.getDeclaredAnnotation(Bind.class);
    }

    public String getFieldName() {
      return ((this.bind != null) && !this.bind.name().isEmpty()) ? this.bind.name() : this.field.getName();
    }

    public String getConverter() {
      return ((this.bind != null) && !this.bind.converter().isEmpty()) ? this.bind.converter() : null;
    }

    public String getDefaultValue() {
      return ((this.bind != null) && !this.bind.defaultValue().isEmpty()) ? this.bind.defaultValue() : null;
    }
  }
}
