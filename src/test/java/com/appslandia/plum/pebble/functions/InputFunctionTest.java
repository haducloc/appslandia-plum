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

package com.appslandia.plum.pebble.functions;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.appslandia.common.base.Params;
import com.appslandia.common.base.StringWriter;
import com.appslandia.plum.base.ActionResult;
import com.appslandia.plum.base.BrowserFeatures;
import com.appslandia.plum.base.Controller;
import com.appslandia.plum.base.HttpGet;
import com.appslandia.plum.base.MockTestBase;
import com.appslandia.plum.base.Model;
import com.appslandia.plum.base.RequestAccessor;
import com.appslandia.plum.mocks.MemPebbleTemplateProvider;
import com.appslandia.plum.pebble.PebbleUtils;

import jakarta.servlet.http.Cookie;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class InputFunctionTest extends MockTestBase {

    protected MemPebbleTemplateProvider pebbleTemplateProvider;

    @Override
    protected void initialize() {
	container.register(TestController.class, TestController.class);

	pebbleTemplateProvider = container.getObject(MemPebbleTemplateProvider.class);
    }

    @Test
    public void test_text() {
	String templateContent = """
		{{ input(path='model.dob') }}
		""";
	pebbleTemplateProvider.addTemplate("/WEB-INF/pebble/index.peb", templateContent.trim());

	try {
	    getCurrentRequest().addParameter("dob", "1980-01-01");
	    executeCurrent("GET", "http://localhost/app/testController/index");

	    Map<String, Object> model = new Params().set("model", getCurrentRequest().getAttribute("model"));

	    StringWriter out = new StringWriter();
	    PebbleUtils.executePebble(getCurrentRequest(), getCurrentResponse(), out, "/WEB-INF/pebble/index.peb", model, getCurrentRequestContext().getLanguage().getLocale());

	    String content = out.toString();
	    Assertions.assertEquals("id=\"dob\" name=\"dob\" value=\"01/01/1980\" type=\"text\"", content);

	} catch (Exception ex) {
	    Assertions.fail(ex);
	}
    }

    @Test
    public void test_date() {
	String templateContent = """
		{{ input(path='model.dob', type='date') }}
		""";
	pebbleTemplateProvider.addTemplate("/WEB-INF/pebble/index.peb", templateContent.trim());

	try {
	    getCurrentRequest().addParameter("dob", "1980-01-01");
	    executeCurrent("GET", "http://localhost/app/testController/index");

	    Map<String, Object> model = new Params().set("model", getCurrentRequest().getAttribute("model"));

	    StringWriter out = new StringWriter();
	    PebbleUtils.executePebble(getCurrentRequest(), getCurrentResponse(), out, "/WEB-INF/pebble/index.peb", model, getCurrentRequestContext().getLanguage().getLocale());

	    String content = out.toString();

	    // HTML5 date not supported
	    // date -> text & localize
	    Assertions.assertEquals("id=\"dob\" name=\"dob\" value=\"01/01/1980\" type=\"text\"", content);

	} catch (Exception ex) {
	    Assertions.fail(ex);
	}
    }

    @Test
    public void test_date_html5() {
	String templateContent = """
		{{ input(path='model.dob', type='date') }}
		""";
	pebbleTemplateProvider.addTemplate("/WEB-INF/pebble/index.peb", templateContent.trim());

	try {
	    // BrowserFeatures.INPUT_DATE
	    getCurrentRequest().addCookie(new Cookie("browserFeatures", String.valueOf(BrowserFeatures.INPUT_DATE)));
	    getCurrentRequest().addParameter("dob", "1980-01-01");

	    executeCurrent("GET", "http://localhost/app/testController/index");

	    Map<String, Object> model = new Params().set("model", getCurrentRequest().getAttribute("model"));

	    StringWriter out = new StringWriter();
	    PebbleUtils.executePebble(getCurrentRequest(), getCurrentResponse(), out, "/WEB-INF/pebble/index.peb", model, getCurrentRequestContext().getLanguage().getLocale());

	    String content = out.toString();

	    // HTML5 date supported
	    Assertions.assertEquals("id=\"dob\" name=\"dob\" value=\"1980-01-01\" type=\"date\"", content);

	} catch (Exception ex) {
	    Assertions.fail(ex);
	}
    }

    @Controller("testController")
    public static class TestController {

	@HttpGet
	public ActionResult index(RequestAccessor request, @Model UserModel model) throws Exception {
	    request.storeModel(model);

	    return ActionResult.EMPTY;
	}
    }

    public static class UserModel {

	@NotNull
	private LocalDate dob;

	public LocalDate getDob() {
	    return dob;
	}

	public void setDob(LocalDate dob) {
	    this.dob = dob;
	}
    }
}
