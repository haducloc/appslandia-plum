// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
  public static class DocumentController extends ControllerBase {

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
