// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.oauth2;

import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.base.AuthResult;
import com.appslandia.plum.base.IdentityStoreBase;

import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class OAuth2IdentityStore extends IdentityStoreBase {

  @Inject
  protected OAuth2Service service;

  @Inject
  protected OAuth2ConfigProvider configProvider;

  protected OAuth2Config getConfig(String provider) {
    return configProvider.getConfig(provider);
  }

  protected abstract AuthResult doValidate(String module, String provider, String subject);

  @Override
  protected AuthResult doValidate(String module, Credential credential) {
    if (!(credential instanceof OAuth2Credential oauth2Cred)) {
      return AuthResult.NOT_VALIDATED_RESULT;
    }

    var provider = oauth2Cred.getProvider();
    var config = getConfig(provider);

    // Load userInfo
    final String userInfo;
    try {
      userInfo = service.getUserInfo(module, provider, oauth2Cred.getAccessToken());

    } catch (OAuth2HttpException ex) {

      if (config.isAccessTokenInvalid(ex)) {
        return AuthResult.toInvalidResult(AuthResult.TOKEN_INVALID);
      }
      throw ex;
    }

    // Parse user
    var user = config.parseUserInfo(userInfo);
    if (user == null) {
      return AuthResult.toInvalidResult(AuthResult.TOKEN_INVALID);
    }

    Asserts.isTrue(oauth2Cred.getSubject().equals(user.getSubject()));
    return doValidate(module, provider, user.getSubject());
  }
}
