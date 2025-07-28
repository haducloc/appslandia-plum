// The MIT License (MIT)
// Copyright © 2015 Loc Ha

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

import static jakarta.security.enterprise.identitystore.IdentityStore.ValidationType.VALIDATE;

import java.util.EnumSet;
import java.util.Set;

import com.appslandia.common.base.Out;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.utils.SecurityUtils;

import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class IdentityStoreBase implements IdentityStore {

  protected abstract Set<String> validationModules();

  @Override
  public CredentialValidationResult validate(Credential credential) {
    // AuthCredential
    if (!(credential instanceof AuthCredential authCredential)) {
      throw new IllegalArgumentException(
          "The given credential must be an instance of com.appslandia.plum.base.AuthCredential.");
    }

    // Validate module
    if (!validationModules().contains(authCredential.getModule())) {
      return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }

    // Validate credential
    var invalidCode = new Out<String>();
    var rolesPrincipal = doValidate(authCredential.getModule(), authCredential.getCredential(), invalidCode);

    if (rolesPrincipal == null) {
      var code = Asserts.notNull(invalidCode.value);
      return InvalidAuth.valueOf(code);
    }
    return SecurityUtils.createIdentityStoreResult(rolesPrincipal.getPrincipal(), rolesPrincipal.getRoles());
  }

  protected abstract RolesPrincipal doValidate(String module, Credential credential, Out<String> invalidCode);

  private static final Set<ValidationType> VALIDATION_TYPE_ONLY = EnumSet.of(VALIDATE);

  @Override
  public Set<ValidationType> validationTypes() {
    return VALIDATION_TYPE_ONLY;
  }
}
