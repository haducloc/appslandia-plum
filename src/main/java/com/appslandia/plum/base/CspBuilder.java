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

/**
 *
 * @author Loc Ha
 *
 */
public class CspBuilder extends HeaderBuilder {

  public CspBuilder() {
    super("; ", ' ');
  }

  public CspBuilder baseUri(CspValueBuilder value) {
    addPair("base-uri", value.toString());
    return this;
  }

  public CspBuilder childSrc(CspValueBuilder value) {
    addPair("child-src", value.toString());
    return this;
  }

  public CspBuilder connectSrc(CspValueBuilder value) {
    addPair("connect-src", value.toString());
    return this;
  }

  public CspBuilder defaultSrc(CspValueBuilder value) {
    addPair("default-src", value.toString());
    return this;
  }

  public CspBuilder fencedFrameSrc(CspValueBuilder value) {
    addPair("fenced-frame-src", value.toString());
    return this;
  }

  public CspBuilder fontSrc(CspValueBuilder value) {
    addPair("font-src", value.toString());
    return this;
  }

  public CspBuilder formAction(CspValueBuilder value) {
    addPair("form-action", value.toString());
    return this;
  }

  public CspBuilder frameAncestors(CspValueBuilder value) {
    addPair("frame-ancestors", value.toString());
    return this;
  }

  public CspBuilder frameSrc(CspValueBuilder value) {
    addPair("frame-src", value.toString());
    return this;
  }

  public CspBuilder imgSrc(CspValueBuilder value) {
    addPair("img-src", value.toString());
    return this;
  }

  public CspBuilder manifestSrc(CspValueBuilder value) {
    addPair("manifest-src", value.toString());
    return this;
  }

  public CspBuilder mediaSrc(CspValueBuilder value) {
    addPair("media-src", value.toString());
    return this;
  }

  public CspBuilder objectSrc(CspValueBuilder value) {
    addPair("object-src", value.toString());
    return this;
  }

  public CspBuilder reportTo(String value) {
    addPair("report-to", value);
    return this;
  }

  public CspBuilder sandbox(CspValueBuilder value) {
    addPair("sandbox", value.toString());
    return this;
  }

  public CspBuilder scriptSrc(CspValueBuilder value) {
    addPair("script-src", value.toString());
    return this;
  }

  public CspBuilder scriptSrcAttr(CspValueBuilder value) {
    addPair("script-src-attr", value.toString());
    return this;
  }

  public CspBuilder scriptSrcElem(CspValueBuilder value) {
    addPair("script-src-elem", value.toString());
    return this;
  }

  public CspBuilder styleSrc(CspValueBuilder value) {
    addPair("style-src", value.toString());
    return this;
  }

  public CspBuilder styleSrcAttr(CspValueBuilder value) {
    addPair("style-src-attr", value.toString());
    return this;
  }

  public CspBuilder styleSrcElem(CspValueBuilder value) {
    addPair("style-src-elem", value.toString());
    return this;
  }

  public CspBuilder upgradeInsecureRequests(boolean value) {
    if (value) {
      addValue("upgrade-insecure-requests");
    }
    return this;
  }

  public CspBuilder workerSrc(CspValueBuilder value) {
    addPair("worker-src", value.toString());
    return this;
  }

  public CspBuilder and(String value) {
    addValue(value);
    return this;
  }
}
