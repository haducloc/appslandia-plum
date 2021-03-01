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

import com.appslandia.common.base.TextGenerator;
import com.appslandia.plum.utils.ServletUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public abstract class SimpleCsrfManager implements CsrfManager {

	public static final String PARAM_CSRF_ID = "csrfId";
	public static final String REQUEST_ATTRIBUTE_CSRF_DATA = "csrfData";

	protected abstract TextGenerator getCsrfIdGenerator();

	protected abstract Object saveCsrf(HttpServletRequest request, String csrfId);

	protected abstract Object parseCsrfData(HttpServletRequest request, String csrfId);

	@Override
	public void initCsrf(HttpServletRequest request) {
		String csrfId = getCsrfIdGenerator().generate();
		Object csrfData = saveCsrf(request, csrfId);
		request.setAttribute(REQUEST_ATTRIBUTE_CSRF_DATA, csrfData);
	}

	public abstract boolean verifyCsrf(HttpServletRequest request, String csrfId, boolean remove);

	@Override
	public boolean verifyCsrf(HttpServletRequest request, boolean remove) {
		String csrfId = request.getParameter(PARAM_CSRF_ID);
		if (csrfId == null) {
			ServletUtils.addError(request, PARAM_CSRF_ID, Resources.ERROR_CSRF_FAILED);
			return false;
		}
		boolean valid = verifyCsrf(request, csrfId, remove);
		if (!valid) {
			ServletUtils.addError(request, PARAM_CSRF_ID, Resources.ERROR_CSRF_FAILED);
		}
		return valid;
	}
}
