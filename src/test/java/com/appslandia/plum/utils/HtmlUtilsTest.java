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

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.appslandia.common.base.StringWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class HtmlUtilsTest {

	@Test
	public void test_attribute() {
		StringWriter out = new StringWriter();
		try {
			HtmlUtils.attribute(out, "method", "POST");
			Assert.assertTrue(out.toString().contains("method=\"POST\""));
		} catch (IOException ex) {
			Assert.fail(ex.getMessage());
		}

		out = new StringWriter();
		try {
			HtmlUtils.attribute(out, "method", "");
			Assert.assertTrue(out.toString().contains("method=\"\""));
		} catch (IOException ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_escapeAttribute() {
		StringWriter out = new StringWriter();
		try {
			HtmlUtils.escAttribute(out, "title", "test < title");
			Assert.assertTrue(out.toString().contains("title=\"test &lt; title\""));
		} catch (IOException ex) {
			Assert.fail(ex.getMessage());
		}

		out = new StringWriter();
		try {
			HtmlUtils.escAttribute(out, "title", "");
			Assert.assertTrue(out.toString().contains("title=\"\""));
		} catch (IOException ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void test_buildId() {
		String id = HtmlUtils.buildId("user.userName");
		Assert.assertEquals(id, "user_userName");

		id = HtmlUtils.buildId("user.location.address");
		Assert.assertEquals(id, "user_location_address");

		id = HtmlUtils.buildId("user.addresses[1].location");
		Assert.assertEquals(id, "user_addresses_1__location");
	}
}
