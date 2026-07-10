// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mem;

import java.util.Set;

import com.appslandia.common.jose.JwtSigner;
import com.appslandia.common.jose.JwtToken;
import com.appslandia.plum.base.JwtIdentityStore;

import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
public class MemJwtIdentityStore extends JwtIdentityStore {

  @Inject
  @MemVersion
  protected JwtSigner jwtSigner;

  private static final Set<String> VALIDATION_MODULES = Set.of(MemModules.MEM_JWT);

  @Override
  public String storeId() {
    return "MemJwtStore";
  }

  @Override
  public Set<String> validationModules() {
    return VALIDATION_MODULES;
  }

  @Override
  protected JwtToken parseJwtToken(String module, String credential) {
    var token = jwtSigner.parse(credential);
    jwtSigner.verify(token);
    return token;
  }
}
