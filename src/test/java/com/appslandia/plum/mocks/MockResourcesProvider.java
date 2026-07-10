// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import java.util.Locale;
import java.util.Map;

import com.appslandia.common.base.InitializingException;
import com.appslandia.plum.base.Resources;
import com.appslandia.plum.base.ResourcesProvider;

/**
 *
 * @author Loc Ha
 *
 */
public class MockResourcesProvider extends ResourcesProvider {

  final Locale locale;

  public MockResourcesProvider() {
    locale = Locale.getDefault();
  }

  @Override
  protected Resources loadResources(Locale locale) throws InitializingException {
    return new MockResources();
  }

  class MockResources implements Resources {

    @Override
    public String get(Object key) {
      return locale.getLanguage() + ":" + key;
    }

    @Override
    public String get(String key, Object... params) {
      return locale.getLanguage() + ":" + key + "[]";
    }

    @Override
    public String get(String key, Map<String, Object> params) {
      return locale.getLanguage() + ":" + key + "{}";
    }
  }
}
