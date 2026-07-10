// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.base.TextBuilder;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.ObjectUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class HttpAuthMechanismProvider extends InitializingObject {

  private Map<String, HttpAuthMechanismBase> authMechanismMap = new HashMap<>();

  @Override
  protected void init() throws Exception {
    authMechanismMap = Collections.unmodifiableMap(authMechanismMap);
  }

  public void registerMechanism(String module, HttpAuthMechanismBase impl) {
    assertNotInitialized();
    authMechanismMap.put(module, impl);
  }

  public HttpAuthMechanismBase getMechanism(String module) {
    initialize();
    var impl = authMechanismMap.get(module);
    return Arguments.notNull(impl, "No HttpAuthMechanismBase is registered for module '{}'.", module);
  }

  //@formatter:off
  @Override
  public String toString() {
    initialize();

    var desc = new TextBuilder(512);
    desc.append(ObjectUtils.toIdHash(this)).append("[").appendln();

    for (var auth : authMechanismMap.entrySet()) {
      desc.appendsp(2).append(ObjectUtils.toIdHash(auth.getValue())).append("(").appendln()
          .appendsp(4).append("module: ").append(auth.getKey()).appendln()
          .appendsp(4).append("authMethod: ").append(auth.getValue().getAuthMethod()).appendln()
          .appendsp(2).appendln(")");
    }
    desc.append("]").appendln();
    return desc.toString();
  }
  // @formatter:on
}
