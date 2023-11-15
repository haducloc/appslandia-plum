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

import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.StringUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class CspBuilder extends HeaderBuilder {

  public CspBuilder() {
    super("; ", ' ');
  }

  public CspBuilder baseUri(String value) {
    addPair("base-uri", Asserts.notNull(toDirectiveValue(value)));
    return this;
  }

  public CspBuilder blockAllMixedContent(boolean value) {
    if (value) {
      addValue("block-all-mixed-content");
    }
    return this;
  }

  public CspBuilder formAction(String value) {
    addPair("form-action", Asserts.notNull(toDirectiveValue(value)));
    return this;
  }

  public CspBuilder frameAncestors(String value) {
    addPair("frame-ancestors", Asserts.notNull(toDirectiveValue(value)));
    return this;
  }

  public CspBuilder pluginTypes(String value) {
    addPair("plugin-types", Asserts.notNull(toDirectiveValue(value)));
    return this;
  }

  public CspBuilder requireSriFor(String value) {
    addPair("require-sri-for", Asserts.notNull(toDirectiveValue(value)));
    return this;
  }

  public CspBuilder sandbox(String value) {
    addPair("sandbox", toDirectiveValue(value));
    return this;
  }

  public CspBuilder upgradeInsecureRequests(boolean value) {
    if (value) {
      addValue("upgrade-insecure-requests");
    }
    return this;
  }

  public CspBuilder childSrc(String value) {
    addPair("child-src", Asserts.notNull(toDirectiveValue(value)));
    return this;
  }

  public CspBuilder connectSrc(String value) {
    addPair("connect-src", Asserts.notNull(toDirectiveValue(value)));
    return this;
  }

  public CspBuilder defaultSrc(String value) {
    addPair("default-src", Asserts.notNull(toDirectiveValue(value)));
    return this;
  }

  public CspBuilder fontSrc(String value) {
    addPair("font-src", Asserts.notNull(toDirectiveValue(value)));
    return this;
  }

  public CspBuilder imgSrc(String value) {
    addPair("img-src", Asserts.notNull(toDirectiveValue(value)));
    return this;
  }

  public CspBuilder frameSrc(String value) {
    addPair("frame-src", Asserts.notNull(toDirectiveValue(value)));
    return this;
  }

  public CspBuilder manifestSrc(String value) {
    addPair("manifest-src", Asserts.notNull(toDirectiveValue(value)));
    return this;
  }

  public CspBuilder mediaSrc(String value) {
    addPair("media-src", Asserts.notNull(toDirectiveValue(value)));
    return this;
  }

  public CspBuilder objectSrc(String value) {
    addPair("object-src", Asserts.notNull(toDirectiveValue(value)));
    return this;
  }

  public CspBuilder scriptSrc(String value) {
    addPair("script-src", Asserts.notNull(toDirectiveValue(value)));
    return this;
  }

  public CspBuilder styleSrc(String value) {
    addPair("style-src", Asserts.notNull(toDirectiveValue(value)));
    return this;
  }

  public CspBuilder workerSrc(String value) {
    addPair("worker-src", Asserts.notNull(toDirectiveValue(value)));
    return this;
  }

  // CspValueBuilder

  public CspBuilder baseUri(CspValueBuilder builder) {
    addPair("base-uri", Asserts.notNull(builder.toString()));
    return this;
  }

  public CspBuilder formAction(CspValueBuilder builder) {
    addPair("form-action", Asserts.notNull(builder.toString()));
    return this;
  }

  public CspBuilder frameAncestors(CspValueBuilder builder) {
    addPair("frame-ancestors", Asserts.notNull(builder.toString()));
    return this;
  }

  public CspBuilder pluginTypes(CspValueBuilder builder) {
    addPair("plugin-types", Asserts.notNull(builder.toString()));
    return this;
  }

  public CspBuilder reportUri(String value) {
    addPair("report-uri", Asserts.notNull(StringUtils.trimToNull(value)));
    return this;
  }

  public CspBuilder reportTo(String value) {
    addPair("report-to", Asserts.notNull(StringUtils.trimToNull(value)));
    return this;
  }

  public CspBuilder requireSriFor(CspValueBuilder builder) {
    addPair("require-sri-for", Asserts.notNull(builder.toString()));
    return this;
  }

  public CspBuilder sandbox(CspValueBuilder builder) {
    addPair("sandbox", builder.toString());
    return this;
  }

  public CspBuilder childSrc(CspValueBuilder builder) {
    addPair("child-src", Asserts.notNull(builder.toString()));
    return this;
  }

  public CspBuilder connectSrc(CspValueBuilder builder) {
    addPair("connect-src", Asserts.notNull(builder.toString()));
    return this;
  }

  public CspBuilder defaultSrc(CspValueBuilder builder) {
    addPair("default-src", Asserts.notNull(builder.toString()));
    return this;
  }

  public CspBuilder fontSrc(CspValueBuilder builder) {
    addPair("font-src", Asserts.notNull(builder.toString()));
    return this;
  }

  public CspBuilder imgSrc(CspValueBuilder builder) {
    addPair("img-src", Asserts.notNull(builder.toString()));
    return this;
  }

  public CspBuilder frameSrc(CspValueBuilder builder) {
    addPair("frame-src", Asserts.notNull(builder.toString()));
    return this;
  }

  public CspBuilder manifestSrc(CspValueBuilder builder) {
    addPair("manifest-src", Asserts.notNull(builder.toString()));
    return this;
  }

  public CspBuilder mediaSrc(CspValueBuilder builder) {
    addPair("media-src", Asserts.notNull(builder.toString()));
    return this;
  }

  public CspBuilder objectSrc(CspValueBuilder builder) {
    addPair("object-src", Asserts.notNull(builder.toString()));
    return this;
  }

  public CspBuilder scriptSrc(CspValueBuilder builder) {
    addPair("script-src", Asserts.notNull(builder.toString()));
    return this;
  }

  public CspBuilder styleSrc(CspValueBuilder builder) {
    addPair("style-src", Asserts.notNull(builder.toString()));
    return this;
  }

  public CspBuilder workerSrc(CspValueBuilder builder) {
    addPair("worker-src", Asserts.notNull(builder.toString()));
    return this;
  }

  private String toDirectiveValue(String value) {
    return StringUtils.trimToNull(value);
  }
}
