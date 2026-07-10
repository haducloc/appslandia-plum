// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.appslandia.common.base.InitializingException;
import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.common.utils.StringUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class ConstGroupProvider extends InitializingObject {

  private Map<ConstValue, String> constKeyMap = new HashMap<>();

  @Override
  protected void init() throws Exception {
    constKeyMap = Collections.unmodifiableMap(constKeyMap);
  }

  public String getDescKey(String constGroup, Object value) {
    initialize();
    return constKeyMap.get(new ConstValue(constGroup, value));
  }

  public void addConstClass(Class<?> constClass) {
    assertNotInitialized();
    try {
      var defConstGroup = StringUtils.toSnakeCase(constClass.getSimpleName());
      for (Field field : constClass.getDeclaredFields()) {

        if (!ReflectionUtils.isPublicConst(field.getModifiers())
            || (field.getDeclaredAnnotation(ConstGroup.class) == null)) {
          continue;
        }
        var constGroup = field.getDeclaredAnnotation(ConstGroup.class);
        var group = constGroup.value().isEmpty() ? defConstGroup : constGroup.value();

        // descKey: group.constName
        var descKey = group + "." + StringUtils.toSnakeCase(field.getName());
        var value = field.get(null);

        constKeyMap.put(new ConstValue(group, value), descKey);
      }
    } catch (Exception ex) {
      throw new InitializingException(ex);
    }
  }

  public void addConst(String constGroup, Object value, String descKey) {
    constKeyMap.put(new ConstValue(constGroup, value), descKey);
  }

  private static class ConstValue {

    final String constGroup;
    final Object value;

    public ConstValue(String constGroup, Object value) {
      this.constGroup = constGroup;
      this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof ConstValue that)) {
        return false;
      }
      return Objects.equals(constGroup, that.constGroup) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
      int hash = 1, p = 31;
      hash = p * hash + Objects.hashCode(constGroup);
      hash = p * hash + Objects.hashCode(value);
      return hash;
    }
  }
}
