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

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class RequestFlags {

	public static final String REQUEST_ATTRIBUTE_ID = RequestFlags.class.getName();

	public static final int FLAG_ASYNC_COMPLETE_IMPLICITLY = 1; // 2^0

	private int flags;

	public void setFlag(int flag) {
		this.flags += flag;
	}

	public boolean isFlagSet(int flag) {
		return (this.flags & flag) == flag;
	}

	public static void setFlag(HttpServletRequest request, int flag) {
		RequestFlags obj = (RequestFlags) request.getAttribute(RequestFlags.REQUEST_ATTRIBUTE_ID);
		if (obj == null) {
			obj = new RequestFlags();
			request.setAttribute(RequestFlags.REQUEST_ATTRIBUTE_ID, obj);
		}
		obj.setFlag(flag);
	}

	public static boolean isFlagSet(HttpServletRequest request, int flag) {
		RequestFlags obj = (RequestFlags) request.getAttribute(RequestFlags.REQUEST_ATTRIBUTE_ID);
		return (obj != null) ? obj.isFlagSet(flag) : false;
	}
}
