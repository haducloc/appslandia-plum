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

import java.util.function.Supplier;

import com.appslandia.common.base.Out;
import com.appslandia.common.utils.SplitUtils;
import com.appslandia.common.utils.TagList;
import com.appslandia.common.utils.TagUtils;
import com.appslandia.common.utils.URLEncoding;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class TagCookieHandler {

  @Inject
  protected CookieHandler cookieHandler;

  protected abstract String getCookieName();

  protected abstract int getCookieAge();

  public void saveTags(HttpServletResponse response, TagList tagList) {
    String tags = URLEncoding.encodeParam(String.join(",", tagList));
    this.cookieHandler.saveCookie(response, getCookieName(), tags, getCookieAge(), null);
  }

  public TagList getTagList(HttpServletRequest request, Supplier<TagList> supplier) {
    // TagList
    TagList tagList = (TagList) request.getAttribute(getCookieName());
    if (tagList != null) {
      return tagList;
    }
    tagList = (supplier != null) ? supplier.get() : new TagList();
    request.setAttribute(getCookieName(), tagList);

    // tagsCookie
    String tagsCookie = this.cookieHandler.getCookieValue(request, getCookieName());
    if (tagsCookie == null) {
      return tagList;
    }
    try {
      String decodedTags = URLEncoding.decodeParam(tagsCookie);
      Out<Boolean> tagValid = new Out<>();

      for (String tag : SplitUtils.splitByComma(decodedTags)) {
        if (tagList.isPreTag(tag)) {
          continue;
        }
        tag = TagUtils.toTag(tag, tagValid);

        if (tagValid.get()) {
          tagList.add(tag);
        }
      }
    } catch (IllegalArgumentException ex) {
    }
    return tagList;
  }

  public void saveTags(HttpServletRequest request, HttpServletResponse response, String tags,
      Supplier<TagList> supplier) {
    if (tags == null) {
      return;
    }
    String[] parsedTags = TagUtils.toTags(tags, new Out<>()).stream().toArray(String[]::new);
    doSaveTags(request, response, parsedTags, supplier);
  }

  private void doSaveTags(HttpServletRequest request, HttpServletResponse response, String[] tags,
      Supplier<TagList> supplier) {
    if (tags == null || tags.length == 0) {
      return;
    }
    TagList tagList = getTagList(request, supplier);
    boolean updated = false;

    for (String tag : tags) {
      if (tagList.add(tag)) {
        updated = true;
      }
    }
    if (updated) {
      saveTags(response, tagList);
    }
  }
}
