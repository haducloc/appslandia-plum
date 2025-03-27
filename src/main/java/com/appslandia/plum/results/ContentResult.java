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

package com.appslandia.plum.results;

import java.nio.charset.StandardCharsets;

import com.appslandia.common.utils.MimeTypes;
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
    this(content, MimeTypes.TEXT_PLAIN);
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
    response.setContentType(this.contentType);

    if (this.contentEncoding != null) {
      response.setCharacterEncoding(this.contentEncoding);
    }

    response.getWriter().write(this.content);
    response.getWriter().flush();
  }
}
