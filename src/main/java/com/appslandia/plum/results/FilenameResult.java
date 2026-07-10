// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.results;

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
public abstract class FilenameResult implements ActionResult {

  private String fileName;
  private String contentType;
  private boolean inline;

  public FilenameResult(String fileName, String contentType) {
    this(fileName, contentType, false);
  }

  public FilenameResult(String fileName, String contentType, boolean inline) {
    this.fileName = fileName;
    this.contentType = contentType;
    this.inline = inline;
  }

  @Override
  public void execute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    response.setContentType(contentType);
    ServletUtils.setFileDisposition(response, fileName, inline);

    writeContent(request, response);
  }

  protected abstract void writeContent(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
