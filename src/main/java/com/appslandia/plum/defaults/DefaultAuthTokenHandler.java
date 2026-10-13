// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.common.crypto.EncodedVerifier;
import com.appslandia.plum.base.AuthTokenHandler;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultAuthTokenHandler extends AuthTokenHandler {

  @Override
  protected EncodedVerifier getCodeVerifier(String module, String keyFamily) {
    throw new UnsupportedOperationException("DefaultAuthTokenHandler.getCodeVerifier(module, keyFamily).");
  }

  @Override
  protected EncodedVerifier getIntegrityVerifier(String module, String keyFamily) {
    throw new UnsupportedOperationException("DefaultAuthTokenHandler.getIntegrityVerifier(module, keyFamily).");
  }
}
