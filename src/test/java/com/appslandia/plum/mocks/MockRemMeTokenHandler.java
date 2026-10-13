// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import com.appslandia.common.crypto.Base64Verifier;
import com.appslandia.common.crypto.DigesterImpl;
import com.appslandia.common.crypto.EncodedVerifier;
import com.appslandia.common.crypto.MacVerifier;
import com.appslandia.plum.remme.RemMeTokenHandler;

/**
 *
 * @author Loc Ha
 *
 */
public class MockRemMeTokenHandler extends RemMeTokenHandler {

  final EncodedVerifier tokenVerifier = new Base64Verifier().setVerifier(new DigesterImpl("SHA-256"));

  final EncodedVerifier integrityVerifier = new Base64Verifier()
      .setVerifier(new MacVerifier().setAlgorithm("HmacSHA256").setSecret("secret1".getBytes()));

  @Override
  protected EncodedVerifier getTokenVerifier(String module, String keyFamily) {
    return tokenVerifier;
  }

  @Override
  protected EncodedVerifier getIntegrityVerifier(String module, String keyFamily) {
    return integrityVerifier;
  }
}
