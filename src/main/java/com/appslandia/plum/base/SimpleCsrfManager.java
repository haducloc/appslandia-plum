// The MIT License (MIT)
// Copyright © 2015 Loc Ha

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

import java.util.UUID;

import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class SimpleCsrfManager implements CsrfManager {

  public static final String PARAM_CSRF_ID = "__csrf_id";

  protected String generateCsrfId() {
    return UUID.randomUUID().toString();
  }

  protected abstract void saveCsrf(HttpServletRequest request, String csrfId);

  @Override
  public String initCsrf(HttpServletRequest request) {
    var csrfId = generateCsrfId();
    saveCsrf(request, csrfId);
    request.setAttribute(PARAM_CSRF_ID, csrfId);
    return csrfId;
  }

  public abstract boolean verifyCsrf(HttpServletRequest request, String csrfId, boolean remove);

  @Override
  public boolean verifyCsrf(HttpServletRequest request, boolean remove) {
    var csrfId = request.getParameter(PARAM_CSRF_ID);
    if (csrfId == null) {
      ServletUtils.addError(request, PARAM_CSRF_ID, Resources.ERROR_CSRF_FAILED);
      return false;
    }
    var valid = verifyCsrf(request, csrfId, remove);
    if (!valid) {
      ServletUtils.addError(request, PARAM_CSRF_ID, Resources.ERROR_CSRF_FAILED);
    }
    return valid;
  }
}
