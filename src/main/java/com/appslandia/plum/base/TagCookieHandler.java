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

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.appslandia.common.base.Out;
import com.appslandia.common.utils.SplitUtils;
import com.appslandia.common.utils.TagList;
import com.appslandia.common.utils.TagUtils;
import com.appslandia.common.utils.URLEncoding;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@ApplicationScoped
public class TagCookieHandler {

    public static final String CONFIG_COOKIE_AGE = TagCookieHandler.class.getName() + ".cookie_age";
    public static final int DEFAULT_COOKIE_AGE = (int) TimeUnit.SECONDS.convert(360, TimeUnit.DAYS);

    @Inject
    protected AppConfig appConfig;

    @Inject
    protected CookieHandler cookieHandler;

    protected int getCookieAge() {
	return this.appConfig.getInt(CONFIG_COOKIE_AGE, DEFAULT_COOKIE_AGE);
    }

    public void saveTags(HttpServletResponse response, String cookieName, TagList tagList) {
	String tags = URLEncoding.encodeParam(String.join(",", tagList));
	this.cookieHandler.saveCookie(response, cookieName, tags, getCookieAge(), null);
    }

    public TagList getTagList(HttpServletRequest request, String cookieName, Supplier<TagList> tagListNewer) {
	TagList tagList = (TagList) request.getAttribute(cookieName);
	if (tagList != null) {
	    return tagList;
	}
	tagList = (tagListNewer != null) ? tagListNewer.get() : new TagList();
	request.setAttribute(cookieName, tagList);

	String tagsCookie = this.cookieHandler.getCookieValue(request, cookieName);
	if (tagsCookie == null) {
	    return tagList;
	}
	// Parse Tags
	try {
	    String decodedTags = URLEncoding.decodeParam(tagsCookie);
	    Out<Boolean> tagValid = new Out<>();

	    for (String tag : SplitUtils.split(decodedTags, ',')) {
		if (tagList.isPreTag(tag)) {
		    continue;
		}
		tag = TagUtils.toTag(tag, tagValid);

		if (tagValid.val()) {
		    tagList.add(tag);
		}
	    }
	} catch (IllegalArgumentException ex) {
	}
	return tagList;
    }

    public void replaceTag(HttpServletRequest request, HttpServletResponse response, String tag, String byTag, String cookieName, Supplier<TagList> tagListNewer) {
	TagList tagList = getTagList(request, cookieName, tagListNewer);

	if (tagList.remove(tag) && tagList.add(byTag)) {
	    saveTags(response, cookieName, tagList);
	}
    }

    public void saveTag(HttpServletRequest request, HttpServletResponse response, String tag, String cookieName, Supplier<TagList> tagListNewer) {
	if (tag == null) {
	    return;
	}
	doSaveTags(request, response, new String[] { tag }, cookieName, tagListNewer);
    }

    public void saveTags(HttpServletRequest request, HttpServletResponse response, String tags, String cookieName, Supplier<TagList> tagListNewer) {
	if (tags == null) {
	    return;
	}
	String[] parsedTags = TagUtils.toTags(tags, new Out<>()).stream().toArray(String[]::new);
	doSaveTags(request, response, parsedTags, cookieName, tagListNewer);
    }

    public void saveDbTags(HttpServletRequest request, HttpServletResponse response, String dbTags, String cookieName, Supplier<TagList> tagListNewer) {
	if (dbTags == null) {
	    return;
	}
	doSaveTags(request, response, TagUtils.toTags(dbTags), cookieName, tagListNewer);
    }

    void doSaveTags(HttpServletRequest request, HttpServletResponse response, String[] tags, String cookieName, Supplier<TagList> tagListNewer) {
	if (tags == null || tags.length == 0) {
	    return;
	}
	TagList tagList = getTagList(request, cookieName, tagListNewer);

	boolean update = false;
	for (String tag : tags) {

	    if (tagList.add(tag)) {
		update = true;
	    }
	}
	if (update) {
	    saveTags(response, cookieName, tagList);
	}
    }
}
