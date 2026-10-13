// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.base.CodeGroup;
import com.appslandia.common.base.InitializingException;
import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.utils.CaseUtils;
import com.appslandia.common.utils.ReflectionUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class CodeGroupProvider extends InitializingObject {

  private Map<CodeKey, String> codeKeyMap = new HashMap<>();

  @Override
  protected void init() throws Exception {
    codeKeyMap = Collections.unmodifiableMap(codeKeyMap);
  }

  public String getDescKey(String codeGroup, Object code) {
    initialize();
    return codeKeyMap.get(new CodeKey(codeGroup, code));
  }

  public void addCodeClass(Class<?> codeClass) {
    assertNotInitialized();
    try {
      var defCodeGroup = CaseUtils.toSnakeCase(codeClass.getSimpleName());
      for (Field field : codeClass.getDeclaredFields()) {

        if (!ReflectionUtils.isPublicStaticFinal(field.getModifiers())) {
          continue;
        }
        var codeGroupAnn = field.getDeclaredAnnotation(CodeGroup.class);
        if (codeGroupAnn == null) {
          continue;
        }
        var parsedGroup = codeGroupAnn.value().isEmpty() ? defCodeGroup : codeGroupAnn.value();

        // descKey: groupName.field_name
        var descKey = parsedGroup + "." + CaseUtils.toSnakeCase(field.getName());
        var code = field.get(null);

        codeKeyMap.put(new CodeKey(parsedGroup, code), descKey);
      }

    } catch (Exception ex) {
      throw new InitializingException(ex);
    }
  }

  public void addCode(String codeGroup, Object code, String descKey) {
    assertNotInitialized();
    codeKeyMap.put(new CodeKey(codeGroup, code), descKey);
  }

  private record CodeKey(String codeGroup, Object code) {
  }
}
