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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.UUID;

import com.appslandia.common.models.LocalIdSupport;

/**
 *
 * @author Loc Ha
 *
 */
public class ModelStateUtils {

  public static void traverse(Object model, String currentPath, String currentPathLocal, PropertyVisitor visitor)
      throws Exception {
    if (model == null) {
      return;
    }

    // Notes:
    // Compare two paths using ==, not the String.equals method in the traverse
    // context
    // For other context, use String.equals

    BeanInfo beanInfo = Introspector.getBeanInfo(model.getClass());
    PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

    for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
      String propertyName = propertyDescriptor.getName();

      if ("class".equals(propertyName)) {
        continue;
      }

      // Value
      Object value = propertyDescriptor.getReadMethod().invoke(model);

      // Property Path
      String propPath = currentPath.isEmpty() ? propertyName : currentPath + "." + propertyName;

      // Property Path - Local
      String propPathLocal = (currentPath == currentPathLocal) ? propPath
          : (currentPathLocal.isEmpty() ? propertyName : currentPathLocal + "." + propertyName);

      if (value == null) {
        visitor.visit(propPath, propPathLocal);

      }
      // List
      else if (value instanceof List) {

        visitor.visit(propPath, propPathLocal);
        List<?> list = (List<?>) value;

        for (int idx = 0; idx < list.size(); idx++) {

          Object element = list.get(idx);
          String elementPath = propPath + "[" + idx + "]";
          String elementPathLocal = null;

          if (element instanceof LocalIdSupport) {
            elementPathLocal = propPathLocal + "[" + ((LocalIdSupport) element).getLocalId() + "]";
          } else {
            elementPathLocal = (propPath == propPathLocal) ? elementPath : (propPathLocal + "[" + idx + "]");
          }

          traverse(list.get(idx), elementPath, elementPathLocal, visitor);
        }
      }
      // Model
      else if (isModel(value)) {

        visitor.visit(propPath, propPathLocal);
        traverse(value, propPath, propPathLocal, visitor);

      }
      // Value
      else {
        visitor.visit(propPath, propPathLocal);
      }
    }
  }

  private static boolean isModel(Object value) {
    return !(value.getClass().isPrimitive() || value instanceof String || value instanceof Number
        || value instanceof Boolean || value instanceof Character || value instanceof Enum || value instanceof UUID
        || value instanceof java.util.Date || value instanceof Temporal);
  }

  @FunctionalInterface
  public interface PropertyVisitor {
    void visit(String path, String pathLocal);
  }
}
