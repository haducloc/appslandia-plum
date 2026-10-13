// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mem;

import com.appslandia.plum.base.FormHttpAuthMechanism;

/**
 *
 * @author Loc Ha
 *
 */
public class MemFormHttpAuthMechanism extends FormHttpAuthMechanism {

  @Override
  public String getModule() {
    return MemModules.MEM_FORM;
  }
}
