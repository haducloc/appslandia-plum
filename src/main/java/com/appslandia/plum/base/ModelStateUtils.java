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

import java.beans.PropertyDescriptor;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import com.appslandia.common.models.LocalIdSupport;
import com.appslandia.common.utils.ModelUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class ModelStateUtils {

  public static void traverse(Object model, String currentPath, String currentPathLocal, PropertyVisitor visitor)
      throws Exception {
    traverse(model, currentPath, currentPathLocal, visitor, ModelStateUtils::isModelType);
  }

  public static void traverse(Object model, String currentPath, String currentPathLocal, PropertyVisitor visitor,
      Function<Object, Boolean> isModelType) throws Exception {
    if (model == null) {
      return;
    }

    var modelBI = ModelUtils.getBeanInfo(model.getClass());
    for (PropertyDescriptor propertyDescriptor : modelBI.getPropertyDescriptors()) {
      var propertyName = propertyDescriptor.getName();
      if ("class".equals(propertyName)) {
        continue;
      }

      var value = propertyDescriptor.getReadMethod().invoke(model);
      var propPath = currentPath.isEmpty() ? propertyName : currentPath + "." + propertyName;
      var propPathLocal = currentPath.equals(currentPathLocal) ? propPath
          : (currentPathLocal.isEmpty() ? propertyName : currentPathLocal + "." + propertyName);

      if (value == null) {
        visitor.visit(propPath, propPathLocal);
      } else if (value instanceof List) {
        visitor.visit(propPath, propPathLocal);
        List<?> list = (List<?>) value;

        for (var idx = 0; idx < list.size(); idx++) {

          Object element = list.get(idx);
          var elementPath = propPath + "[" + idx + "]";
          String elementPathLocal;

          if (element instanceof LocalIdSupport) {
            elementPathLocal = propPathLocal + "[" + ((LocalIdSupport) element).getLocalId() + "]";
          } else {
            elementPathLocal = propPath.equals(propPathLocal) ? elementPath : (propPathLocal + "[" + idx + "]");
          }

          traverse(list.get(idx), elementPath, elementPathLocal, visitor);
        }
      } else if (isModelType.apply(value)) {
        visitor.visit(propPath, propPathLocal);
        traverse(value, propPath, propPathLocal, visitor);
      } else {
        visitor.visit(propPath, propPathLocal);
      }
    }
  }

  private static boolean isModelType(Object value) {
    return !(value instanceof String || value instanceof Boolean || value instanceof Character || value instanceof UUID
        || value instanceof Number || value instanceof Enum<?> || value instanceof Temporal
        || value instanceof java.util.Date);
  }

  @FunctionalInterface
  public interface PropertyVisitor {
    void visit(String path, String pathLocal);
  }
}
