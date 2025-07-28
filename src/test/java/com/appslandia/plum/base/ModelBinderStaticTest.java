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

package com.appslandia.plum.base;

import java.lang.reflect.Array;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.base.FormatProvider;
import com.appslandia.common.base.FormatProviderImpl;
import com.appslandia.common.base.Language;
import com.appslandia.common.base.Out;
import com.appslandia.common.converters.Converter;
import com.appslandia.common.converters.ConverterProvider;

/**
 *
 * @author Loc Ha
 *
 */
public class ModelBinderStaticTest {

  static final ConverterProvider converterProvider = new ConverterProvider();
  final FormatProvider formatProvider = new FormatProviderImpl(Language.EN_US);

  @Test
  public void test_string() {
    Converter<String> converter = converterProvider.getConverter(String.class);

    var parsedErrorMsg = new Out<String>();
    var result = ModelBinder.parseValue(" data ", String.class, parsedErrorMsg, converter, formatProvider);
    Assertions.assertNotNull(result);
    Assertions.assertEquals("data", result);
    Assertions.assertNull(parsedErrorMsg.value);

    // Null
    parsedErrorMsg = new Out<>();
    result = ModelBinder.parseValue(" ", String.class, parsedErrorMsg, converter, formatProvider);
    Assertions.assertNull(result);
    Assertions.assertNull(parsedErrorMsg.value);
  }

  @Test
  public void test_parseValue() {
    Converter<Integer> converter = converterProvider.getConverter(Integer.class);

    // Valid values
    var parsedErrorMsg = new Out<String>();
    var result = ModelBinder.parseValue("100", int.class, parsedErrorMsg, converter, formatProvider);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(100, result);
    Assertions.assertNull(parsedErrorMsg.value);

    // Null
    parsedErrorMsg = new Out<>();
    result = ModelBinder.parseValue(null, int.class, parsedErrorMsg, converter, formatProvider);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(0, result);
    Assertions.assertNotNull(parsedErrorMsg.value);

    // Invalid values
    parsedErrorMsg = new Out<>();
    result = ModelBinder.parseValue("invalid", int.class, parsedErrorMsg, converter, formatProvider);
    Assertions.assertEquals(0, result);
    Assertions.assertNotNull(parsedErrorMsg.value);
  }

  @Test
  public void test_parseValue_wrappers() {
    Converter<Integer> converter = converterProvider.getConverter(Integer.class);

    // Valid values
    var parsedErrorMsg = new Out<String>();
    var result = ModelBinder.parseValue("100", Integer.class, parsedErrorMsg, converter, formatProvider);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(100, result);
    Assertions.assertNull(parsedErrorMsg.value);

    // Null
    parsedErrorMsg = new Out<>();
    result = ModelBinder.parseValue(null, Integer.class, parsedErrorMsg, converter, formatProvider);
    Assertions.assertNull(result);
    Assertions.assertNull(parsedErrorMsg.value);

    // Invalid values
    parsedErrorMsg = new Out<>();
    result = ModelBinder.parseValue("invalid", Integer.class, parsedErrorMsg, converter, formatProvider);
    Assertions.assertNull(result);
    Assertions.assertNotNull(parsedErrorMsg.value);
  }

  @Test
  public void test_parseArray() {
    Converter<Integer> converter = converterProvider.getConverter(Integer.class);

    // Valid values
    var parsedErrorMsg = new Out<String>();
    var array = ModelBinder.parseArray(new String[] { "100", "2000" }, int.class, parsedErrorMsg, converter,
        formatProvider);
    Assertions.assertNotNull(array);
    Assertions.assertEquals(100, Array.get(array, 0));
    Assertions.assertEquals(2000, Array.get(array, 1));
    Assertions.assertNull(parsedErrorMsg.value);

    // Invalid values
    parsedErrorMsg = new Out<>();
    array = ModelBinder.parseArray(new String[] { "100", "2000", null, "invalid" }, int.class, parsedErrorMsg,
        converter, formatProvider);
    Assertions.assertNotNull(array);
    Assertions.assertEquals(100, Array.get(array, 0));
    Assertions.assertEquals(2000, Array.get(array, 1));
    Assertions.assertEquals(0, Array.get(array, 2));
    Assertions.assertEquals(0, Array.get(array, 3));
    Assertions.assertNotNull(parsedErrorMsg.value);
  }

  @Test
  public void test_parseArray_wrappers() {
    Converter<Integer> converter = converterProvider.getConverter(Integer.class);

    // Valid values
    var parsedErrorMsg = new Out<String>();
    var array = ModelBinder.parseArray(new String[] { "100", "2000" }, Integer.class, parsedErrorMsg, converter,
        formatProvider);
    Assertions.assertNotNull(array);
    Assertions.assertEquals(100, Array.get(array, 0));
    Assertions.assertEquals(2000, Array.get(array, 1));
    Assertions.assertNull(parsedErrorMsg.value);

    // Invalid values
    parsedErrorMsg = new Out<>();
    array = ModelBinder.parseArray(new String[] { "100", "2000", null, "invalid" }, Integer.class, parsedErrorMsg,
        converter, formatProvider);
    Assertions.assertNotNull(array);
    Assertions.assertEquals(100, Array.get(array, 0));
    Assertions.assertEquals(2000, Array.get(array, 1));
    Assertions.assertEquals(null, Array.get(array, 2));
    Assertions.assertEquals(null, Array.get(array, 3));
    Assertions.assertNotNull(parsedErrorMsg.value);
  }

  @Test
  public void test_toMultiValues() {
    var values = ModelBinder.toMultiValues(null);
    Assertions.assertNull(values);

    values = ModelBinder.toMultiValues(new int[] { 1, 2, 3 });
    Assertions.assertEquals("1,2,3", values);

    values = ModelBinder.toMultiValues(new String[] { "1", null, "3" });
    Assertions.assertEquals("1,3", values);
  }
}
