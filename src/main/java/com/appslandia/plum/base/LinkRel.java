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

import java.util.Objects;

/**
 *
 * @author Loc Ha
 *
 */
public class LinkRel {

  public static final String HREFLANG_X_DEFAULT = "x-default";

  private String rel;
  private String hreflang;
  private String href;

  public LinkRel() {
  }

  public LinkRel(String rel, String hreflang, String href) {
    this.rel = rel;
    this.hreflang = hreflang;
    this.href = href;
  }

  public String getRel() {
    return this.rel;
  }

  public void setRel(String rel) {
    this.rel = rel;
  }

  public String getHreflang() {
    return this.hreflang;
  }

  public void setHreflang(String hreflang) {
    this.hreflang = hreflang;
  }

  public String getHref() {
    return this.href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  @Override
  public int hashCode() {
    int hash = 1, p = 31;
    hash = p * hash + Objects.hashCode(this.rel);
    hash = p * hash + Objects.hashCode(this.hreflang);
    hash = p * hash + Objects.hashCode(this.href);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof LinkRel)) {
      return false;
    }
    LinkRel that = (LinkRel) obj;
    return Objects.equals(this.rel, that.rel) && Objects.equals(this.hreflang, that.hreflang)
        && Objects.equals(this.href, that.href);
  }
}
