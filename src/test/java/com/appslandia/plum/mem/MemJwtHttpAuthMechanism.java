// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mem;

import com.appslandia.plum.base.JwtHttpAuthMechanism;

/**
 *
 * @author Loc Ha
 *
 */
public class MemJwtHttpAuthMechanism extends JwtHttpAuthMechanism {

  @Override
  public String getModule() {
    return MemModules.MEM_JWT;
  }
}
