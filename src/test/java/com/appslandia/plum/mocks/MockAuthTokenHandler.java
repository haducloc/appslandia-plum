// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import com.appslandia.common.crypto.Base64Verifier;
import com.appslandia.common.crypto.EncodedVerifier;
import com.appslandia.common.crypto.MacVerifier;
import com.appslandia.plum.base.AuthTokenHandler;

/**
 *
 * @author Loc Ha
 *
 */
public class MockAuthTokenHandler extends AuthTokenHandler {

  final EncodedVerifier integrityVerifier = new Base64Verifier()
      .setVerifier(new MacVerifier().setAlgorithm("HmacSHA256").setSecret("secret1".getBytes()));

  final EncodedVerifier codeVerifier = new Base64Verifier()
      .setVerifier(new MacVerifier().setAlgorithm("HmacSHA256").setSecret("secret2".getBytes()));

  @Override
  protected EncodedVerifier getIntegrityVerifier(String module, String keyFamily) {
    return integrityVerifier;
  }

  @Override
  protected EncodedVerifier getCodeVerifier(String module, String keyFamily) {
    return codeVerifier;
  }
}
