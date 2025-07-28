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

package com.appslandia.plum.results;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.csv.CsvProcessor;
import com.appslandia.common.csv.CsvRecord;
import com.appslandia.common.utils.IOUtils;
import com.appslandia.common.utils.MimeTypes;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;

/**
 *
 * @author Loc Ha
 *
 */
public class TextFileResultTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
  }

  @Test
  public void test_testCsvResult() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testCsvResult");

      Assertions.assertEquals("application/csv", getCurrentResponse().getContentType());
      Assertions.assertEquals(StandardCharsets.UTF_8.name(), getCurrentResponse().getCharacterEncoding());

      try (var reader = IOUtils.readerBOM(new ByteArrayInputStream(getCurrentResponse().getContent().toByteArray()),
          StandardCharsets.UTF_8.name())) {

        var records = CsvProcessor.INSTANCE.parseRecords(reader, true);

        Assertions.assertTrue(records.size() == 1);
        var recordAsString = records.get(0).toString();

        Assertions.assertEquals("val1,val2,val3", recordAsString);

      }

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_testCsvResult_ISO_8859_1() {
    try {
      executeCurrent("GET", "http://localhost/app/testController/testCsvResult_ISO_8859_1");

      Assertions.assertEquals("application/csv", getCurrentResponse().getContentType());
      Assertions.assertEquals(StandardCharsets.ISO_8859_1.name(), getCurrentResponse().getCharacterEncoding());

      try (var reader = IOUtils.readerBOM(new ByteArrayInputStream(getCurrentResponse().getContent().toByteArray()),
          StandardCharsets.ISO_8859_1.name())) {

        var records = CsvProcessor.INSTANCE.parseRecords(reader, true);

        Assertions.assertTrue(records.size() == 1);
        var recordAsString = records.get(0).toString();

        Assertions.assertEquals("val1,val2,val3", recordAsString);

      }

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpGet
    public ActionResult testCsvResult() throws Exception {
      return new CsvFileResult("test.csv",
          Arrays.asList(new String[] { "item1", "item2", "item3" }, new String[] { "val1", "val2", "val3" }),
          StandardCharsets.UTF_8.name());
    }

    @HttpGet
    public ActionResult testCsvResult_ISO_8859_1() throws Exception {
      return new CsvFileResult("test.csv",
          Arrays.asList(new String[] { "item1", "item2", "item3" }, new String[] { "val1", "val2", "val3" }),
          StandardCharsets.ISO_8859_1.name());
    }

  }

  static class CsvFileResult extends TextFileResult {

    final List<String[]> records;

    public CsvFileResult(String fileName, List<String[]> records, String encoding) {
      super(fileName, MimeTypes.APP_CSV, encoding);
      this.records = records;
    }

    @Override
    protected BufferedWriter createBufferedWriter(OutputStream os, String charset) throws IOException {
      return IOUtils.writerBOM(os, charset);
    }

    @Override
    protected void writeContent(BufferedWriter out) throws Exception {
      for (String[] record : records) {
        out.write(new CsvRecord(record).toString());
        out.newLine();
      }
    }
  }
}
