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

package com.appslandia.plum.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.appslandia.common.crypto.DigesterImpl;
import com.appslandia.common.utils.DateUtils;
import com.appslandia.common.utils.RandomUtils;
import com.appslandia.plum.mocks.MockHttpServletRequest;
import com.appslandia.plum.mocks.MockHttpServletResponse;
import com.appslandia.plum.mocks.MockServletContext;
import com.appslandia.plum.mocks.MockSessionCookieConfig;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ServletUtilsTest {

	MockServletContext servletContext;

	@Before
	public void initialize() {
		servletContext = new MockServletContext(new MockSessionCookieConfig());
	}

	@Test
	public void test_appendUriQuery() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		request.setRequestURL("http://localhost/app/main");
		Assert.assertEquals(ServletUtils.appendUriQuery(request, new StringBuilder()).toString(), "/app/main");

		request = new MockHttpServletRequest(servletContext);
		request.setRequestURL("http://localhost/app/main?param1=value1");
		Assert.assertEquals(ServletUtils.appendUriQuery(request, new StringBuilder()).toString(), "/app/main?param1=value1");
	}

	@Test
	public void test_getSecureUrl() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		request.setRequestURL("http://localhost/app/main");
		Assert.assertEquals(ServletUtils.getSecureUrl(request, 443), "https://localhost/app/main");

		request = new MockHttpServletRequest(servletContext);
		request.setRequestURL("http://localhost/app/main?param1=value1");
		Assert.assertEquals(ServletUtils.getSecureUrl(request, 443), "https://localhost/app/main?param1=value1");

		request = new MockHttpServletRequest(servletContext);
		request.setRequestURL("http://localhost/app/main");
		Assert.assertEquals(ServletUtils.getSecureUrl(request, 8443), "https://localhost:8443/app/main");
	}

	@Test
	public void test_toEtag() {
		byte[] md5 = new DigesterImpl("MD5").digest(RandomUtils.nextBytes(16));
		String weakEtag = ServletUtils.toEtag(md5);

		Assert.assertTrue(weakEtag.length() == 34);
		Assert.assertTrue(weakEtag.startsWith("\""));
		Assert.assertTrue(weakEtag.endsWith("\""));
	}

	@Test
	public void test_checkNotModified_lastModified() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		MockHttpServletResponse response = new MockHttpServletResponse(servletContext);

		long ifModifiedSince = DateUtils.clearMs(System.currentTimeMillis());
		request.setHeaderValues("If-Modified-Since", HeaderUtils.toDateHeaderString(ifModifiedSince));

		long lastModified = ifModifiedSince;
		boolean notModified = ServletUtils.checkNotModified(request, response, lastModified);
		Assert.assertTrue(notModified);
		Assert.assertEquals(response.getDateHeader("Last-Modified"), lastModified);
		Assert.assertEquals(response.getStatus(), 304);
	}

	@Test
	public void test_checkNotModified_lastModified_modified() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		MockHttpServletResponse response = new MockHttpServletResponse(servletContext);

		long ifModifiedSince = DateUtils.clearMs(System.currentTimeMillis());
		request.setHeaderValues("If-Modified-Since", HeaderUtils.toDateHeaderString(ifModifiedSince));

		long lastModified = ifModifiedSince + 1000;
		boolean notModified = ServletUtils.checkNotModified(request, response, lastModified);
		Assert.assertFalse(notModified);

		Assert.assertEquals(response.getDateHeader("Last-Modified"), lastModified);
		Assert.assertEquals(response.getStatus(), 200);
	}

	@Test
	public void test_checkNotModified_etag() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		MockHttpServletResponse response = new MockHttpServletResponse(servletContext);

		String ifNoneMatch = "etag";
		request.setHeaderValues("If-None-Match", ifNoneMatch);

		String etag = "etag";
		boolean notModified = ServletUtils.checkNotModified(request, response, etag);
		Assert.assertTrue(notModified);
		Assert.assertEquals(response.getHeader("ETag"), etag);
		Assert.assertEquals(response.getStatus(), 304);
	}

	@Test
	public void test_checkNotModified_etag_modified() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		MockHttpServletResponse response = new MockHttpServletResponse(servletContext);

		String ifNoneMatch = "etag";
		request.setHeaderValues("If-None-Match", ifNoneMatch);

		String etag = "etag1";
		boolean notModified = ServletUtils.checkNotModified(request, response, etag);
		Assert.assertFalse(notModified);
		Assert.assertEquals(response.getHeader("ETag"), etag);
		Assert.assertEquals(response.getStatus(), 200);
	}

	@Test
	public void test_checkPrecondition_lastModified() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		MockHttpServletResponse response = new MockHttpServletResponse(servletContext);

		long ifUnmodifiedSince = DateUtils.clearMs(System.currentTimeMillis());
		request.setHeaderValues("If-Unmodified-Since", HeaderUtils.toDateHeaderString(ifUnmodifiedSince));

		long lastModified = ifUnmodifiedSince + 999;
		boolean notModified = ServletUtils.checkPrecondition(request, response, lastModified);
		Assert.assertTrue(notModified);
	}

	@Test
	public void test_checkPrecondition_lastModified_modified() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		MockHttpServletResponse response = new MockHttpServletResponse(servletContext);

		long ifUnmodifiedSince = DateUtils.clearMs(System.currentTimeMillis());
		request.setHeaderValues("If-Unmodified-Since", HeaderUtils.toDateHeaderString(ifUnmodifiedSince));

		long lastModified = ifUnmodifiedSince + 1000;
		boolean notModified = ServletUtils.checkPrecondition(request, response, lastModified);
		Assert.assertFalse(notModified);
	}

	@Test
	public void test_checkPrecondition_etag() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		MockHttpServletResponse response = new MockHttpServletResponse(servletContext);

		String ifMatch = "etag";
		request.setHeaderValues("If-Match", ifMatch);

		String etag = "etag";
		boolean notModified = ServletUtils.checkPrecondition(request, response, etag);
		Assert.assertTrue(notModified);
	}

	@Test
	public void test_checkPrecondition_etag_modified() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
		MockHttpServletResponse response = new MockHttpServletResponse(servletContext);

		String ifMatch = "etag";
		request.setHeaderValues("If-Match", ifMatch);

		String etag = "etag1";
		boolean notModified = ServletUtils.checkPrecondition(request, response, etag);
		Assert.assertFalse(notModified);
	}
}
