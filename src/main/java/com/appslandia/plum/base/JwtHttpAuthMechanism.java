// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import jakarta.security.enterprise.credential.Credential;

/**
 * The implementation must be exposed as a CDI bean with @ApplicationScoped and annotated with @MappedID using the using
 * the target module name.
 *
 * @author Loc Ha
 *
 */
public abstract class JwtHttpAuthMechanism extends BearerHttpAuthMechanism {

  @Override
  protected Credential doParseCredential(String credential) throws Exception {
    return new JwtCredential(credential);
  }
}
