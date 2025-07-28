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

/**
 *
 * @author Loc Ha
 *
 */
public class CspBuilderTest {

  @Test
  public void test_baseUri() {
    var builder = new CspBuilder().baseUri(new CspValueBuilder());
    Assertions.assertEquals("base-uri", builder.toString());
  }

  @Test
  public void test_childSrc() {
    var builder = new CspBuilder().childSrc(new CspValueBuilder());
    Assertions.assertEquals("child-src", builder.toString());
  }

  @Test
  public void test_connectSrc() {
    var builder = new CspBuilder().connectSrc(new CspValueBuilder());
    Assertions.assertEquals("connect-src", builder.toString());
  }

  @Test
  public void test_defaultSrc() {
    var builder = new CspBuilder().defaultSrc(new CspValueBuilder());
    Assertions.assertEquals("default-src", builder.toString());
  }

  @Test
  public void test_fencedFrameSrc() {
    var builder = new CspBuilder().fencedFrameSrc(new CspValueBuilder());
    Assertions.assertEquals("fenced-frame-src", builder.toString());
  }

  @Test
  public void test_fontSrc() {
    var builder = new CspBuilder().fontSrc(new CspValueBuilder());
    Assertions.assertEquals("font-src", builder.toString());
  }

  @Test
  public void test_formAction() {
    var builder = new CspBuilder().formAction(new CspValueBuilder());
    Assertions.assertEquals("form-action", builder.toString());
  }

  @Test
  public void test_frameAncestors() {
    var builder = new CspBuilder().frameAncestors(new CspValueBuilder());
    Assertions.assertEquals("frame-ancestors", builder.toString());
  }

  @Test
  public void test_frameSrc() {
    var builder = new CspBuilder().frameSrc(new CspValueBuilder());
    Assertions.assertEquals("frame-src", builder.toString());
  }

  @Test
  public void test_imgSrc() {
    var builder = new CspBuilder().imgSrc(new CspValueBuilder());
    Assertions.assertEquals("img-src", builder.toString());
  }

  @Test
  public void test_manifestSrc() {
    var builder = new CspBuilder().manifestSrc(new CspValueBuilder());
    Assertions.assertEquals("manifest-src", builder.toString());
  }

  @Test
  public void test_mediaSrc() {
    var builder = new CspBuilder().mediaSrc(new CspValueBuilder());
    Assertions.assertEquals("media-src", builder.toString());
  }

  @Test
  public void test_objectSrc() {
    var builder = new CspBuilder().objectSrc(new CspValueBuilder());
    Assertions.assertEquals("object-src", builder.toString());
  }

  @Test
  public void test_reportTo() {
    var builder = new CspBuilder().reportTo("endpoint");
    Assertions.assertEquals("report-to endpoint", builder.toString());
  }

  @Test
  public void test_sandbox() {
    var builder = new CspBuilder().sandbox(new CspValueBuilder());
    Assertions.assertEquals("sandbox", builder.toString());
  }

  @Test
  public void test_scriptSrc() {
    var builder = new CspBuilder().scriptSrc(new CspValueBuilder());
    Assertions.assertEquals("script-src", builder.toString());
  }

  @Test
  public void test_scriptSrcAttr() {
    var builder = new CspBuilder().scriptSrcAttr(new CspValueBuilder());
    Assertions.assertEquals("script-src-attr", builder.toString());
  }

  @Test
  public void test_scriptSrcElem() {
    var builder = new CspBuilder().scriptSrcElem(new CspValueBuilder());
    Assertions.assertEquals("script-src-elem", builder.toString());
  }

  @Test
  public void test_styleSrc() {
    var builder = new CspBuilder().styleSrc(new CspValueBuilder());
    Assertions.assertEquals("style-src", builder.toString());
  }

  @Test
  public void test_styleSrcAttr() {
    var builder = new CspBuilder().styleSrcAttr(new CspValueBuilder());
    Assertions.assertEquals("style-src-attr", builder.toString());
  }

  @Test
  public void test_styleSrcElem() {
    var builder = new CspBuilder().styleSrcElem(new CspValueBuilder());
    Assertions.assertEquals("style-src-elem", builder.toString());
  }

  @Test
  public void test_upgradeInsecureRequests() {
    var builder = new CspBuilder().upgradeInsecureRequests(true);
    Assertions.assertEquals("upgrade-insecure-requests", builder.toString());
  }

  @Test
  public void test_workerSrc() {
    var builder = new CspBuilder().workerSrc(new CspValueBuilder());
    Assertions.assertEquals("worker-src", builder.toString());
  }

  @Test
  public void test_and() {
    var builder = new CspBuilder().and("value");
    Assertions.assertEquals("value", builder.toString());
  }
}
