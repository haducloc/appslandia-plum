// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Collections;
import java.util.Map;

import com.appslandia.common.base.CaseInsensitiveMap;
import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 *
 */
public class SortConfig extends InitializingObject {

  private String defBy;
  private Map<String, Boolean> sortFields = new CaseInsensitiveMap<>();

  @Override
  protected void init() throws Exception {
    Arguments.notNull(defBy);
    sortFields = Collections.unmodifiableMap(sortFields);
  }

  public SortConfig asc(String... fieldNames) {
    assertNotInitialized();

    for (String fieldName : fieldNames) {
      sortFields.put(fieldName, true);
    }

    if (defBy == null && fieldNames.length > 0) {
      defBy = fieldNames[0];
    }
    return this;
  }

  public SortConfig desc(String... fieldNames) {
    assertNotInitialized();

    for (String fieldName : fieldNames) {
      sortFields.put(fieldName, false);
    }

    if (defBy == null && fieldNames.length > 0) {
      defBy = fieldNames[0];
    }
    return this;
  }

  public SortConfig defBy(String fieldName) {
    assertNotInitialized();

    Arguments.isTrue(sortFields.containsKey(fieldName));
    defBy = fieldName;
    return this;
  }

  public String getDefBy() {
    initialize();
    return defBy;
  }

  public boolean hasField(String fieldName) {
    initialize();
    return sortFields.containsKey(fieldName);
  }

  public Boolean getSortAsc(String fieldName) {
    initialize();
    return sortFields.get(fieldName);
  }
}
