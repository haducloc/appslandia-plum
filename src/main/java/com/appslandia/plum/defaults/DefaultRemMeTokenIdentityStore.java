// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.plum.base.AuthResult;
import com.appslandia.plum.base.RemMeTokenIdentityStore;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultRemMeTokenIdentityStore extends RemMeTokenIdentityStore {

  @Override
  protected AuthResult doValidate(String module, String identity) {
    throw new UnsupportedOperationException("DefaultRemMeTokenIdentityStore.doValidate(module, identity)");
  }
}
