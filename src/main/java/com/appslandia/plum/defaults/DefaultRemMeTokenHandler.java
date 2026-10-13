// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.common.crypto.EncodedVerifier;
import com.appslandia.plum.remme.RemMeTokenHandler;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultRemMeTokenHandler extends RemMeTokenHandler {

  @Override
  protected EncodedVerifier getTokenVerifier(String module, String keyFamily) {
    throw new UnsupportedOperationException("DefaultRemMeTokenHandler.getTokenVerifier(module, keyFamily).");
  }

  @Override
  protected EncodedVerifier getIntegrityVerifier(String module, String keyFamily) {
    throw new UnsupportedOperationException("DefaultRemMeTokenHandler.getIntegrityVerifier(module, keyFamily).");
  }
}
