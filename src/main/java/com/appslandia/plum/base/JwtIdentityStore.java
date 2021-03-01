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

import java.util.HashSet;
import java.util.List;

import javax.security.enterprise.credential.Credential;

import com.appslandia.common.base.Out;
import com.appslandia.common.jwt.JwtToken;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class JwtIdentityStore extends IdentityStoreBase {

	protected abstract JwtToken parseJwtToken(String credential);

	@Override
	public abstract Class<? extends JwtCredential> getCredentialType();

	@SuppressWarnings("unchecked")
	@Override
	protected PrincipalGroups doValidate(Credential credential, Out<String> failureCode) {
		JwtCredential jwtCredential = (JwtCredential) credential;

		// JwtToken
		JwtToken token = null;
		try {
			token = parseJwtToken(jwtCredential.getToken());

		} catch (Exception ex) {
			failureCode.value = AuthFailureResult.CREDENTIAL_INVALID.getFailureCode();
			return null;
		}

		// PrincipalGroups
		List<String> roles = (List<String>) token.getPayload().get(JwtPrincipal.KEY_USER_ROLES);
		return new PrincipalGroups(new JwtPrincipal(token), (roles != null) ? new HashSet<>(roles) : null);
	}
}
