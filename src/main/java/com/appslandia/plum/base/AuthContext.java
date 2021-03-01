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

import java.security.Principal;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appslandia.common.base.InitializeException;
import com.appslandia.common.base.Out;
import com.appslandia.common.utils.AssertUtils;
import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class AuthContext {

	@SuppressWarnings("cdi-ambiguous-dependency")
	@Inject
	protected SecurityContext securityContext;

	@Inject
	protected VerifyService verifyService;

	@Inject
	protected Instance<HttpAuthenticationMechanismBase> authenticationMechanism;

	@PostConstruct
	protected void initialize() {
		if (!this.authenticationMechanism.isResolvable()) {
			throw new InitializeException("HttpAuthenticationMechanismBase is not resolvable.");
		}
	}

	public <T extends UsernamePasswordCredential> boolean authenticate(HttpServletRequest request, HttpServletResponse response, T credential,
			boolean rememberMe, Out<String> failureCode) {
		return authenticate(request, response, new AuthParameters().credential(credential).rememberMe(rememberMe), failureCode);
	}

	public boolean authenticate(HttpServletRequest request, HttpServletResponse response, AuthParameters authParameters, Out<String> failureCode) {
		AuthenticationStatus status = this.securityContext.authenticate(request, response, authParameters);

		CredentialValidationResult failureResult = (CredentialValidationResult) request.getAttribute(CredentialValidationResult.class.getName());
		if (failureResult != null) {

			if (failureResult instanceof AuthFailureResult) {
				failureCode.value = ((AuthFailureResult) failureResult).getFailureCode();

			} else {
				failureCode.value = AuthFailureResult.CREDENTIAL_INVALID.getFailureCode();
			}
			return false;
		}
		return status == AuthenticationStatus.SUCCESS;
	}

	public boolean reauthenticate(HttpServletRequest request, HttpServletResponse response, ReauthByCodeCredential credential, Out<String> failureCode)
			throws ServletException {
		UserPrincipal principal = ServletUtils.getRequiredPrincipal(request);

		if (this.verifyService.verifyToken(credential.getSeries(), credential.getToken(), credential.getIdentity(), credential.getVerifyCode(), 0,
				failureCode)) {
			return false;
		}

		// REAUTHENTICATE
		boolean rememberMe = principal.isRememberMe();
		request.logout();

		AuthParameters authParameters = new AuthParameters().credential(credential).rememberMe(rememberMe).reauthentication(true);
		return authenticate(request, response, authParameters, failureCode);
	}

	public boolean isCallerInRoles(String... roles) {
		AssertUtils.assertHasElements(roles);
		return Arrays.stream(roles).anyMatch(role -> this.securityContext.isCallerInRole(role));
	}

	public UserPrincipal getUserPrincipal() {
		Principal principal = this.securityContext.getCallerPrincipal();
		if (principal == null) {
			return null;
		}
		if (!(principal instanceof UserPrincipal)) {
			throw new IllegalStateException("securityContext.getCallerPrincipal() must be UserPrincipal.");
		}
		return (UserPrincipal) principal;
	}

	public UserPrincipal getRequiredPrincipal() {
		return AssertUtils.assertStateNotNull(getUserPrincipal(), "securityContext.getCallerPrincipal() must be not null.");
	}

	public int getUserId() {
		return getRequiredPrincipal().getUserId();
	}
}
