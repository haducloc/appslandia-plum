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

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class HeaderBuilderTest {

	@Test
	public void test() {
		HeaderBuilder builder = new HeaderBuilder();

		builder.addValue("key1");
		builder.addPair("key2", "value2");
		builder.addPair("key3", "value3");
		builder.addValues("key4", "key5");

		Assert.assertEquals(builder.toString(), "key1, key2=value2, key3=value3, key4, key5");
	}

	@Test
	public void test_customSeparator() {
		HeaderBuilder builder = new HeaderBuilder("; ");

		builder.addValue("key1");
		builder.addPair("key2", "value2");
		builder.addPair("key3", "value3");
		builder.addValues("key4", "key5");

		Assert.assertEquals(builder.toString(), "key1; key2=value2; key3=value3; key4; key5");
	}

	@Test
	public void test_override() {
		HeaderBuilder builder = new HeaderBuilder("; ");

		builder.addValue("key1");
		builder.addValues("key2", "key1");

		Assert.assertEquals(builder.toString(), "key1; key2");

		builder = new HeaderBuilder("; ");

		builder.addPair("key1", "value1");
		builder.addPair("key2", "value2");
		builder.addPair("key1", "value11"); // Override value1

		Assert.assertEquals(builder.toString(), "key1=value11; key2=value2");
	}
}
