// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.function.Function;

import com.appslandia.common.utils.ModelUtils;
import com.appslandia.plum.models.LocalIdSupport;

/**
 *
 * @author Loc Ha
 *
 */
public class ModelStateUtils {

  public static void traverse(Object model, String currentPath, String currentPathLocal, PropertyVisitor visitor,
      Function<String, Boolean> isSubModel) throws Exception {
    if (model == null) {
      return;
    }
    var modelBI = ModelUtils.getBeanInfo(model.getClass());
    for (PropertyDescriptor propertyDescriptor : modelBI.getPropertyDescriptors()) {

      var propertyName = propertyDescriptor.getName();
      if ("class".equals(propertyName)) {
        continue;
      }

      var propPath = currentPath.isEmpty() ? propertyName : currentPath + "." + propertyName;
      var propPathLocal = currentPath.equals(currentPathLocal) ? propPath
          : (currentPathLocal.isEmpty() ? propertyName : currentPathLocal + "." + propertyName);

      var value = propertyDescriptor.getReadMethod().invoke(model);

      if (value == null) {
        visitor.visit(propPath, propPathLocal);
      }
      // List
      else if (value instanceof List) {
        visitor.visit(propPath, propPathLocal);
        List<?> list = (List<?>) value;

        for (var idx = 0; idx < list.size(); idx++) {

          Object element = list.get(idx);
          var elementPath = propPath + "[" + idx + "]";
          String elementPathLocal;

          // LocalIdSupport
          if (element instanceof LocalIdSupport) {
            elementPathLocal = propPathLocal + "[" + ((LocalIdSupport) element).getLocalId() + "]";
          } else {
            elementPathLocal = propPath.equals(propPathLocal) ? elementPath : (propPathLocal + "[" + idx + "]");
          }

          traverse(list.get(idx), elementPath, elementPathLocal, visitor, isSubModel);
        }
      }
      // Sub-model
      else if (isSubModel.apply(propPath)) {

        visitor.visit(propPath, propPathLocal);
        traverse(value, propPath, propPathLocal, visitor, isSubModel);
      }
      // Other
      else {
        visitor.visit(propPath, propPathLocal);
      }
    }
  }

  @FunctionalInterface
  public interface PropertyVisitor {
    void visit(String path, String pathLocal);
  }
}
