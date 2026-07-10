// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import com.appslandia.common.base.TextGenerator;
import com.appslandia.common.base.TokenGenerator;
import com.appslandia.common.crypto.PasswordDigesterImpl;
import com.appslandia.common.crypto.TextDigester;
import com.appslandia.plum.base.AuthTokenHandler;

import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultAuthTokenHandler extends AuthTokenHandler {

  final TextGenerator tokenGenerator = new TokenGenerator(64);

  final TextDigester tokenDigester = new PasswordDigesterImpl();

  @Override
  protected TextGenerator getTokenGenerator() {
    return tokenGenerator;
  }

  @Override
  protected TextDigester getTokenDigester() {
    return tokenDigester;
  }
}
