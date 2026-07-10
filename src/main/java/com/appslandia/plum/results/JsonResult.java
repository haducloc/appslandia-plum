// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.results;

import java.nio.charset.StandardCharsets;

import com.appslandia.common.json.JsonProcessor;
import com.appslandia.common.utils.ContentTypes;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class JsonResult implements ActionResult {

  protected Object content;
  protected String contentType;

  final String contentEncoding = StandardCharsets.UTF_8.name();

  public JsonResult(Object content) {
    this(content, ContentTypes.APP_JSON);
  }

  public JsonResult(Object content, String contentType) {
    this.content = content;
    this.contentType = contentType;
  }

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    response.setContentType(contentType);

    if (contentEncoding != null) {
      response.setCharacterEncoding(contentEncoding);
    }

    var jsonProcessor = ServletUtils.getAppScoped(request, JsonProcessor.class);
    jsonProcessor.write(response.getWriter(), content);
    response.getWriter().flush();
  }
}
