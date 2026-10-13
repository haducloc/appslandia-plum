// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 *
 */
public class CorsPolicyProvider extends InitializingObject {

  private Map<String, CorsPolicy> corsPolicyMap = new HashMap<>();

  @Override
  protected void init() throws Exception {
    this.corsPolicyMap = Collections.unmodifiableMap(corsPolicyMap);
  }

  public CorsPolicy getCorsPolicy(String name) {
    initialize();
    var impl = corsPolicyMap.get(name);
    return Arguments.notNull(impl, "No CorsPolicy is registered for name '{}'.", name);
  }

  public void registerCorsPolicy(String name, CorsPolicy impl) {
    assertNotInitialized();
    corsPolicyMap.put(name, impl);
  }
}
