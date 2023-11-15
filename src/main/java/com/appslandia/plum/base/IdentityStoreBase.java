// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.appslandia.plum.base;

import com.appslandia.common.base.Out;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.utils.SecurityUtils;

import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class IdentityStoreBase implements IdentityStore {

  public abstract Class<? extends Credential> getAcceptedCredentialType();

  @Override
  public CredentialValidationResult validate(Credential credential) {
    // Not AuthCredential
    if (!(credential instanceof AuthCredential)) {
      return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }
    AuthCredential authCredential = (AuthCredential) credential;

    // Check credential type
    if (getAcceptedCredentialType() != authCredential.getCredential().getClass()) {
      return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }

    // Validate credential
    Out<String> invalidCode = new Out<>();
    PrincipalRoles principalRoles = doValidate(authCredential.getModule(), authCredential.getCredential(), invalidCode);

    if (principalRoles == null) {
      String code = Asserts.notNull(invalidCode.value, "invalidCode is required.");
      return InvalidAuthResult.valueOf(code);
    }
    return SecurityUtils.createIdentityStoreResult(principalRoles.getPrincipal(), principalRoles.getRoles());
  }

  protected abstract PrincipalRoles doValidate(String module, Credential credential, Out<String> invalidCode);
}
