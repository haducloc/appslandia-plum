// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mem;

import com.appslandia.plum.base.BasicHttpAuthMechanism;

/**
 *
 * @author Loc Ha
 *
 */
public class MemBasicHttpAuthMechanism extends BasicHttpAuthMechanism {

  @Override
  public String getModule() {
    return MemModules.MEM_BASIC;
  }
}
