// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.STR;

/**
 *
 * @author Loc Ha
 *
 */
public class ResourcesImpl implements Resources {

  final Properties res;
  final Locale locale;

  public ResourcesImpl(Properties res, Locale locale) {
    this.res = res;
    this.locale = locale;
  }

  public Locale getLocale() {
    return locale;
  }

  @Override
  public String get(Object key) {
    Arguments.notNull(key);
    var value = (String) res.get(key);
    if (value == null) {
      return locale.getLanguage() + ":" + key;
    }
    return value;
  }

  @Override
  public String get(String key, Object... params) {
    Arguments.notNull(key);

    var value = (String) res.get(key);
    if (value == null) {
      return locale.getLanguage() + ":" + key;
    }
    return STR.format(value, params);
  }

  @Override
  public String get(String key, Map<String, Object> params) {
    Arguments.notNull(key);

    var value = (String) res.get(key);
    if (value == null) {
      return locale.getLanguage() + ":" + key;
    }
    return STR.format(value, params);
  }
}
