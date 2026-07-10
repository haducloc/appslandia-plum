// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.appslandia.common.base.Config;
import com.appslandia.common.base.MapWrapper;
import com.appslandia.common.utils.CollectionUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class PrefCookie extends MapWrapper<String, String> implements Config {
  private static final long serialVersionUID = 1L;

  public static final String PREF_LANGUAGE_ID = "__language_id";

  public static final PrefCookie EMPTY = new PrefCookie(Collections.emptyMap());

  public PrefCookie(Map<String, String> prefMap) {
    super(CollectionUtils.toUnmodifiableMap(prefMap));
  }

  @Override
  public Iterator<String> getKeys() {
    return map.keySet().iterator();
  }

  @Override
  public String getString(String key) {
    return map.get(key);
  }

  public PrefCookie copyWith(Consumer<Map<String, String>> prefMapConsumer) {
    Map<String, String> prefMap = new LinkedHashMap<>(this.map);
    prefMapConsumer.accept(prefMap);

    return new PrefCookie(prefMap);
  }
}
