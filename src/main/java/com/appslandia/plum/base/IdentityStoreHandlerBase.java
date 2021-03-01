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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.CredentialValidationResult.Status;
import javax.security.enterprise.identitystore.IdentityStore;
import javax.security.enterprise.identitystore.IdentityStore.ValidationType;
import javax.security.enterprise.identitystore.IdentityStoreHandler;

import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.logging.AppLogger;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class IdentityStoreHandlerBase extends InitializeObject implements IdentityStoreHandler {

	@Inject
	protected AppLogger appLogger;

	protected List<IdentityStore> identityStores = new ArrayList<>();

	@Override
	protected void init() throws Exception {
		this.identityStores = Collections.unmodifiableList(this.identityStores);
	}

	@Override
	public CredentialValidationResult validate(Credential credential) {
		this.initialize();

		// Not AuthCredential?
		if (!(credential instanceof AuthCredential)) {
			this.appLogger.warn("HttpAuthenticationMechanismBase is not being used. Returning CredentialValidationResult.NOT_VALIDATED_RESULT");

			return CredentialValidationResult.NOT_VALIDATED_RESULT;
		}

		AuthCredential authCredential = (AuthCredential) credential;
		CredentialValidationResult result = null;

		// RULES: First store that returns Status is not NOT_VALIDATED will be used
		// Only IdentityStoreBase validated

		for (IdentityStore store : this.identityStores) {
			if (!(store instanceof IdentityStoreBase)) {
				continue;
			}
			if (!store.validationTypes().contains(ValidationType.VALIDATE)) {
				continue;
			}

			// Validate authCredential
			result = store.validate(authCredential);

			if (result.getStatus() != Status.NOT_VALIDATED) {
				break;
			}
		}

		// NOT_VALIDATED
		if ((result == null) || (result.getStatus() == Status.NOT_VALIDATED)) {
			this.appLogger.warn("No identity store found for validating credential type: " + credential.getClass().getName());

			return CredentialValidationResult.NOT_VALIDATED_RESULT;
		}

		// INVALID
		if (result.getStatus() == Status.INVALID) {
			return result;
		}
		UserPrincipalWrapper principalWrapper = new UserPrincipalWrapper((UserPrincipal) result.getCallerPrincipal(), authCredential);
		return new CredentialValidationResult(principalWrapper, result.getCallerGroups());
	}
}
