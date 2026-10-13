// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.Cookie;

/**
 *
 * @author Loc Ha
 *
 */
public class PrefCookieHandlerTest extends MockTestBase {

  PrefCookieHandler prefCookieHandler;
  RequestContextParser requestContextParser;

  // MockAppConfig
  final String module = "memForm";

  protected String getCookieName() {
    return prefCookieHandler.getCookieName(module);
  }

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);

    prefCookieHandler = container.getObject(PrefCookieHandler.class);
    requestContextParser = container.getObject(RequestContextParser.class);
  }

  private Cookie createPrefCookie() {
    var request = container.createRequest();
    var response = container.createResponse();

    request.setRequestURL("http://localhost/app/test/index");
    requestContextParser.initRequestContext(request, response);

    var map = Map.of("state", "NE");
    prefCookieHandler.savePrefCookie(request, response, new PrefCookie(map));
    return response.getCookie(getCookieName());
  }

  @Test
  public void test_savePrefCookie() {
    try {
      getCurrentRequest().setRequestURL("http://localhost/app/test/index");
      requestContextParser.initRequestContext(getCurrentRequest(), getCurrentResponse());

      var map = Map.of("state", "NE");
      prefCookieHandler.savePrefCookie(getCurrentRequest(), getCurrentResponse(), new PrefCookie(map));

      var savedCookie = getCurrentResponse().getCookie(getCookieName());
      Assertions.assertNotNull(savedCookie);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_loadPrefCookie() {
    try {
      getCurrentRequest().addCookie(createPrefCookie());

      var prefCookie = prefCookieHandler.loadPrefCookie(getCurrentRequest(), getCurrentResponse(), module);
      Assertions.assertNotNull(prefCookie);
      Assertions.assertEquals("NE", prefCookie.get("state"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_loadPrefCookie_noCookie() {
    try {
      var prefCookie = prefCookieHandler.loadPrefCookie(getCurrentRequest(), getCurrentResponse(), module);
      Assertions.assertEquals(PrefCookie.EMPTY, prefCookie);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_loadPrefCookie_invalidCookie() {
    try {
      getCurrentRequest().addCookie(new Cookie(getCookieName(), "invalid"));
      var prefCookie = prefCookieHandler.loadPrefCookie(getCurrentRequest(), getCurrentResponse(), module);
      Assertions.assertEquals(PrefCookie.EMPTY, prefCookie);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("test")
  public static class TestController extends ControllerBase {

    @HttpGet
    public void index() throws Exception {
    }
  }
}
