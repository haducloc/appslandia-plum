// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import com.appslandia.common.base.Language;
import com.appslandia.plum.base.LanguageProvider;

/**
 *
 * @author Loc Ha
 *
 */
public class MockLanguageProvider extends LanguageProvider {

  @Override
  protected void init() throws Exception {
    registerLanguages(new Language[] { Language.EN_US });
    super.init();
  }
}
