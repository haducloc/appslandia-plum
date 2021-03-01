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

import java.util.Set;

import javax.security.enterprise.CallerPrincipal;
import javax.security.enterprise.identitystore.CredentialValidationResult;

import com.appslandia.common.utils.AssertUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class AuthFailureResult extends CredentialValidationResult {

	static final CallerPrincipal INVALID_PRINCIPAL = new CallerPrincipal("invalid");
	static final CredentialValidationResult INVALID_RESULT = CredentialValidationResult.INVALID_RESULT;

	final String failureCode;

	private AuthFailureResult(String failureCode) {
		// DON'T WORK: super((String) null, null, null, null, null);
		super((String) null, INVALID_PRINCIPAL, null, null, null);

		this.failureCode = AssertUtils.assertNotNull(failureCode);
	}

	@Override
	public Status getStatus() {
		return INVALID_RESULT.getStatus();
	}

	@Override
	public String getIdentityStoreId() {
		return INVALID_RESULT.getIdentityStoreId();
	}

	@Override
	public CallerPrincipal getCallerPrincipal() {
		return INVALID_RESULT.getCallerPrincipal();
	}

	@Override
	public String getCallerUniqueId() {
		return INVALID_RESULT.getCallerUniqueId();
	}

	@Override
	public String getCallerDn() {
		return INVALID_RESULT.getCallerDn();
	}

	@Override
	public Set<String> getCallerGroups() {
		return INVALID_RESULT.getCallerGroups();
	}

	public String getFailureCode() {
		return this.failureCode;
	}

	public static final AuthFailureResult CREDENTIAL_INVALID = new AuthFailureResult("credential_invalid");
	public static final AuthFailureResult PASSWORD_INVALID = new AuthFailureResult("password_invalid");

	public static final AuthFailureResult CREDENTIAL_SUSPENDED = new AuthFailureResult("credential_suspended");
	public static final AuthFailureResult CREDENTIAL_NOT_ACTIVATED = new AuthFailureResult("credential_not_activated");
	public static final AuthFailureResult CREDENTIAL_NOT_APPROVED = new AuthFailureResult("credential_not_approved");
	public static final AuthFailureResult CREDENTIAL_INVALID_STATE = new AuthFailureResult("credential_invalid_state");

	public static final AuthFailureResult TOKEN_INVALID = new AuthFailureResult("token_invalid");
	public static final AuthFailureResult TOKEN_THEFTED = new AuthFailureResult("token_thefted");
	public static final AuthFailureResult TOKEN_EXPIRED = new AuthFailureResult("token_expired");

	public static final AuthFailureResult ID_STORE_EXCEPTION = new AuthFailureResult("id_store_exception");

	public static AuthFailureResult valueOf(String failureCode) {
		AssertUtils.assertNotNull(failureCode);

		if (CREDENTIAL_INVALID.getFailureCode().equalsIgnoreCase(failureCode)) {
			return CREDENTIAL_INVALID;
		}
		if (PASSWORD_INVALID.getFailureCode().equalsIgnoreCase(failureCode)) {
			return PASSWORD_INVALID;
		}

		if (CREDENTIAL_SUSPENDED.getFailureCode().equalsIgnoreCase(failureCode)) {
			return CREDENTIAL_SUSPENDED;
		}
		if (CREDENTIAL_NOT_ACTIVATED.getFailureCode().equalsIgnoreCase(failureCode)) {
			return CREDENTIAL_NOT_ACTIVATED;
		}
		if (CREDENTIAL_NOT_APPROVED.getFailureCode().equalsIgnoreCase(failureCode)) {
			return CREDENTIAL_NOT_APPROVED;
		}
		if (CREDENTIAL_INVALID_STATE.getFailureCode().equalsIgnoreCase(failureCode)) {
			return CREDENTIAL_INVALID_STATE;
		}

		if (TOKEN_INVALID.getFailureCode().equalsIgnoreCase(failureCode)) {
			return TOKEN_INVALID;
		}
		if (TOKEN_THEFTED.getFailureCode().equalsIgnoreCase(failureCode)) {
			return TOKEN_THEFTED;
		}
		if (TOKEN_EXPIRED.getFailureCode().equalsIgnoreCase(failureCode)) {
			return TOKEN_EXPIRED;
		}

		if (ID_STORE_EXCEPTION.getFailureCode().equalsIgnoreCase(failureCode)) {
			return ID_STORE_EXCEPTION;
		}
		return new AuthFailureResult(failureCode);
	}
}
