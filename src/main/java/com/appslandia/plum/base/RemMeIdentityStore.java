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

import com.appslandia.common.base.BaseEncoder;
import com.appslandia.common.base.Out;
import com.appslandia.common.cdi.Json;
import com.appslandia.common.cdi.Json.Profile;
import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.DateUtils;
import com.appslandia.common.utils.ValueUtils;
import com.appslandia.plum.utils.SecurityUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.CallerPrincipal;
import jakarta.security.enterprise.credential.RememberMeCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.RememberMeIdentityStore;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class RemMeIdentityStore implements RememberMeIdentityStore {

    @Inject
    protected AppConfig appConfig;

    @Inject
    protected HttpServletRequest request;

    @Inject
    protected AuthTokenManager authTokenManager;

    @Inject
    protected AuthTokenHandler authTokenHandler;

    @Inject
    @Json(Profile.COMPACT)
    protected JsonProcessor jsonProcessor;

    @Inject
    protected IdentityValidator identityValidator;

    protected String getIdentity(UserPrincipal userPrincipal) {
	return userPrincipal.getName();
    }

    @Override
    public String generateLoginToken(CallerPrincipal callerPrincipal, Set<String> groups) {
	// UserPrincipal
	UserPrincipal userPrincipal = (UserPrincipal) callerPrincipal;
	String identity = getIdentity(userPrincipal);

	// AuthToken
	AuthToken authToken = new AuthToken();
	authToken.setSeries(this.authTokenHandler.getSeriesGenerator().generate());
	String clearToken = this.authTokenHandler.getTokenGenerator().generate();

	final long curTimeMs = System.currentTimeMillis();
	long expiresAt = curTimeMs + this.appConfig.getRequiredInt(AppConfig.CONFIG_REMME_COOKIE_AGE) * 1000L;

	String tokenData = this.authTokenHandler.getTokenData(authToken.getSeries(), clearToken, identity, expiresAt, null);
	String hashToken = this.authTokenHandler.getTokenDigester().digest(tokenData);

	authToken.setHashToken(hashToken);
	authToken.setHashIdentity(this.authTokenHandler.getIdentityDigester().digest(identity));

	authToken.setExpiresAt(expiresAt);
	authToken.setIssuedAt(curTimeMs);

	this.authTokenManager.save(authToken);

	LoginToken loginToken = new LoginToken().setSeries(authToken.getSeries()).setToken(clearToken).setModule(userPrincipal.getModule()).setIdentity(identity);
	return encodeLoginToken(loginToken);
    }

    protected PrincipalRoles doValidate(String module, String identity, Out<String> invalidCode) {
	return this.identityValidator.validate(module, identity, invalidCode);
    }

    protected void handleTokenTheft(String identity, AuthToken authToken) {
	this.authTokenManager.removeAll(authToken.getHashIdentity());
    }

    protected long getNewExpiresAt(long curExpiresAt, long curTimeMs, int cfgMaxAge) {
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
	if ((loginToken == null) || (loginToken.getSeries() == null) || (loginToken.getToken() == null) || (loginToken.getModule() == null) || (loginToken.getIdentity() == null)) {
	    return InvalidAuthResult.TOKEN_INVALID;
	}

	// AuthToken
	AuthToken authToken = this.authTokenManager.load(loginToken.getSeries());
	if (authToken == null) {
	    return InvalidAuthResult.TOKEN_INVALID;
	}

	// Identity
	if (!this.authTokenHandler.getIdentityDigester().verify(loginToken.getIdentity(), authToken.getHashIdentity())) {
	    return InvalidAuthResult.TOKEN_INVALID;
	}

	// Token
	String tokenData = this.authTokenHandler.getTokenData(loginToken.getSeries(), loginToken.getToken(), loginToken.getIdentity(), authToken.getExpiresAt(), null);

	if (!this.authTokenHandler.getTokenDigester().verify(tokenData, authToken.getHashToken())) {
	    this.request.setAttribute(AuthToken.class.getName(), authToken);

	    handleTokenTheft(loginToken.getIdentity(), authToken);
	    return InvalidAuthResult.TOKEN_THEFTED;
	}

	// ExpiresAt
	if (!DateUtils.isFutureTime(authToken.getExpiresAt(), 0)) {
	    return InvalidAuthResult.TOKEN_EXPIRED;
	}

	// Validate identity
	Out<String> invalidCode = new Out<>();
	PrincipalRoles principalRoles = doValidate(loginToken.getModule(), loginToken.getIdentity(), invalidCode);

	if (principalRoles == null) {
	    return InvalidAuthResult.valueOf(invalidCode.val());
	}

	// CredentialValidationResult
	AuthUserPrincipal principal = new AuthUserPrincipal(principalRoles.getPrincipal(), loginToken.getModule(), true, false);
	CredentialValidationResult result = SecurityUtils.createIdentityStoreResult(principal, principalRoles.getRoles());

	// Reissue Token
	final long curTimeMs = System.currentTimeMillis();
	String clearToken = this.authTokenHandler.getTokenGenerator().generate();

	long newExpiresAt = this.appConfig.getRequiredBool(AppConfig.CONFIG_REMME_COOKIE_SLIDING)
		? getNewExpiresAt(authToken.getExpiresAt(), curTimeMs, this.appConfig.getRequiredInt(AppConfig.CONFIG_REMME_COOKIE_AGE))
		: authToken.getExpiresAt();

	String newTokenData = this.authTokenHandler.getTokenData(loginToken.getSeries(), clearToken, loginToken.getIdentity(), newExpiresAt, null);
	String newHashToken = this.authTokenHandler.getTokenDigester().digest(newTokenData);

	this.authTokenManager.reissue(authToken.getSeries(), newHashToken, newExpiresAt, curTimeMs);

	int remainingSec = ValueUtils.valueOrMin((int) ((newExpiresAt - curTimeMs) / 1000L), 0);
	String newLoginToken = encodeLoginToken(
		new LoginToken().setSeries(loginToken.getSeries()).setToken(clearToken).setModule(loginToken.getModule()).setIdentity(loginToken.getIdentity()));

	// ReissuedToken
	this.request.setAttribute(ReissuedToken.class.getName(), new ReissuedToken(loginToken.getIdentity(), newLoginToken, remainingSec));
	return result;
    }

    @Override
    public void removeLoginToken(String token) {
	LoginToken loginToken = decodeLoginToken(token);
	if ((loginToken != null) && (loginToken.getSeries() != null)) {
	    this.authTokenManager.remove(loginToken.getSeries());
	}
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

    static class ReissuedToken {
	final String identity;
	final String loginToken;
	final int maxAge;

	public ReissuedToken(String identity, String loginToken, int maxAge) {
	    this.identity = identity;
	    this.loginToken = loginToken;
	    this.maxAge = maxAge;
	}

	public String getIdentity() {
	    return this.identity;
	}

	public String getLoginToken() {
	    return this.loginToken;
	}

	public int getMaxAge() {
	    return this.maxAge;
	}
    }
}
