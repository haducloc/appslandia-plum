// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.plum.utils.HeaderUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class LastModifiedTest extends MockTestBase {

  @Override
  protected void initialize() {
    container.register(DocumentController.class, DocumentController.class);
  }

  protected String initLastModified() throws Exception {
    var request = container.createRequest("GET", "http://localhost/app/documentController/document/1");
    var response = container.createResponse();
    execute(request, response);

    var lastModified = response.getHeader("Last-Modified");
    Assertions.assertNotNull(lastModified);
    return lastModified;
  }

  @Test
  public void test_document_GET() {
    try {
      executeCurrent("GET", "http://localhost/app/documentController/document/1");

      Assertions.assertEquals(200, getCurrentResponse().getStatus());
      Assertions.assertNotNull(getCurrentResponse().getHeader("Last-Modified"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_document_notModified() {
    try {
      var ifModifiedSince = initLastModified();
      getCurrentRequest().setHeader("If-Modified-Since", ifModifiedSince);

      executeCurrent("GET", "http://localhost/app/documentController/document/1");

      Assertions.assertEquals(304, getCurrentResponse().getStatus());
      var lastModified = getCurrentResponse().getHeader("Last-Modified");
      Assertions.assertEquals(lastModified, ifModifiedSince);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("documentController")
  public static class DocumentController extends ControllerBase {

    @HttpGet
    @PathParams("/{id}")
    public Document document(HttpServletRequest request, HttpServletResponse response, int id) throws Exception {
      var doc = new Document();
      doc.id = id;
      doc.name = "Document-" + id;
      doc.content = "Content-" + id;

      if (ServletUtils.checkNotModified(request, response, getLastModifiedMs(doc))) {
        return null;
      }
      return doc;
    }

    protected long getLastModifiedMs(Document doc) {
      return HeaderUtils.parseDateHeader("Sun, 01 Jan 2017 06:00:00 GMT");
    }
  }

  static class Document {
    int id;
    String name;
    String content;
  }
}
