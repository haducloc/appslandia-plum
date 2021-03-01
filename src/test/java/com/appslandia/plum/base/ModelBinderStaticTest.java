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

import java.lang.reflect.Array;

import org.junit.Assert;
import org.junit.Test;

import com.appslandia.common.base.FormatProvider;
import com.appslandia.common.base.FormatProviderImpl;
import com.appslandia.common.base.Language;
import com.appslandia.common.base.Out;
import com.appslandia.common.formatters.Formatter;
import com.appslandia.common.formatters.FormatterProvider;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ModelBinderStaticTest {

	final FormatProvider formatProvider = new FormatProviderImpl(Language.EN);

	@Test
	public void test_string() {
		Formatter formatter = FormatterProvider.getDefault().getFormatter(String.class);

		Out<String> parsedErrorMsg = new Out<>();
		Object result = ModelBinder.parseValue(" data ", String.class, parsedErrorMsg, formatter, formatProvider);
		Assert.assertNotNull(result);
		Assert.assertEquals(result, "data");
		Assert.assertNull(parsedErrorMsg.value);

		// Null
		parsedErrorMsg = new Out<>();
		result = ModelBinder.parseValue(" ", String.class, parsedErrorMsg, formatter, formatProvider);
		Assert.assertNull(result);
		Assert.assertNull(parsedErrorMsg.value);
	}

	@Test
	public void test_parseValue() {
		Formatter formatter = FormatterProvider.getDefault().getFormatter(Integer.class);

		// Valid values
		Out<String> parsedErrorMsg = new Out<>();
		Object result = ModelBinder.parseValue("100", int.class, parsedErrorMsg, formatter, formatProvider);
		Assert.assertNotNull(result);
		Assert.assertEquals(result, 100);
		Assert.assertNull(parsedErrorMsg.value);

		// Null
		parsedErrorMsg = new Out<>();
		result = ModelBinder.parseValue(null, int.class, parsedErrorMsg, formatter, formatProvider);
		Assert.assertNotNull(result);
		Assert.assertEquals(result, 0);
		Assert.assertNotNull(parsedErrorMsg.value);

		// Invalid values
		parsedErrorMsg = new Out<>();
		result = ModelBinder.parseValue("invalid", int.class, parsedErrorMsg, formatter, formatProvider);
		Assert.assertEquals(result, 0);
		Assert.assertNotNull(parsedErrorMsg.value);
	}

	@Test
	public void test_parseValue_wrappers() {
		Formatter formatter = FormatterProvider.getDefault().getFormatter(Integer.class);

		// Valid values
		Out<String> parsedErrorMsg = new Out<>();
		Object result = ModelBinder.parseValue("100", Integer.class, parsedErrorMsg, formatter, formatProvider);
		Assert.assertNotNull(result);
		Assert.assertEquals(result, 100);
		Assert.assertNull(parsedErrorMsg.value);

		// Null
		parsedErrorMsg = new Out<>();
		result = ModelBinder.parseValue(null, Integer.class, parsedErrorMsg, formatter, formatProvider);
		Assert.assertNull(result);
		Assert.assertNull(parsedErrorMsg.value);

		// Invalid values
		parsedErrorMsg = new Out<>();
		result = ModelBinder.parseValue("invalid", Integer.class, parsedErrorMsg, formatter, formatProvider);
		Assert.assertNull(result);
		Assert.assertNotNull(parsedErrorMsg.value);
	}

	@Test
	public void test_parseArray() {
		Formatter formatter = FormatterProvider.getDefault().getFormatter(Integer.class);

		// Valid values
		Out<String> parsedErrorMsg = new Out<>();
		Object array = ModelBinder.parseArray(new String[] { "100", "2000" }, int.class, parsedErrorMsg, formatter, formatProvider);
		Assert.assertNotNull(array);
		Assert.assertEquals(Array.get(array, 0), 100);
		Assert.assertEquals(Array.get(array, 1), 2000);
		Assert.assertNull(parsedErrorMsg.value);

		// Invalid values
		parsedErrorMsg = new Out<>();
		array = ModelBinder.parseArray(new String[] { "100", "2000", null, "invalid" }, int.class, parsedErrorMsg, formatter, formatProvider);
		Assert.assertNotNull(array);
		Assert.assertEquals(Array.get(array, 0), 100);
		Assert.assertEquals(Array.get(array, 1), 2000);
		Assert.assertEquals(Array.get(array, 2), 0);
		Assert.assertEquals(Array.get(array, 3), 0);
		Assert.assertNotNull(parsedErrorMsg.value);
	}

	@Test
	public void test_parseArray_wrappers() {
		Formatter formatter = FormatterProvider.getDefault().getFormatter(Integer.class);

		// Valid values
		Out<String> parsedErrorMsg = new Out<>();
		Object array = ModelBinder.parseArray(new String[] { "100", "2000" }, Integer.class, parsedErrorMsg, formatter, formatProvider);
		Assert.assertNotNull(array);
		Assert.assertEquals(Array.get(array, 0), 100);
		Assert.assertEquals(Array.get(array, 1), 2000);
		Assert.assertNull(parsedErrorMsg.value);

		// Invalid values
		parsedErrorMsg = new Out<>();
		array = ModelBinder.parseArray(new String[] { "100", "2000", null, "invalid" }, Integer.class, parsedErrorMsg, formatter, formatProvider);
		Assert.assertNotNull(array);
		Assert.assertEquals(Array.get(array, 0), 100);
		Assert.assertEquals(Array.get(array, 1), 2000);
		Assert.assertEquals(Array.get(array, 2), null);
		Assert.assertEquals(Array.get(array, 3), null);
		Assert.assertNotNull(parsedErrorMsg.value);
	}

	@Test
	public void test_toBitMask() {
		long bitMask = ModelBinder.toBitMask(new int[] {}, new Out<>());
		Assert.assertEquals(bitMask, 0);

		bitMask = ModelBinder.toBitMask(new int[] { 1, 2, 3 }, new Out<>());
		Assert.assertEquals(bitMask, 3);
	}

	@Test
	public void test_toBitMask_decimals() {
		long bitMask = ModelBinder.toBitMask(new double[] {}, new Out<>());
		Assert.assertEquals(bitMask, 0);

		bitMask = ModelBinder.toBitMask(new double[] { 1.0, 2.1, 2.2 }, new Out<>());
		Assert.assertEquals(bitMask, 3);
	}

	@Test
	public void test_toBitMask_invalids() {
		Out<Boolean> bitMaskResult = new Out<>();
		long bitMask = ModelBinder.toBitMask(new int[] { 1, 2, 2, 5 }, bitMaskResult);

		Assert.assertEquals(bitMask, 3);
		Assert.assertEquals(bitMaskResult.value, Boolean.FALSE);
	}
}
