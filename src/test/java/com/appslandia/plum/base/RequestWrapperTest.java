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

package com.appslandia.plum.base;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.appslandia.plum.mocks.MockHttpServletRequest;
import com.appslandia.plum.mocks.MockServletContext;
import com.appslandia.plum.mocks.MockSessionCookieConfig;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class RequestWrapperTest {

	MockServletContext servletContext;

	@Before
	public void onInvokingTest() {
		servletContext = new MockServletContext(new MockSessionCookieConfig());
	}

	@Test
	public void test() {
		MockHttpServletRequest request = new MockHttpServletRequest(servletContext);

		request.addParameter("p1", "v1");
		request.addParameter("p2", "v2");
		request.addParameter("p3", "v31", "v32");

		Map<String, String> pathParamMap = new HashMap<>();
		pathParamMap.put("p2", "v22");
		pathParamMap.put("p4", "v4");

		RequestWrapper requestWrapper = new RequestWrapper(request, pathParamMap);
		try {
			Assert.assertEquals(requestWrapper.getParameter("p1"), "v1");
			Assert.assertEquals(requestWrapper.getParameter("p4"), "v4");

			// p2 is path parameter
			// value orders
			Assert.assertEquals(requestWrapper.getParameterValues("p2")[0], "v22");
			Assert.assertEquals(requestWrapper.getParameterValues("p2")[1], "v2");

			Assert.assertEquals(requestWrapper.getParameterValues("p3")[0], "v31");
			Assert.assertEquals(requestWrapper.getParameterValues("p3")[1], "v32");

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}
	}
}
