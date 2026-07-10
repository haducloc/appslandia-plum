// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import com.appslandia.common.base.SimpleConfig;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.mem.MemModules;

/**
 *
 * @author Loc Ha
 *
 */
public class MockAppConfig extends AppConfig {

  public MockAppConfig() {
    var config = new SimpleConfig();
    config.set(CONFIG_DEFAULT_MODULE, MemModules.MEM_FORM);
    this.config = config;
  }

  public MockAppConfig set(String key, String value) {
    config.set(key, value);
    return this;
  }

  public MockAppConfig set(String key, boolean value) {
    config.set(key, value);
    return this;
  }

  public MockAppConfig set(String key, int value) {
    config.set(key, value);
    return this;
  }

  public MockAppConfig set(String key, long value) {
    config.set(key, value);
    return this;
  }

  public MockAppConfig set(String key, double value) {
    config.set(key, value);
    return this;
  }
}
