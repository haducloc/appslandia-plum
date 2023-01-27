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

import java.util.Locale;

import com.appslandia.common.base.Out;
import com.appslandia.common.base.TextGenerator;
import com.appslandia.common.crypto.TextDigester;
import com.appslandia.common.utils.DateUtils;

import jakarta.inject.Inject;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class AuthTokenHandler {

    @Inject
    protected AuthTokenManager authTokenManager;

    protected abstract TextGenerator getSeriesGenerator();

    protected abstract TextGenerator getTokenGenerator();

    protected abstract TextDigester getTokenDigester();

    protected abstract TextDigester getIdentityDigester();

    public SeriesToken saveToken(String identity, String data, long expiresInMs) {
	// AuthToken
	AuthToken authToken = new AuthToken();
	authToken.setSeries(getSeriesGenerator().generate());
	String clearToken = getTokenGenerator().generate();

	final long curTimeMs = System.currentTimeMillis();
	long expiresAt = curTimeMs + expiresInMs;
	identity = identity.toLowerCase(Locale.ENGLISH);

	String tokenData = getTokenData(authToken.getSeries(), clearToken, identity, expiresAt, data);
	authToken.setHashToken(getTokenDigester().digest(tokenData));
	authToken.setHashIdentity(getIdentityDigester().digest(identity));

	authToken.setExpiresAt(expiresAt);
	authToken.setIssuedAt(curTimeMs);

	this.authTokenManager.save(authToken);
	return new SeriesToken().setSeries(authToken.getSeries()).setToken(clearToken);
    }

    public boolean verifyToken(String series, String token, String identity, String data, int expiryLeewayMs, Out<String> invalidCode) {
	// AuthToken
	AuthToken authToken = this.authTokenManager.load(series);
	if (authToken == null) {
	    invalidCode.value = InvalidAuthResult.TOKEN_INVALID.getCode();
	    return false;
	}

	// Identity
	identity = identity.toLowerCase(Locale.ENGLISH);
	if (!getIdentityDigester().verify(identity, authToken.getHashIdentity())) {
	    return false;
	}

	// Token
	String tokenData = getTokenData(series, token, identity, authToken.getExpiresAt(), data);
	if (!getTokenDigester().verify(tokenData, authToken.getHashToken())) {
	    invalidCode.value = InvalidAuthResult.TOKEN_INVALID.getCode();
	    return false;
	}

	// ExpiresAt
	if (!DateUtils.isFutureTime(authToken.getExpiresAt(), expiryLeewayMs)) {
	    invalidCode.value = InvalidAuthResult.TOKEN_EXPIRED.getCode();
	    return false;
	}
	return true;
    }

    protected String getTokenData(String series, String token, String identity, long expiresAt, String data) {
	return String.join("|", series, token, identity, Long.toString(expiresAt), data);
    }
}
