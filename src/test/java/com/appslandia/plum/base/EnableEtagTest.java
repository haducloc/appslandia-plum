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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Loc Ha
 *
 */
public class EnableEtagTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(DocumentController.class, DocumentController.class);
  }

  protected String initEtag() throws Exception {
    var request = container.createRequest("GET", "http://localhost/app/documentController/document/1");
    var response = container.createResponse();
    execute(request, response);

    var etag = response.getHeader("ETag");
    Assertions.assertNotNull(etag);
    return etag;
  }

  @Test
  public void test_document() {
    try {
      executeCurrent("GET", "http://localhost/app/documentController/document/1");

      Assertions.assertEquals(200, getCurrentResponse().getStatus());
      Assertions.assertNotNull(getCurrentResponse().getHeader("Content-Length"));
      Assertions.assertNotNull(getCurrentResponse().getHeader("ETag"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_document_HEAD() {
    try {
      executeCurrent("HEAD", "http://localhost/app/documentController/document/1");

      Assertions.assertEquals(200, getCurrentResponse().getStatus());
      Assertions.assertNotNull(getCurrentResponse().getHeader("Content-Length"));
      Assertions.assertNotNull(getCurrentResponse().getHeader("ETag"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_document_notModified() {
    try {
      var ifNoneMatch = initEtag();
      getCurrentRequest().setHeader("If-None-Match", ifNoneMatch);

      executeCurrent("GET", "http://localhost/app/documentController/document/1");

      Assertions.assertEquals(304, getCurrentResponse().getStatus());
      var etag = getCurrentResponse().getHeader("ETag");
      Assertions.assertEquals(etag, ifNoneMatch);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_document_notModified_HEAD() {
    try {
      var ifNoneMatch = initEtag();
      getCurrentRequest().setHeader("If-None-Match", ifNoneMatch);

      executeCurrent("HEAD", "http://localhost/app/documentController/document/1");

      Assertions.assertEquals(304, getCurrentResponse().getStatus());
      var etag = getCurrentResponse().getHeader("ETag");
      Assertions.assertEquals(etag, ifNoneMatch);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("documentController")
  public static class DocumentController {

    @HttpGet
    @PathParams("/{id}")
    @EnableEtag
    public Document document(int id) throws Exception {
      var doc = new Document();
      doc.id = id;
      doc.name = "Document-" + id;
      doc.content = "Content-" + id;
      return doc;
    }
  }

  static class Document {
    int id;
    String name;
    String content;
  }
}
