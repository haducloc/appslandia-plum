// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import com.appslandia.common.base.Language;
import com.appslandia.plum.base.ModuleConfig;
import com.appslandia.plum.base.ModuleConfigProvider;
import com.appslandia.plum.mem.MemModules;

/**
 *
 * @author Loc Ha
 *
 */
public class MockModuleConfigProvider extends ModuleConfigProvider {

  @Override
  protected void init() throws Exception {
    registerModuleConfig(MemModules.MEM_FORM, new ModuleConfig().registerLanguages(new Language[] { Language.EN_US }));
    registerModuleConfig(MemModules.MEM_BASIC, new ModuleConfig().registerLanguages(new Language[] { Language.EN_US }));
    registerModuleConfig(MemModules.MEM_JWT, new ModuleConfig().registerLanguages(new Language[] { Language.EN_US }));

    registerModuleConfig(ModuleConfig.MODULE_NONE,
        new ModuleConfig().registerLanguages(new Language[] { Language.EN_US }));

    setFallbackModule(ModuleConfig.MODULE_NONE);
    super.init();
  }
}
