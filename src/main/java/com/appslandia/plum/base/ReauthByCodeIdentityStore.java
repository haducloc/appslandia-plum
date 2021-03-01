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

import javax.inject.Inject;
import javax.security.enterprise.credential.Credential;

import com.appslandia.common.base.Out;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class ReauthByCodeIdentityStore extends IdentityStoreBase {

	public static final int DEFAULT_EXPIRY_LEEWAY_MS = 1000;
	public static final String CONFIG_EXPIRY_LEEWAY_MS = ReauthByCodeIdentityStore.class.getName() + ".expiry_leeway_ms";

	@Inject
	protected AppConfig appConfig;

	@Inject
	protected VerifyService verifyService;

	protected int getExpiryLeewayMs() {
		return this.appConfig.getInt(CONFIG_EXPIRY_LEEWAY_MS, DEFAULT_EXPIRY_LEEWAY_MS);
	}

	@Override
	public abstract Class<? extends ReauthByCodeCredential> getCredentialType();

	protected abstract PrincipalGroups doValidate(String identity, Out<String> failureCode);

	@Override
	protected PrincipalGroups doValidate(Credential credential, Out<String> failureCode) {
		ReauthByCodeCredential reauthByCodeCredential = (ReauthByCodeCredential) credential;

		// Verify reauthByCodeCredential
		if (!this.verifyService.verifyToken(reauthByCodeCredential.getSeries(), reauthByCodeCredential.getToken(), reauthByCodeCredential.getIdentity(),
				reauthByCodeCredential.getVerifyCode(), getExpiryLeewayMs(), failureCode)) {
			return null;
		}

		// Validate identity
		PrincipalGroups principalGroups = doValidate(reauthByCodeCredential.getIdentity(), failureCode);
		if (principalGroups == null) {
			return null;
		}
		return new PrincipalGroups(principalGroups.getPrincipal(), principalGroups.getGroups());
	}
}
