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

import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appslandia.common.crypto.DigesterImpl;
import com.appslandia.common.utils.DateUtils;
import com.appslandia.common.utils.RandomUtils;
import com.appslandia.plum.mocks.MockHttpServletRequest;
import com.appslandia.plum.mocks.MockHttpServletResponse;
import com.appslandia.plum.mocks.MockServletContext;
import com.appslandia.plum.mocks.MockSessionCookieConfig;

/**
 *
 * @author Loc Ha
 *
 */
public class ServletUtilsTest {

  MockServletContext servletContext;

  @BeforeEach
  public void initialize() {
    servletContext = new MockServletContext(new MockSessionCookieConfig());
  }

  @Test
  public void test_appendUriQuery() {
    var request = new MockHttpServletRequest(servletContext);
    request.setRequestURL("http://localhost/app/main");
    Assertions.assertEquals("/app/main", ServletUtils.appendUriQuery(request, new StringBuilder()).toString());

    request = new MockHttpServletRequest(servletContext);
    request.setRequestURL("http://localhost/app/main?param1=value1");
    Assertions.assertEquals("/app/main?param1=value1",
        ServletUtils.appendUriQuery(request, new StringBuilder()).toString());
  }

  @Test
  public void test_toEtag() {
    var content = RandomUtils.nextBytes(100, new Random());
    var md5 = new DigesterImpl("MD5").digest(content);
    var weakEtag = ServletUtils.toEtag(md5);

    Assertions.assertTrue(weakEtag.length() == 34);
    Assertions.assertTrue(weakEtag.startsWith("\""));
    Assertions.assertTrue(weakEtag.endsWith("\""));
  }

  @Test
  public void test_checkNotModified_lastModified() {
    var request = new MockHttpServletRequest(servletContext);
    var response = new MockHttpServletResponse(servletContext);

    var ifModifiedSince = DateUtils.clearMs(System.currentTimeMillis());
    request.setHeaderValues("If-Modified-Since", HeaderUtils.toDateHeaderString(ifModifiedSince));

    var lastModified = ifModifiedSince;
    var notModified = ServletUtils.checkNotModified(request, response, lastModified);
    Assertions.assertTrue(notModified);
    Assertions.assertEquals(lastModified, response.getDateHeader("Last-Modified"));
    Assertions.assertEquals(304, response.getStatus());
  }

  @Test
  public void test_checkNotModified_lastModified_modified() {
    var request = new MockHttpServletRequest(servletContext);
    var response = new MockHttpServletResponse(servletContext);

    var ifModifiedSince = DateUtils.clearMs(System.currentTimeMillis());
    request.setHeaderValues("If-Modified-Since", HeaderUtils.toDateHeaderString(ifModifiedSince));

    var lastModified = ifModifiedSince + 1000;
    var notModified = ServletUtils.checkNotModified(request, response, lastModified);
    Assertions.assertFalse(notModified);

    Assertions.assertEquals(lastModified, response.getDateHeader("Last-Modified"));
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  public void test_checkNotModified_etag() {
    var request = new MockHttpServletRequest(servletContext);
    var response = new MockHttpServletResponse(servletContext);

    var ifNoneMatch = "etag";
    request.setHeaderValues("If-None-Match", ifNoneMatch);

    var etag = "etag";
    var notModified = ServletUtils.checkNotModified(request, response, etag);
    Assertions.assertTrue(notModified);
    Assertions.assertEquals(etag, response.getHeader("ETag"));
    Assertions.assertEquals(304, response.getStatus());
  }

  @Test
  public void test_checkNotModified_etag_modified() {
    var request = new MockHttpServletRequest(servletContext);
    var response = new MockHttpServletResponse(servletContext);

    var ifNoneMatch = "etag";
    request.setHeaderValues("If-None-Match", ifNoneMatch);

    var etag = "etag1";
    var notModified = ServletUtils.checkNotModified(request, response, etag);
    Assertions.assertFalse(notModified);
    Assertions.assertEquals(etag, response.getHeader("ETag"));
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  public void test_checkPrecondition_lastModified() {
    var request = new MockHttpServletRequest(servletContext);
    var response = new MockHttpServletResponse(servletContext);

    var ifUnmodifiedSince = DateUtils.clearMs(System.currentTimeMillis());
    request.setHeaderValues("If-Unmodified-Since", HeaderUtils.toDateHeaderString(ifUnmodifiedSince));

    var lastModified = ifUnmodifiedSince + 999;
    var notModified = ServletUtils.checkPrecondition(request, response, lastModified);
    Assertions.assertTrue(notModified);
  }

  @Test
  public void test_checkPrecondition_lastModified_modified() {
    var request = new MockHttpServletRequest(servletContext);
    var response = new MockHttpServletResponse(servletContext);

    var ifUnmodifiedSince = DateUtils.clearMs(System.currentTimeMillis());
    request.setHeaderValues("If-Unmodified-Since", HeaderUtils.toDateHeaderString(ifUnmodifiedSince));

    var lastModified = ifUnmodifiedSince + 1000;
    var notModified = ServletUtils.checkPrecondition(request, response, lastModified);
    Assertions.assertFalse(notModified);
  }

  @Test
  public void test_checkPrecondition_etag() {
    var request = new MockHttpServletRequest(servletContext);
    var response = new MockHttpServletResponse(servletContext);

    var ifMatch = "etag";
    request.setHeaderValues("If-Match", ifMatch);

    var etag = "etag";
    var notModified = ServletUtils.checkPrecondition(request, response, etag);
    Assertions.assertTrue(notModified);
  }

  @Test
  public void test_checkPrecondition_etag_modified() {
    var request = new MockHttpServletRequest(servletContext);
    var response = new MockHttpServletResponse(servletContext);

    var ifMatch = "etag";
    request.setHeaderValues("If-Match", ifMatch);

    var etag = "etag1";
    var notModified = ServletUtils.checkPrecondition(request, response, etag);
    Assertions.assertFalse(notModified);
  }

  @Test
  public void test_getBestEncoding() {
    var request = new MockHttpServletRequest(servletContext);

    request.setHeader("Accept-Encoding", "*");
    var encoding = ServletUtils.getBestEncoding(request, Arrays.asList("gzip", "br"));
    Assertions.assertNull(encoding);
  }

  @Test
  public void test_getBestEncoding_qvalue() {
    var request = new MockHttpServletRequest(servletContext);

    request.setHeader("Accept-Encoding", "gzip;q=0.8, deflate;q=0.6, br;q=1.0");
    var encoding = ServletUtils.getBestEncoding(request, Arrays.asList("gzip", "br"));
    Assertions.assertEquals("br", encoding);
  }

  @Test
  public void test_getBestEncoding_unsupported() {
    var request = new MockHttpServletRequest(servletContext);

    request.setHeader("Accept-Encoding", "gzip, deflate;q=0.6");
    var encoding = ServletUtils.getBestEncoding(request, Arrays.asList("br"));
    Assertions.assertNull(encoding);
  }
}
