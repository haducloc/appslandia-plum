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

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import javax.inject.Inject;
import javax.security.enterprise.CallerPrincipal;
import javax.security.enterprise.credential.RememberMeCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.RememberMeIdentityStore;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appslandia.common.base.BaseEncoder;
import com.appslandia.common.base.Out;
import com.appslandia.common.base.TextGenerator;
import com.appslandia.common.cdi.Json;
import com.appslandia.common.cdi.Json.Profile;
import com.appslandia.common.crypto.TextDigester;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.DateUtils;
import com.appslandia.common.utils.ValueUtils;
import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class RemMeIdentityStore implements RememberMeIdentityStore {

	@Inject
	protected AppConfig appConfig;

	@Inject
	protected HttpServletRequest request;

	@Inject
	protected RemMeTokenManager remMeTokenManager;

	@Inject
	@Json(Profile.COMPACT)
	protected JsonProcessor jsonProcessor;

	protected abstract TextGenerator getSeriesGenerator();

	protected abstract TextGenerator getTokenGenerator();

	protected abstract TextDigester getTokenDigester();

	protected abstract TextDigester getIdentityDigester();

	@Override
	public String generateLoginToken(CallerPrincipal callerPrincipal, Set<String> groups) {
		UserPrincipal userPrincipal = (UserPrincipal) callerPrincipal;
		String identity = getIdentity(userPrincipal);

		RemMeToken remMeToken = new RemMeToken();
		remMeToken.setSeries(getSeriesGenerator().generate());
		String clearToken = getTokenGenerator().generate();

		final long curTimeMs = System.currentTimeMillis();
		long expiresAt = curTimeMs + this.appConfig.getRequiredInt(AppConfig.CONFIG_REMME_COOKIE_AGE) * 1000L;

		String hashToken = getTokenDigester().digest(getTokenData(remMeToken.getSeries(), clearToken, identity, expiresAt));
		remMeToken.setToken(hashToken);
		remMeToken.setHashIdentity(getIdentityDigester().digest(identity));

		remMeToken.setExpiresAt(expiresAt);
		remMeToken.setIssuedAt(curTimeMs);

		this.remMeTokenManager.save(remMeToken);
		LoginToken loginToken = new LoginToken().setSeries(remMeToken.getSeries()).setToken(clearToken).setModule(userPrincipal.getModule())
				.setIdentity(identity);
		return encodeLoginToken(loginToken);
	}

	protected String getIdentity(UserPrincipal userPrincipal) {
		return userPrincipal.getName();
	}

	protected abstract PrincipalGroups doValidate(String module, String identity, Out<String> failureCode);

	protected void handleTokenTheft(String identity, RemMeToken remMeToken) {
		this.remMeTokenManager.removeAll(remMeToken.getHashIdentity());
	}

	protected long getSlidingExpiresAt(long curExpiresAt, long curTimeMs, int cfgMaxAge) {
		int remainingSec = ValueUtils.valueOrMin((int) ((curExpiresAt - curTimeMs) / 1000L), 0);
		if (remainingSec > cfgMaxAge / 2) {
			return curExpiresAt;
		}
		return curTimeMs + cfgMaxAge * 1000L;
	}

	@Override
	public CredentialValidationResult validate(RememberMeCredential credential) {
		// LoginToken
		LoginToken loginToken = decodeLoginToken(credential.getToken());
		if ((loginToken == null) || (loginToken.getSeries() == null) || (loginToken.getToken() == null) || (loginToken.getModule() == null)
				|| (loginToken.getIdentity() == null)) {
			return AuthFailureResult.TOKEN_INVALID;
		}

		// RemMeToken
		RemMeToken remMeToken = this.remMeTokenManager.load(loginToken.getSeries());
		if (remMeToken == null) {
			return AuthFailureResult.TOKEN_INVALID;
		}

		// Identity
		if (!getIdentityDigester().verify(loginToken.getIdentity(), remMeToken.getHashIdentity())) {
			return AuthFailureResult.TOKEN_INVALID;
		}

		// Token
		if (!getTokenDigester().verify(getTokenData(loginToken.getSeries(), loginToken.getToken(), loginToken.getIdentity(), remMeToken.getExpiresAt()),
				remMeToken.getToken())) {
			this.request.setAttribute(RemMeToken.class.getName(), remMeToken);

			handleTokenTheft(loginToken.getIdentity(), remMeToken);
			return AuthFailureResult.TOKEN_THEFTED;
		}

		// ExpiresAt
		if (!DateUtils.isFutureTime(remMeToken.getExpiresAt(), 0)) {
			return AuthFailureResult.TOKEN_EXPIRED;
		}

		// Validate identity
		Out<String> failureCode = new Out<>();
		PrincipalGroups principalGroups = doValidate(loginToken.getModule(), loginToken.getIdentity(), failureCode);

		if (principalGroups == null) {
			return AuthFailureResult.valueOf(failureCode.val());
		}

		// CredentialValidationResult
		UserPrincipalWrapper principalWrapper = new UserPrincipalWrapper(principalGroups.getPrincipal(), loginToken.getModule(), true, false);
		CredentialValidationResult result = new CredentialValidationResult(principalWrapper, principalGroups.getGroups());

		// Reissue Token
		final long curTimeMs = System.currentTimeMillis();
		String clearToken = getTokenGenerator().generate();

		long newExpiresAt = this.appConfig.getRequiredBool(AppConfig.CONFIG_REMME_COOKIE_SLIDING)
				? getSlidingExpiresAt(remMeToken.getExpiresAt(), curTimeMs, this.appConfig.getRequiredInt(AppConfig.CONFIG_REMME_COOKIE_AGE))
				: remMeToken.getExpiresAt();

		String hashToken = getTokenDigester().digest(getTokenData(loginToken.getSeries(), clearToken, loginToken.getIdentity(), newExpiresAt));
		this.remMeTokenManager.reissue(remMeToken.getSeries(), hashToken, newExpiresAt, curTimeMs);

		int remainingSec = ValueUtils.valueOrMin((int) ((newExpiresAt - curTimeMs) / 1000L), 0);
		String newLoginToken = encodeLoginToken(new LoginToken().setSeries(loginToken.getSeries()).setToken(clearToken).setModule(loginToken.getModule())
				.setIdentity(loginToken.getIdentity()));

		this.request.setAttribute(RemMeCookieUpdater.class.getName(), createRemMeCookieUpdater(newLoginToken, remainingSec));
		this.request.setAttribute(LoginToken.class.getName(), loginToken.getIdentity());
		return result;
	}

	@Override
	public void removeLoginToken(String token) {
		LoginToken loginToken = decodeLoginToken(token);
		if ((loginToken != null) && (loginToken.getSeries() != null)) {
			this.remMeTokenManager.remove(loginToken.getSeries());
		}
	}

	protected String getTokenData(String series, String token, String identity, long expiresAt) {
		return series + '|' + token + '|' + identity + '|' + expiresAt;
	}

	protected String encodeLoginToken(LoginToken token) {
		return BaseEncoder.BASE64_URL_NP.encode(this.jsonProcessor.toString(token).getBytes(StandardCharsets.UTF_8));
	}

	protected LoginToken decodeLoginToken(String token) {
		try {
			return this.jsonProcessor.read(new StringReader(new String(BaseEncoder.BASE64_URL_NP.decode(token), StandardCharsets.UTF_8)), LoginToken.class);
		} catch (Exception ex) {
			return null;
		}
	}

	protected RemMeCookieUpdater createRemMeCookieUpdater(String newLoginToken, int maxAgeSec) {
		return new RemMeCookieUpdater() {

			@Override
			public void apply(HttpServletRequest request, HttpServletResponse response) {
				Cookie cookie = new Cookie(appConfig.getRequiredString(AppConfig.CONFIG_REMME_COOKIE_NAME), newLoginToken);
				cookie.setMaxAge(maxAgeSec);

				String domain = request.getServletContext().getSessionCookieConfig().getDomain();
				if (domain != null) {
					cookie.setDomain(domain);
				}
				cookie.setPath(ServletUtils.getCookiePath(request.getServletContext()));
				cookie.setSecure(appConfig.getRequiredBool(AppConfig.CONFIG_REMME_COOKIE_SECURE));
				cookie.setHttpOnly(appConfig.getRequiredBool(AppConfig.CONFIG_REMME_COOKIE_HTTPONLY));

				response.addCookie(cookie);
			}
		};
	}

	public interface RemMeCookieUpdater {

		void apply(HttpServletRequest request, HttpServletResponse response);
	}
}
