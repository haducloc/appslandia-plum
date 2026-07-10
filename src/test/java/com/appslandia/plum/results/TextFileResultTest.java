// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
import com.appslandia.common.utils.ContentTypes;
import com.appslandia.common.utils.IOUtils;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.ControllerBase;
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
  public static class TestController extends ControllerBase {

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
      super(fileName, ContentTypes.APP_CSV, encoding);
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
