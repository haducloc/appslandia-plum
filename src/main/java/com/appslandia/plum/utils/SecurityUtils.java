// The MIT License (MIT)
// Copyright © 2015 Loc Ha

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.appslandia.plum.utils;

import java.util.Set;

import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.SplitUtils;

import jakarta.security.enterprise.CallerPrincipal;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;

/**
 *
 * @author Loc Ha
 *
 */
public class SecurityUtils {

  public static CredentialValidationResult createIdentityStoreResult(CallerPrincipal callerPrincipal,
      String userRoles) {
    if (userRoles == null) {
      return new CredentialValidationResult(callerPrincipal, null);
    }
    var roles = parseUserRoles(userRoles);

    if (roles.length > 0) {
      return new CredentialValidationResult(callerPrincipal, CollectionUtils.toSet(roles));
    }
    return new CredentialValidationResult(callerPrincipal, null);
  }

  public static String[] parseUserRoles(String userRoles) {
    return SplitUtils.splitByComma(userRoles);
  }

  public static String toUserRoles(Set<String> userRoles) {
    return (userRoles != null) && !userRoles.isEmpty() ? String.join(",", userRoles) : null;
  }
}
