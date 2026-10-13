// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

import com.appslandia.common.crypto.EncodedVerifier;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultOAuth2StateHandler extends OAuth2StateHandler {

  @Override
  protected EncodedVerifier getIntegrityVerifier(String module, String keyFamily) {
    throw new UnsupportedOperationException("DefaultOAuth2StateHandler.getIntegrityVerifier(module, keyFamily).");
  }
}
