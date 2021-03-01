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

package com.appslandia.plum.utils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appslandia.common.utils.URLEncoding;

/**
 * 
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class CookieImpl {

	protected final String name;
	protected String value;
	protected String path;
	protected String domain;
	protected int maxAge = -1;

	protected boolean discard;
	protected boolean secure;
	protected boolean httpOnly;

	protected boolean sameSite;
	protected String sameSiteMode;

	public CookieImpl(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public CookieImpl(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}

	public CookieImpl setValue(String value) {
		this.value = value;
		return this;
	}

	public String getPath() {
		return this.path;
	}

	public CookieImpl setPath(String path) {
		this.path = path;
		return this;
	}

	public String getDomain() {
		return this.domain;
	}

	public CookieImpl setDomain(String domain) {
		this.domain = (domain != null) ? domain.toLowerCase(Locale.ENGLISH) : null;
		return this;
	}

	public int getMaxAge() {
		return this.maxAge;
	}

	public CookieImpl setMaxAge(int maxAge, TimeUnit unit) {
		if (maxAge < 0) {
			this.maxAge = -1;
		}
		this.maxAge = (int) TimeUnit.SECONDS.convert(maxAge, unit);
		return this;
	}

	public boolean isDiscard() {
		return this.discard;
	}

	public CookieImpl setDiscard(boolean discard) {
		this.discard = discard;
		return this;
	}

	public boolean isSecure() {
		return this.secure;
	}

	public CookieImpl setSecure(boolean secure) {
		this.secure = secure;
		return this;
	}

	public boolean isHttpOnly() {
		return this.httpOnly;
	}

	public CookieImpl setHttpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
		return this;
	}

	public boolean isSameSite() {
		return this.sameSite;
	}

	public CookieImpl setSameSite(boolean sameSite) {
		this.sameSite = sameSite;
		return this;
	}

	public String getSameSiteMode() {
		return this.sameSiteMode;
	}

	public CookieImpl setSameSiteMode(String sameSiteMode) {
		if (sameSiteMode == null) {
			return this;
		}
		switch (sameSiteMode.toLowerCase(Locale.ENGLISH)) {
		case "strict":
			this.setSameSite(true);
			this.sameSiteMode = "Strict";
			break;
		case "lax":
			this.setSameSite(true);
			this.sameSiteMode = "Lax";
			break;
		default:
			throw new IllegalArgumentException();
		}
		return this;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(URLEncoding.encodeParam(this.name));
		sb.append("=");
		if (this.value != null) {
			sb.append(URLEncoding.encodeParam(this.value));
		}
		if (this.path != null) {
			sb.append("; Path=");
			sb.append(this.path);
		}
		if (this.domain != null) {
			sb.append("; Domain=");
			sb.append(this.domain);
		}
		if (this.discard) {
			sb.append("; Discard");
		}
		if (this.secure) {
			sb.append("; Secure");
		}
		if (this.httpOnly) {
			sb.append("; HttpOnly");
		}
		if (this.maxAge >= 0) {
			sb.append("; Max-Age=");
			sb.append(this.maxAge);

			long expires = (this.maxAge > 0) ? (System.currentTimeMillis() + this.maxAge * 1000L) : 0;
			sb.append("; Expires=");
			sb.append(HeaderUtils.toDateHeaderString(expires));
		}
		if (this.sameSite) {
			if (this.sameSiteMode != null) {
				sb.append("; SameSite=");
				sb.append(this.sameSiteMode);
			} else {
				sb.append("; SameSite");
			}
		}
		return sb.toString();
	}

	public void setCookie(HttpServletResponse response) {
		response.addHeader("Set-Cookie", toString());
	}

	public static String getCookieValue(HttpServletRequest request, String cookieName) {
		String cookieValue = ServletUtils.getCookieValue(request, URLEncoding.encodeParam(cookieName));
		if (cookieValue != null) {
			try {
				return URLEncoding.decodeParam(cookieValue);
			} catch (IllegalArgumentException ex) {
				return null;
			}
		}
		return null;
	}
}
