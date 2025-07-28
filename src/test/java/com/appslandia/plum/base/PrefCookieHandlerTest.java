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

    prefCookieHandler.savePrefCookie(request, response, new PrefCookie().set("state", "NE"));
    return response.getCookie(prefCookieHandler.getCookieName());
  }

  @Test
  public void test_savePrefCookie() {
    try {
      prefCookieHandler.savePrefCookie(getCurrentRequest(), getCurrentResponse(), new PrefCookie().set("state", "NE"));

      var savedCookie = getCurrentResponse().getCookie(prefCookieHandler.getCookieName());
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
      getCurrentRequest().addCookie(new Cookie(prefCookieHandler.getCookieName(), "invalid"));
      var prefCookie = prefCookieHandler.loadPrefCookie(getCurrentRequest(), getCurrentResponse());
      Assertions.assertEquals(PrefCookie.EMPTY, prefCookie);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }
}
