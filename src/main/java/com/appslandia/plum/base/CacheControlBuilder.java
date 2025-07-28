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

import java.util.concurrent.TimeUnit;

import com.appslandia.common.utils.ValueUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class CacheControlBuilder extends HeaderBuilder {

  public CacheControlBuilder usePublic() {
    addValue("public");
    return this;
  }

  public CacheControlBuilder usePrivate() {
    addValue("private");
    return this;
  }

  public CacheControlBuilder noStore() {
    addValue("no-store");
    return this;
  }

  public CacheControlBuilder noCache() {
    addValue("no-cache");
    return this;
  }

  public CacheControlBuilder noTransform() {
    addValue("no-transform");
    return this;
  }

  public CacheControlBuilder maxAge(int maxAge, TimeUnit unit) {
    var ageInSec = TimeUnit.SECONDS.convert(maxAge, unit);
    addPair("max-age", Long.toString(ValueUtils.valueOrMin(ageInSec, 0)));
    return this;
  }

  public CacheControlBuilder sMaxAge(int sMaxAge, TimeUnit unit) {
    var ageInSec = TimeUnit.SECONDS.convert(sMaxAge, unit);
    addPair("s-maxage", Long.toString(ValueUtils.valueOrMin(ageInSec, 0)));
    return this;
  }

  public CacheControlBuilder mustRevalidate() {
    addValue("must-revalidate");
    return this;
  }

  public CacheControlBuilder proxyRevalidate() {
    addValue("proxy-revalidate");
    return this;
  }
}
