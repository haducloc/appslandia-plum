// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
// @NotLog
public class InternalServerException extends HttpException {
  private static final long serialVersionUID = 1L;

  private static final int STATUS = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

  public InternalServerException(String devMessage) {
    super(devMessage, STATUS);
  }

  public InternalServerException(String devMessage, String titleKey, ResKey detailKey) {
    super(devMessage, STATUS, titleKey, detailKey);
  }

  public InternalServerException(String devMessage, Throwable cause, String titleKey, ResKey detailKey) {
    super(devMessage, STATUS, cause, titleKey, detailKey);
  }
}
