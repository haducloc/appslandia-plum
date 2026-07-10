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

  @Override
  protected void initialize() {
    prefCookieHandler = container.getObject(PrefCookieHandler.class);
  }

  private Cookie createPrefCookie() {
    var request = container.createRequest();
    var response = container.createResponse();

    var map = Map.of("state", "NE");
    prefCookieHandler.savePrefCookie(request, response, new PrefCookie(map));
    return response.getCookie("__pref_cookie");
  }

  @Test
  public void test_savePrefCookie() {
    try {
      var map = Map.of("state", "NE");
      prefCookieHandler.savePrefCookie(getCurrentRequest(), getCurrentResponse(), new PrefCookie(map));

      var savedCookie = getCurrentResponse().getCookie("__pref_cookie");
      Assertions.assertNotNull(savedCookie);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_loadPrefCookie() {
    try {
      getCurrentRequest().addCookie(createPrefCookie());

      var prefCookie = prefCookieHandler.loadPrefCookie(getCurrentRequest(), getCurrentResponse());
      Assertions.assertNotNull(prefCookie);
      Assertions.assertEquals("NE", prefCookie.get("state"));

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_loadPrefCookie_noCookie() {
    try {
      var prefCookie = prefCookieHandler.loadPrefCookie(getCurrentRequest(), getCurrentResponse());
      Assertions.assertEquals(PrefCookie.EMPTY, prefCookie);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_loadPrefCookie_invalidCookie() {
    try {
      getCurrentRequest().addCookie(new Cookie("__pref_cookie", "invalid"));
      var prefCookie = prefCookieHandler.loadPrefCookie(getCurrentRequest(), getCurrentResponse());
      Assertions.assertEquals(PrefCookie.EMPTY, prefCookie);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }
}
