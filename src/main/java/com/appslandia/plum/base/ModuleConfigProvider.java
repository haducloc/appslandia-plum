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
public class ModuleConfigProvider extends InitializingObject {

  private String fallbackModule = ModuleConfig.MODULE_NONE;
  private Map<String, ModuleConfig> configMap = new HashMap<>();

  @Override
  protected void init() throws Exception {
    Arguments.isTrue(configMap.containsKey(this.fallbackModule));
    configMap = Collections.unmodifiableMap(configMap);
  }

  public ModuleConfigProvider registerModuleConfig(String module, ModuleConfig impl) {
    assertNotInitialized();
    configMap.put(module, impl);
    return this;
  }

  /**
   * Sets the fallback module to use for resources whose associated MVC module cannot be determined. The default is
   * {@code ModuleConfig.MODULE_NONE}. If you use the {@code ModuleConfig.MODULE_NONE} module, you must register a
   * module config for it. You may also need to define a master layout or error page body for the
   * {@code ModuleConfig.MODULE_NONE} module, as you would for other modules.
   *
   * @param fallbackModule the fallback module
   */
  public ModuleConfigProvider setFallbackModule(String fallbackModule) {
    assertNotInitialized();
    this.fallbackModule = Arguments.notNull(fallbackModule);
    return this;
  }

  public ModuleConfig getModuleConfig(String module) {
    initialize();
    var impl = configMap.get(module);
    return Arguments.notNull(impl, "No ModuleConfig is registered for module '{}'.", module);
  }

  public Map<String, ModuleConfig> getModuleConfigs() {
    initialize();
    return configMap;
  }

  public String getFallbackModule() {
    initialize();
    return fallbackModule;
  }
}
