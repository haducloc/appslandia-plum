// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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

import com.appslandia.common.utils.SplitUtils;
import com.appslandia.common.utils.TagList;
import com.appslandia.common.utils.URLEncoding;
import com.appslandia.plum.mocks.MockServletUtils;

import jakarta.servlet.http.Cookie;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class TagCookieHandlerTest extends MockTestBase {

  protected TagCookieHandler tagCookieHandler;

  @Override
  protected void initialize() {
    container.register(TestController.class, TestController.class);
    tagCookieHandler = container.getObject(TagCookieHandler.class);
  }

  @Test
  public void test_getTagList() {
    try {
      getCurrentRequest().addCookie(
          MockServletUtils.createCookie(getCurrentRequest().getServletContext(), "tags", "#tag1,#tag2", 1000));

      executeCurrent("GET", "http://localhost/app/testController/testAction");

      TagList tagList = tagCookieHandler.getTagList(getCurrentRequest(), null);
      Assertions.assertNotNull(tagList);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Test
  public void test_saveTags() {
    try {
      getCurrentRequest().addCookie(
          MockServletUtils.createCookie(getCurrentRequest().getServletContext(), "tags", "#tag1,#tag2", 1000));

      executeCurrent("GET", "http://localhost/app/testController/testAction");

      tagCookieHandler.saveTags(getCurrentRequest(), getCurrentResponse(), "tag3,tag4", null);
      Cookie tagsCookie = getCurrentResponse().getCookie(tagCookieHandler.getCookieName());

      Assertions.assertNotNull(tagsCookie);

      String decodedTags = URLEncoding.decodeParam(tagsCookie.getValue());
      String[] tags = SplitUtils.split(decodedTags, ',');

      Assertions.assertEquals(4, tags.length);

      Assertions.assertEquals("#tag1", tags[0]);
      Assertions.assertEquals("#tag2", tags[1]);
      Assertions.assertEquals("#tag3", tags[2]);
      Assertions.assertEquals("#tag4", tags[3]);

    } catch (Exception ex) {
      Assertions.fail(ex.getMessage());
    }
  }

  @Controller("testController")
  public static class TestController {

    @HttpGet
    public void testAction() throws Exception {
    }
  }
}
