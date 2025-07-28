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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import com.appslandia.common.base.DeployEnv;
import com.appslandia.common.base.MemoryStream;
import com.appslandia.common.base.ToStringBuilder;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.IOUtils;
import com.appslandia.common.utils.ReflectionUtils;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.mocks.MockHttpServletRequest;
import com.appslandia.plum.mocks.MockHttpServletResponse;

import jakarta.servlet.RequestDispatcher;

/**
 *
 * @author Loc Ha
 *
 */
public class TestUtils {

  public static void initDeployEnv(DeployEnv env) {
    var field = ReflectionUtils.findField(DeployEnv.class, "current");
    Asserts.notNull(field);

    ReflectionUtils.set(field, null, env);
  }

  public static String ungzipToString(MemoryStream gzipContent) throws IOException {
    if (gzipContent.size() == 0) {
      return StringUtils.EMPTY_STRING;
    }
    try (var gis = new GZIPInputStream(new ByteArrayInputStream(gzipContent.toByteArray()))) {
      return IOUtils.toString(gis, StandardCharsets.UTF_8.name());
    }
  }

  public static void printException(MockHttpServletRequest request) {
    var error = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
    if (error != null) {
      error.printStackTrace();
    }
  }

  public static void printHeaders(MockHttpServletRequest request) {
    System.out.println(new ToStringBuilder().toStringHeaders(request));
  }

  public static void printHeaders(MockHttpServletResponse response) {
    System.out.println(new ToStringBuilder().toStringHeaders(response));
  }
}
