// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import java.util.Map;

import com.appslandia.common.base.InitializingException;
import com.appslandia.common.base.Language;
import com.appslandia.plum.base.Resources;
import com.appslandia.plum.base.ResourcesProvider;

/**
 *
 * @author Loc Ha
 *
 */
public class MockResourcesProvider extends ResourcesProvider {

  @Override
  protected Resources loadResources(String module, Language language) throws InitializingException {
    return new MockResources(language);
  }

  class MockResources implements Resources {

    final Language language;

    public MockResources(Language language) {
      this.language = language;
    }

    @Override
    public String get(Object key) {
      return language.getId() + ":" + key;
    }

    @Override
    public String get(String key, Object... params) {
      return language.getId() + ":" + key + "[]";
    }

    @Override
    public String get(String key, Map<String, Object> params) {
      return language.getId() + ":" + key + "{}";
    }
  }
}
