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

package com.appslandia.plum.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.appslandia.plum.base.RequestAttributes;
import com.appslandia.plum.base.ServletContentResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class JspTemplateUtils {

  public static String executeJsp(HttpServletRequest request, HttpServletResponse response, String jspPath,
      Object model) throws ServletException, IOException {
    return executeJsp(request, response, jspPath, model, StandardCharsets.UTF_8.name());
  }

  public static String executeJsp(HttpServletRequest request, HttpServletResponse response, String jspPath,
      Object model, String contentEncoding) throws ServletException, IOException {
    var backupAttributes = new RequestAttributes(request);
    request.setAttribute(ServletUtils.REQUEST_ATTRIBUTE_MODEL, model);

    final var ce = response.getCharacterEncoding();

    try {
      response.setCharacterEncoding(contentEncoding);

      var wrapper = new ServletContentResponse(response, false);
      ServletUtils.include(request, wrapper, jspPath);

      wrapper.finishWrapper();
      var content = wrapper.getContent();

      var contentAsString = content.toString(contentEncoding);
      content.reset();
      return contentAsString;
    } finally {
      response.setCharacterEncoding(ce);
      backupAttributes.restore(request);
    }
  }
}
