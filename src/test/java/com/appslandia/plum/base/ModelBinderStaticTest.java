// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
  public void test_toMultiValue() {
    var values = ModelBinder.toMultiValue(null);
    Assertions.assertNull(values);

    values = ModelBinder.toMultiValue(new int[] { 1, 2, 3 });
    Assertions.assertEquals("1,2,3", values);

    values = ModelBinder.toMultiValue(new String[] { "1", null, "3" });
    Assertions.assertEquals("1,3", values);
  }
}
