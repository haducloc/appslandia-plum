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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ResponseWrapperImpl extends ResponseWrapper {

	private Collection<String> _headers;

	public ResponseWrapperImpl(HttpServletResponse response) {
		super(response);
	}

	@Override
	public void finishWrapper() throws IOException {
	}

	public HeaderValue[] toHeaderValues() {
		if (this._headers == null) {
			return null;
		}
		List<HeaderValue> headerValues = new ArrayList<>(this._headers.size());
		for (String headerName : this._headers) {
			Collection<String> values = super.getHeaders(headerName);
			if (!values.isEmpty()) {
				headerValues.add(new HeaderValue(headerName, values.toArray(new String[values.size()])));
			} else {
				String value = super.getHeader(headerName);
				if (value != null) {
					headerValues.add(new HeaderValue(headerName, new String[] { value }));
				}
			}
		}
		return headerValues.toArray(new HeaderValue[headerValues.size()]);
	}

	public void resetHeaders() {
		if (this._headers == null) {
			return;
		}
		for (String headerName : this._headers) {
			super.setHeader(headerName, null);
		}
	}

	protected void addHeader(String headerName) {
		if (this._headers == null) {
			this._headers = new HashSet<String>();
		}
		this._headers.add(headerName);
	}

	@Override
	public void setContentLength(int len) {
		super.setContentLength(len);
		addHeader("Content-Length");
	}

	@Override
	public void setContentLengthLong(long len) {
		super.setContentLengthLong(len);
		addHeader("Content-Length");
	}

	@Override
	public void setContentType(String type) {
		super.setContentType(type);
		addHeader("Content-Type");
	}

	@Override
	public void setHeader(String name, String value) {
		super.setHeader(name, value);
		addHeader(name);
	}

	@Override
	public void setIntHeader(String name, int value) {
		super.setIntHeader(name, value);
		addHeader(name);
	}

	@Override
	public void setDateHeader(String name, long date) {
		super.setDateHeader(name, date);
		addHeader(name);
	}

	@Override
	public void addHeader(String name, String value) {
		super.addHeader(name, value);
		addHeader(name);
	}

	@Override
	public void addIntHeader(String name, int value) {
		super.addIntHeader(name, value);
		addHeader(name);
	}

	@Override
	public void addDateHeader(String name, long date) {
		super.addDateHeader(name, date);
		addHeader(name);
	}
}
