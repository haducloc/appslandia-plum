// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.base.GroupFormat;
import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 *
 */
public class GroupFormatProvider extends InitializingObject {

  private Map<String, GroupFormat> groupFormatMap = new HashMap<>();

  @Override
  protected void init() throws Exception {
    groupFormatMap = Collections.unmodifiableMap(groupFormatMap);
  }

  public GroupFormat getGroupFormat(String name) {
    initialize();
    var impl = groupFormatMap.get(name);
    return Arguments.notNull(impl, "No GroupFormat is registered for name '{}'.", name);
  }

  public void registerGroupFormat(String name, GroupFormat impl) {
    assertNotInitialized();
    groupFormatMap.put(name, impl);
  }
}
