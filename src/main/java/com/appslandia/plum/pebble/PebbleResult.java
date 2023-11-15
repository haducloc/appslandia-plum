// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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

package com.appslandia.plum.pebble;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.appslandia.common.utils.MimeTypes;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.results.ViewResult;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class PebbleResult extends ViewResult {

  private String contentType;
  private String characterEncoding;

  public PebbleResult() {
    super();
  }

  public PebbleResult(String path) {
    super(path);
  }

  public PebbleResult(Map<String, Object> model) {
    super(model);
  }

  public PebbleResult(String path, Map<String, Object> model) {
    super(path, model);
  }

  public PebbleResult contentType(String contentType) {
    this.contentType = contentType;
    return this;
  }

  public PebbleResult characterEncoding(String characterEncoding) {
    this.characterEncoding = characterEncoding;
    return this;
  }

  @Override
  public String getSuffix() {
    return ".peb";
  }

  @Override
  protected String getViewDir(ServletContext servletContext) {
    return PebbleUtils.getPebbleDir(servletContext);
  }

  @Override
  protected void doExecute(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext)
      throws Exception {
    response.setContentType(this.contentType != null ? this.contentType : MimeTypes.TEXT_HTML);
    response
        .setCharacterEncoding(this.characterEncoding != null ? this.characterEncoding : StandardCharsets.UTF_8.name());

    PebbleUtils.executePebble(request, response, response.getWriter(), this.resolvedPath, this.model,
        requestContext.getLanguage().getLocale());
  }

  public static final PebbleResult DEFAULT = new PebbleResult() {

    @Override
    public PebbleResult contentType(String contentType) {
      throw new UnsupportedOperationException();
    }

    @Override
    public PebbleResult characterEncoding(String encoding) {
      throw new UnsupportedOperationException();
    }
  };
}
