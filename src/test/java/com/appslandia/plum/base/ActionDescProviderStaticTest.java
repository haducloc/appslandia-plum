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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ActionDescProviderStaticTest {

	@Test
	public void test_parsePathParams() {
		List<PathParam> pathParams = ActionDescProvider.parsePathParams("/{param0}");
		Assert.assertEquals(pathParams.size(), 1);
		Assert.assertEquals(pathParams.get(0).getParamName(), "param0");
		Assert.assertEquals(pathParams.get(0).getSubParams(), null);

		pathParams = ActionDescProvider.parsePathParams("/{param0}/{param1}");
		Assert.assertEquals(pathParams.size(), 2);

		Assert.assertEquals(pathParams.get(0).getParamName(), "param0");
		Assert.assertNull(pathParams.get(0).getSubParams());
		Assert.assertEquals(pathParams.get(1).getParamName(), "param1");
		Assert.assertNull(pathParams.get(1).getSubParams());
	}

	@Test
	public void test_parsePathParams_subParams() {
		List<PathParam> pathParams = ActionDescProvider.parsePathParams("/{param0}-{param1}/{param2}");
		Assert.assertEquals(pathParams.size(), 2);

		Assert.assertEquals(pathParams.get(0).getParamName(), null);
		Assert.assertNotNull(pathParams.get(0).getSubParams());

		List<PathParam> subParams = pathParams.get(0).getSubParams();
		Assert.assertEquals(subParams.size(), 2);

		Assert.assertEquals(subParams.get(0).getParamName(), "param0");
		Assert.assertNull(subParams.get(0).getSubParams());

		Assert.assertEquals(subParams.get(1).getParamName(), "param1");
		Assert.assertNull(subParams.get(1).getSubParams());

		Assert.assertEquals(pathParams.get(1).getParamName(), "param2");
		Assert.assertNull(pathParams.get(1).getSubParams());
	}

	@Test
	public void test_parseSubParams() {
		List<PathParam> pathParams = ActionDescProvider.parseSubParams("{param0}-{param1}");
		Assert.assertEquals(pathParams.size(), 2);

		Assert.assertEquals(pathParams.get(0).getParamName(), "param0");
		Assert.assertNull(pathParams.get(0).getSubParams());

		Assert.assertEquals(pathParams.get(1).getParamName(), "param1");
		Assert.assertNull(pathParams.get(1).getSubParams());
	}

	@Test
	public void test_getPathParamCount() {
		List<PathParam> pathParams = ActionDescProvider.parsePathParams("/{param0}");
		int pathParamCount = ActionDescProvider.getPathParamCount(pathParams);
		Assert.assertEquals(pathParamCount, 1);
	}

	@Test
	public void test_getPathParamCount_subParams() {
		List<PathParam> pathParams = ActionDescProvider.parsePathParams("/{param0}-{param1}/{param2}");
		int pathParamCount = ActionDescProvider.getPathParamCount(pathParams);
		Assert.assertEquals(pathParamCount, 3);
	}
}
