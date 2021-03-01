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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.appslandia.common.base.Params;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ActionParserStaticTest {

	@Test
	public void test_parseSubParams() {
		List<PathParam> pathParams = ActionDescProvider.parsePathParams("/{param0}-{param1}");
		Assert.assertEquals(pathParams.size(), 1);

		PathParam pathParam = pathParams.get(0);
		Map<String, String> pathParamMap = new HashMap<>();

		boolean parsed = ActionParser.parseSubParams("param0-param1", pathParam.getSubParams(), pathParamMap);
		Assert.assertTrue(parsed);

		Assert.assertEquals(pathParamMap.get("param0"), "param0");
		Assert.assertEquals(pathParamMap.get("param1"), "param1");
	}

	@Test
	public void test_parseSubParams_invalid() {
		List<PathParam> pathParams = ActionDescProvider.parsePathParams("/{param0}-{param1}");
		Assert.assertEquals(pathParams.size(), 1);

		PathParam pathParam = pathParams.get(0);
		Map<String, String> pathParamMap = new HashMap<>();

		boolean parsed = ActionParser.parseSubParams("param0", pathParam.getSubParams(), pathParamMap);
		Assert.assertFalse(parsed);

		pathParamMap.clear();
		parsed = ActionParser.parseSubParams("-param0", pathParam.getSubParams(), pathParamMap);
		Assert.assertFalse(parsed);

		pathParamMap.clear();
		parsed = ActionParser.parseSubParams("param0-", pathParam.getSubParams(), pathParamMap);
		Assert.assertFalse(parsed);

		pathParamMap.clear();
		parsed = ActionParser.parseSubParams("param0_param1", pathParam.getSubParams(), pathParamMap);
		Assert.assertFalse(parsed);
	}

	@Test
	public void test_addPathParams() {
		List<PathParam> pathParams = ActionDescProvider.parsePathParams("/{param0}/{param1}-{param2}");
		Assert.assertEquals(pathParams.size(), 2);

		Map<String, Object> parameters = new Params();
		parameters.put("param0", "param0");
		parameters.put("param1", "param1");
		parameters.put("param2", "param2");

		StringBuilder url = new StringBuilder("http://myDomain.com");
		ActionParser.addPathParams(url, parameters, pathParams);
		Assert.assertEquals(url.toString(), "http://myDomain.com/param0/param1-param2");
	}

	@Test
	public void test_addPathParams_missedParams() {
		List<PathParam> pathParams = ActionDescProvider.parsePathParams("/{param0}/{param1}-{param2}");
		Assert.assertEquals(pathParams.size(), 2);

		Map<String, Object> parameters = new Params();
		parameters.put("param1", "param1");
		parameters.put("param2", "param2");

		StringBuilder url = new StringBuilder("http://myDomain.com");
		try {
			ActionParser.addPathParams(url, parameters, pathParams);
			Assert.fail();
		} catch (Exception ex) {
		}

		parameters = new Params();
		parameters.put("param0", "param0");
		parameters.put("param1", "param1");
		url = new StringBuilder("http://myDomain.com");
		try {
			ActionParser.addPathParams(url, parameters, pathParams);
			Assert.fail();
		} catch (Exception ex) {
		}
	}

	@Test
	public void test_addQueryParams() {
		List<PathParam> pathParams = ActionDescProvider.parsePathParams("/{param0}");
		Assert.assertEquals(pathParams.size(), 1);

		Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put("param0", "param0");
		parameters.put("param1", "param1");
		parameters.put("param2", "param2");

		StringBuilder url = new StringBuilder();
		ActionParser.addQueryParams(url, parameters, pathParams);
		Assert.assertEquals(url.toString(), "param1=param1&param2=param2");
	}
}
