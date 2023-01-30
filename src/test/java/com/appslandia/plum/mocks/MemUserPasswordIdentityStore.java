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

package com.appslandia.plum.mocks;

import com.appslandia.common.base.Out;
import com.appslandia.plum.base.IdentityStoreBase;
import com.appslandia.plum.base.InvalidAuthResult;
import com.appslandia.plum.base.PrincipalRoles;

import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class MemUserPasswordIdentityStore extends IdentityStoreBase {

    @Inject
    protected MemUserDatabase memUserDatabase;

    @Override
    public Class<? extends Credential> getAcceptedCredentialType() {
	return MemUserPasswordCredential.class;
    }

    @Override
    protected PrincipalRoles doValidate(String module, Credential credential, Out<String> invalidCode) {
	MemUserPasswordCredential usernamePasswordCredential = (MemUserPasswordCredential) credential;
	MemUser user = this.memUserDatabase.getUser(usernamePasswordCredential.getCaller());

	if (user == null) {
	    invalidCode.value = InvalidAuthResult.CREDENTIAL_INVALID.getCode();
	    return null;
	}

	// Password
	if (!this.memUserDatabase.verifyPassword(usernamePasswordCredential.getPasswordAsString(), user.getPassword())) {
	    invalidCode.value = InvalidAuthResult.CREDENTIAL_INVALID.getCode();
	    return null;
	}
	return new PrincipalRoles(new MemPrincipal(user), user.getRoles());
    }
}
