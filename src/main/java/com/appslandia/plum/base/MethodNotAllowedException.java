// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.function.Consumer;

import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@NotLog
public class MethodNotAllowedException extends HttpException implements HeaderWriterSupport {
  private static final long serialVersionUID = 1L;

  private static final int STATUS = HttpServletResponse.SC_METHOD_NOT_ALLOWED;

  private final Consumer<HttpServletResponse> headerWriter;

  public MethodNotAllowedException(String devMessage, Consumer<HttpServletResponse> headerWriter) {
    super(devMessage, STATUS);

    this.headerWriter = headerWriter;
  }

  public MethodNotAllowedException(String devMessage, String titleKey, ResKey detailKey,
      Consumer<HttpServletResponse> headerWriter) {
    super(devMessage, STATUS, titleKey, detailKey);

    this.headerWriter = headerWriter;
  }

  public MethodNotAllowedException(String devMessage, Throwable cause, String titleKey, ResKey detailKey,
      Consumer<HttpServletResponse> headerWriter) {
    super(devMessage, STATUS, cause, titleKey, detailKey);

    this.headerWriter = headerWriter;
  }

  @Override
  public Consumer<HttpServletResponse> getHeaderWriter() {
    return headerWriter;
  }
}
