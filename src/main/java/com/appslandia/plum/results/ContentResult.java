// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.results;

import java.nio.charset.StandardCharsets;

import com.appslandia.common.utils.ContentTypes;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.RequestContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class ContentResult implements ActionResult {

  protected String content;
  protected String contentType;
  protected String contentEncoding;

  public ContentResult(String content) {
    this(content, ContentTypes.TEXT_PLAIN);
  }

  public ContentResult(String content, String contentType) {
    this(content, contentType, StandardCharsets.UTF_8.name());
  }

  public ContentResult(String content, String contentType, String contentEncoding) {
    this.content = content;
    this.contentType = contentType;
    this.contentEncoding = contentEncoding;
  }

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    response.setContentType(contentType);

    if (contentEncoding != null) {
      response.setCharacterEncoding(contentEncoding);
    }

    response.getWriter().write(content);
    response.getWriter().flush();
  }
}
