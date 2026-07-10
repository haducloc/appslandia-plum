// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.caching;

import java.io.Serializable;
import java.util.Set;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.CollectionUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class CacheChange implements Serializable {
  private static final long serialVersionUID = 1L;

  final String cacheName;
  final Set<String> keys;

  public CacheChange(String cacheName, String[] keys) {
    Arguments.notNull(cacheName);
    Arguments.notNull(keys);

    this.cacheName = cacheName;
    this.keys = CollectionUtils.toUnmodifiableSet(keys);
  }

  public String getCacheName() {
    return cacheName;
  }

  public Set<String> getKeys() {
    return keys;
  }
}
