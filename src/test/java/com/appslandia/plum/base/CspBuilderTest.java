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

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class CspBuilderTest {

    @Test
    public void test() {
	CspBuilder csp = new CspBuilder();
	csp.defaultSrc("'self'");
	csp.imgSrc("https://*");
	csp.childSrc("'none'");
	Assertions.assertEquals("default-src 'self'; img-src https://*; child-src 'none'", csp.toString());
    }

    @Test
    public void test_baseUri() {
	CspBuilder csp = new CspBuilder();
	String value = "base1 base2";
	csp.baseUri(value);
	Assertions.assertEquals("base-uri base1 base2", csp.toString());
    }

    @Test
    public void test_baseUri_CspValueBuilder() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("base1").add("base2");
	csp.baseUri(builder);
	Assertions.assertEquals("base-uri base1 base2", csp.toString());
    }

    @Test
    public void test_blockAllMixedContent() {
	CspBuilder csp = new CspBuilder();
	csp.blockAllMixedContent(true);
	Assertions.assertEquals("block-all-mixed-content", csp.toString());

	csp = new CspBuilder();
	csp.blockAllMixedContent(false);
	Assertions.assertNull(csp.toString());
    }

    @Test
    public void test_childSrc() {
	CspBuilder csp = new CspBuilder();
	String value = "src1 src2";
	csp.childSrc(value);
	Assertions.assertEquals("child-src src1 src2", csp.toString());
    }

    @Test
    public void test_childSrc_CspValueBuilder() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.childSrc(builder);
	Assertions.assertEquals("child-src src1 src2", csp.toString());
    }

    @Test
    public void test_connectSrc() {
	CspBuilder csp = new CspBuilder();
	String value = "src1 src2";
	csp.connectSrc(value);
	Assertions.assertEquals("connect-src src1 src2", csp.toString());
    }

    @Test
    public void test_connectSrc_CspValueBuilder() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.connectSrc(builder);
	Assertions.assertEquals("connect-src src1 src2", csp.toString());
    }

    @Test
    public void test_defaultSrc() {
	CspBuilder csp = new CspBuilder();
	String value = "src1 src2";
	csp.defaultSrc(value);
	Assertions.assertEquals("default-src src1 src2", csp.toString());
    }

    @Test
    public void test_defaultSrc_CspValueBuilder() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.defaultSrc(builder);
	Assertions.assertEquals("default-src src1 src2", csp.toString());
    }

    @Test
    public void test_fontSrc() {
	CspBuilder csp = new CspBuilder();
	String value = "src1 src2";
	csp.fontSrc(value);
	Assertions.assertEquals("font-src src1 src2", csp.toString());
    }

    @Test
    public void test_fontSrc_CspValueBuilder() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.fontSrc(builder);
	Assertions.assertEquals("font-src src1 src2", csp.toString());
    }

    @Test
    public void test_formAction() {
	CspBuilder csp = new CspBuilder();
	String value = "src1 src2";
	csp.formAction(value);
	Assertions.assertEquals("form-action src1 src2", csp.toString());
    }

    @Test
    public void test_formAction_CspValueBuilder() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.formAction(builder);
	Assertions.assertEquals("form-action src1 src2", csp.toString());
    }

    @Test
    public void test_frameAncestors() {
	CspBuilder csp = new CspBuilder();
	String value = "src1 src2";
	csp.frameAncestors(value);
	Assertions.assertEquals("frame-ancestors src1 src2", csp.toString());
    }

    @Test
    public void test_frameAncestors_CspValueBuilder() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.frameAncestors(builder);
	Assertions.assertEquals("frame-ancestors src1 src2", csp.toString());
    }

    @Test
    public void test_frameSrc() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.frameSrc(builder);
	Assertions.assertEquals("frame-src src1 src2", csp.toString());
    }

    @Test
    public void test_frameSrc_CspValueBuilder() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.frameSrc(builder);
	Assertions.assertEquals("frame-src src1 src2", csp.toString());
    }

    @Test
    public void test_imgSrc() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.imgSrc(builder);
	Assertions.assertEquals("img-src src1 src2", csp.toString());
    }

    @Test
    public void test_imgSrc_CspValueBuilder() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.imgSrc(builder);
	Assertions.assertEquals("img-src src1 src2", csp.toString());
    }

    @Test
    public void test_manifestSrc() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.manifestSrc(builder);
	Assertions.assertEquals("manifest-src src1 src2", csp.toString());
    }

    @Test
    public void test_manifestSrc_CspValueBuilder() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.manifestSrc(builder);
	Assertions.assertEquals("manifest-src src1 src2", csp.toString());
    }

    @Test
    public void test_mediaSrc() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.mediaSrc(builder);
	Assertions.assertEquals("media-src src1 src2", csp.toString());
    }

    @Test
    public void test_mediaSrc_CspValueBuilder() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.mediaSrc(builder);
	Assertions.assertEquals("media-src src1 src2", csp.toString());
    }

    @Test
    public void test_objectSrc() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.objectSrc(builder);
	Assertions.assertEquals("object-src src1 src2", csp.toString());
    }

    @Test
    public void test_objectSrc_CspValueBuilder() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.objectSrc(builder);
	Assertions.assertEquals("object-src src1 src2", csp.toString());
    }

    @Test
    public void test_pluginTypes() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("type1").add("type2");
	csp.pluginTypes(builder);
	Assertions.assertEquals("plugin-types type1 type2", csp.toString());
    }

    @Test
    public void test_pluginTypes_CspValueBuilder() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("type1").add("type2");
	csp.pluginTypes(builder);
	Assertions.assertEquals("plugin-types type1 type2", csp.toString());
    }

    @Test
    public void test_reportTo() {
	CspBuilder csp = new CspBuilder();
	String value = "group1";
	csp.reportTo(value);
	Assertions.assertEquals("report-to group1", csp.toString());
    }

    @Test
    public void test_reportUri() {
	CspBuilder csp = new CspBuilder();
	String value = "uri1";
	csp.reportUri(value);
	Assertions.assertEquals("report-uri uri1", csp.toString());
    }

    @Test
    public void test_requireSriFor() {
	CspBuilder csp = new CspBuilder();
	String value = "script style";
	csp.requireSriFor(value);
	Assertions.assertEquals("require-sri-for script style", csp.toString());
    }

    @Test
    public void test_requireSriFor_CspValueBuilder() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("script").add("style");
	csp.requireSriFor(builder);
	Assertions.assertEquals("require-sri-for script style", csp.toString());
    }

    @Test
    public void test_sandbox() {
	CspBuilder csp = new CspBuilder();
	csp.sandbox((String) null);
	Assertions.assertEquals("sandbox", csp.toString());

	csp = new CspBuilder();
	csp.sandbox("allow-popups");
	Assertions.assertEquals("sandbox allow-popups", csp.toString());
    }

    @Test
    public void test_sandbox_CspValueBuilder() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	csp.sandbox(builder);
	Assertions.assertEquals("sandbox", csp.toString());

	csp = new CspBuilder();
	builder = new CspValueBuilder();
	builder.add("allow-popups");
	csp.sandbox(builder);
	Assertions.assertEquals("sandbox allow-popups", csp.toString());
    }

    @Test
    public void test_scriptSrc() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.scriptSrc(builder);
	Assertions.assertEquals("script-src src1 src2", csp.toString());
    }

    @Test
    public void test_scriptSrc_CspValueBuilder() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.scriptSrc(builder);
	Assertions.assertEquals("script-src src1 src2", csp.toString());
    }

    @Test
    public void test_styleSrc() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.styleSrc(builder);
	Assertions.assertEquals("style-src src1 src2", csp.toString());
    }

    @Test
    public void test_styleSrc_CspValueBuilder() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.styleSrc(builder);
	Assertions.assertEquals("style-src src1 src2", csp.toString());
    }

    @Test
    public void test_upgradeInsecureRequests() {
	CspBuilder csp = new CspBuilder();
	csp.upgradeInsecureRequests(true);
	Assertions.assertEquals("upgrade-insecure-requests", csp.toString());

	csp = new CspBuilder();
	csp.upgradeInsecureRequests(false);
	Assertions.assertNull(csp.toString());
    }

    @Test
    public void test_workerSrc() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.workerSrc(builder);
	Assertions.assertEquals("worker-src src1 src2", csp.toString());
    }

    @Test
    public void test_workerSrc_CspValueBuilder() {
	CspBuilder csp = new CspBuilder();
	CspValueBuilder builder = new CspValueBuilder();
	builder.add("src1").add("src2");
	csp.workerSrc(builder);
	Assertions.assertEquals("worker-src src1 src2", csp.toString());
    }
}
