// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.appslandia.plum.utils.ServletUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class JspRenderUtil {

  @Inject
  protected MemoryStreamFactory memoryStreamFactory;

  public String renderJsp(HttpServletRequest request, HttpServletResponse response, String jspPath, Object model)
      throws ServletException, IOException {
    return renderJsp(request, response, jspPath, model, StandardCharsets.UTF_8.name());
  }

  public String renderJsp(HttpServletRequest request, HttpServletResponse response, String jspPath, Object model,
      String contentEncoding) throws ServletException, IOException {

    var backupAttributes = new RequestAttributes(request);
    request.setAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL, model);

    var backupEncoding = response.getCharacterEncoding();
    var content = memoryStreamFactory.newMemoryStream();

    try {
      response.setCharacterEncoding(contentEncoding);

      // allowSetHeaders=false
      var wrapper = new MemoryResponseWrapper(response, false, content);

      ServletUtils.include(request, wrapper, jspPath);
      wrapper.finishResponse();

      return content.toString(contentEncoding);

    } finally {
      response.setCharacterEncoding(backupEncoding);
      backupAttributes.restore(request);
      content.reset();
    }
  }
}
