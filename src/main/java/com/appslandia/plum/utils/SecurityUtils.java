// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.utils;

import java.util.Collections;
import java.util.Set;

import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.SplitUtils;
import com.appslandia.common.utils.StringUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class SecurityUtils {

  public static String[] parseUserRoles(String userRoles) {
    return (userRoles != null) ? SplitUtils.splitByComma(userRoles) : StringUtils.EMPTY_ARRAY;
  }

  public static Set<String> toUserRoles(String userRoles) {
    if (userRoles == null) {
      return Collections.emptySet();
    }
    var roles = parseUserRoles(userRoles);
    return CollectionUtils.toUnmodifiableSet(roles);
  }

  public static String toUserRoles(Set<String> userRoles) {
    return (userRoles != null && !userRoles.isEmpty()) ? String.join(",", userRoles) : null;
  }
}
