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

import javax.inject.Inject;

import com.appslandia.common.base.Out;
import com.appslandia.common.base.TextGenerator;
import com.appslandia.common.crypto.TextDigester;
import com.appslandia.common.utils.DateUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class VerifyService {

	@Inject
	protected VerifyTokenManager verifyTokenManager;

	protected abstract TextGenerator getSeriesGenerator();

	protected abstract TextGenerator getTokenGenerator();

	protected abstract TextDigester getTokenDigester();

	public SeriesToken saveToken(String identity, String data, long expiresInMs) {
		VerifyToken verToken = new VerifyToken();
		verToken.setSeries(getSeriesGenerator().generate());
		String clearToken = getTokenGenerator().generate();

		final long curTimeMs = System.currentTimeMillis();
		long expiresAt = curTimeMs + expiresInMs;

		// Identity
		identity = identity.toLowerCase(Locale.ENGLISH);

		verToken.setToken(getTokenDigester().digest(getTokenData(verToken.getSeries(), clearToken, identity, data, expiresAt)));
		verToken.setExpiresAt(expiresAt);
		verToken.setIssuedAt(curTimeMs);

		this.verifyTokenManager.save(verToken);
		return new SeriesToken().setSeries(verToken.getSeries()).setToken(clearToken);
	}

	public boolean verifyToken(String series, String token, String identity, String data, int expiryLeewayMs, Out<String> failureCode) {
		// VerifyToken
		VerifyToken verToken = this.verifyTokenManager.load(series);
		if (verToken == null) {
			failureCode.value = AuthFailureResult.TOKEN_INVALID.getFailureCode();
			return false;
		}

		// Identity
		identity = identity.toLowerCase(Locale.ENGLISH);

		// Token
		if (!getTokenDigester().verify(getTokenData(series, token, identity, data, verToken.getExpiresAt()), verToken.getToken())) {
			failureCode.value = AuthFailureResult.TOKEN_INVALID.getFailureCode();
			return false;
		}

		// ExpiresAt
		if (!DateUtils.isFutureTime(verToken.getExpiresAt(), expiryLeewayMs)) {
			failureCode.value = AuthFailureResult.TOKEN_EXPIRED.getFailureCode();
			return false;
		}
		return true;
	}

	public void removeToken(String series) {
		this.verifyTokenManager.remove(series);
	}

	protected String getTokenData(String series, String token, String identity, String data, long expiresAt) {
		return series + '|' + token + '|' + identity + '|' + data + '|' + expiresAt;
	}
}
