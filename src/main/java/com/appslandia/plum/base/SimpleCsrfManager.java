// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class SimpleCsrfManager implements CsrfManager {

  public static final String PARAM_CSRF_ID = "__csrf_id";

  protected abstract String saveCsrf(HttpServletRequest request);

  @Override
  public String initCsrf(HttpServletRequest request) {
    var csrfId = saveCsrf(request);
    request.setAttribute(PARAM_CSRF_ID, csrfId);
    return csrfId;
  }

  public abstract boolean verifyCsrf(HttpServletRequest request, String csrfId, boolean remove);

  @Override
  public boolean verifyCsrf(HttpServletRequest request, boolean remove) {
    var csrfId = request.getParameter(PARAM_CSRF_ID);
    if (csrfId == null) {
      addCsrfError(request);
      return false;
    }

    var valid = verifyCsrf(request, csrfId, remove);
    if (!valid) {
      addCsrfError(request);
    }
    return valid;
  }

  private static void addCsrfError(HttpServletRequest request) {
    var message = ServletUtils.getRequestContext(request).res(Resources.ERROR_CSRF_FAILED);
    ServletUtils.getModelState(request).addError(PARAM_CSRF_ID, message);
  }
}
