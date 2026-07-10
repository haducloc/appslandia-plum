// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.caching;

import com.appslandia.common.utils.STR;

/**
 *
 * @author Loc Ha
 *
 */
public class CacheUtils {

  public static String toKey(String key, Object... params) {

    return STR.format(key, params);
  }
}
