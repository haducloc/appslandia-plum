// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@NotLog
public class BadRequestException extends HttpException {
  private static final long serialVersionUID = 1L;

  private static final int STATUS = HttpServletResponse.SC_BAD_REQUEST;

  public BadRequestException(String devMessage) {
    super(devMessage, STATUS);
  }

  public BadRequestException(String devMessage, String titleKey, ResKey detailKey) {
    super(devMessage, STATUS, titleKey, detailKey);
  }

  public BadRequestException(String devMessage, Throwable cause, String titleKey, ResKey detailKey) {
    super(devMessage, STATUS, cause, titleKey, detailKey);
  }

  public BadRequestException(String devMessage, String titleKey, ResKey detailKey, String type,
      Map<String, List<String>> errors, Map<String, Object> exts) {
    super(devMessage, STATUS, titleKey, detailKey, type, errors, exts);
  }

  public BadRequestException(String devMessage, Throwable cause, String titleKey, ResKey detailKey, String type,
      Map<String, List<String>> errors, Map<String, Object> exts) {
    super(devMessage, cause, STATUS, titleKey, detailKey, type, errors, exts);
  }
}
