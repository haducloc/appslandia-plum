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

package com.appslandia.plum.base;

import java.util.zip.GZIPOutputStream;

import com.appslandia.common.base.MappedID;
import com.appslandia.common.base.MemoryStream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
@MappedID("gzip")
public class GzipResponseEncoder implements ResponseEncoder {

  @Override
  public void encode(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {
    var wrapper = new GzipResponseWrapper(response);
    try {
      chain.doFilter(request, wrapper);

      if ((300 <= response.getStatus()) && (response.getStatus() < 400)) {
        return;
      }
      wrapper.finishWrapper();

    } catch (Exception ex) {
      if (response.isCommitted()) {
        wrapper.finishWrapper();
      }
      throw ex;
    }
  }

  @Override
  public void encode(HttpServletResponse response, MemoryStream content) throws Exception {
    response.setHeader("Content-Encoding", "gzip");

    var out = new GZIPOutputStream(response.getOutputStream());
    content.writeTo(out);

    out.flush();
    out.finish();
  }
}
