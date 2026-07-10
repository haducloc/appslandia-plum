// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appslandia.common.utils.DateUtils;
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
  public void test_getUriWithQuery() {
    var request = new MockHttpServletRequest(servletContext);
    request.setRequestURL("http://localhost/app/main");
    Assertions.assertEquals("/app/main", ServletUtils.getUriWithQuery(request));

    request = new MockHttpServletRequest(servletContext);
    request.setRequestURL("http://localhost/app/main?param1=value1");
    Assertions.assertEquals("/app/main?param1=value1", ServletUtils.getUriWithQuery(request));
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
    request.setMethod("POST");

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
    request.setMethod("POST");

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
    request.setMethod("POST");

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
    request.setMethod("POST");

    var ifMatch = "etag";
    request.setHeaderValues("If-Match", ifMatch);

    var etag = "etag1";
    var notModified = ServletUtils.checkPrecondition(request, response, etag);
    Assertions.assertFalse(notModified);
  }
}
