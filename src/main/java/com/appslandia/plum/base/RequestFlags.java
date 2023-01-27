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

import jakarta.servlet.ServletRequest;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class RequestFlags {

    public static final String REQUEST_ATTRIBUTE_ID = RequestFlags.class.getName();

    public static final int FLAG_ASYNC_COMPLETE_IMPLICITLY = 1; // 2^0

    public static void setFlag(ServletRequest request, int flag) {
	Integer vals = (Integer) request.getAttribute(REQUEST_ATTRIBUTE_ID);
	int newVals = ((vals != null) ? vals : 0) + flag;

	request.setAttribute(REQUEST_ATTRIBUTE_ID, newVals);
    }

    public static boolean isFlagSet(ServletRequest request, int flag) {
	Integer vals = (Integer) request.getAttribute(REQUEST_ATTRIBUTE_ID);
	return (vals != null) ? ((vals & flag) == flag) : false;
    }
}
