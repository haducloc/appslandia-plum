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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.appslandia.common.base.CaseInsensitiveSet;
import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.Patterns;
import com.appslandia.common.utils.SplitUtils;
import com.appslandia.common.utils.ValueUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class CorsPolicy extends InitializeObject {

  public static final String ANY = "*";

  private String name;
  private boolean anyOrigin;
  private boolean varyOrigin = true;
  private List<Pattern> allowOrigins;

  private Set<String> allowHeaders;
  private String allowHeadersString;

  private Set<String> exposeHeaders;
  private String exposeHeadersString;

  private boolean allowCredentials;
  private int maxAge;

  @Override
  protected void init() throws Exception {
    Arguments.notNull(this.name);

    // allowOrigins
    Arguments.isTrue(this.anyOrigin || (this.allowOrigins != null), "No allow origin configured.");

    if (this.allowOrigins != null) {
      this.allowOrigins = Collections.unmodifiableList(this.allowOrigins);
    }

    // allowHeaders
    if (this.allowHeaders != null) {
      this.allowHeadersString = String.join(", ", this.allowHeaders);
      this.allowHeaders = Collections.unmodifiableSet(this.allowHeaders);
    }

    // exposeHeaders
    if (this.exposeHeaders != null) {
      this.exposeHeadersString = String.join(", ", this.exposeHeaders);
      this.exposeHeaders = Collections.unmodifiableSet(this.exposeHeaders);
    }
  }

  public boolean allowOrigin(String origin) {
    this.initialize();

    if (this.anyOrigin) {
      return true;
    }
    for (Pattern p : this.allowOrigins) {
      if (p.matcher(origin).matches()) {
        return true;
      }
    }
    return false;
  }

  public boolean allowHeaders(String headers) {
    this.initialize();
    Arguments.notNull(headers);

    if (this.allowHeaders == null) {
      return false;
    }
    for (String header : SplitUtils.splitByComma(headers)) {
      if (!this.allowHeaders.contains(header)) {
        return false;
      }
    }
    return true;
  }

  public String getAllowOrigin(String origin) {
    this.initialize();
    if (this.anyOrigin) {
      return ANY;
    }
    return origin;
  }

  public String getName() {
    this.initialize();
    return this.name;
  }

  public CorsPolicy setName(String name) {
    assertNotInitialized();
    this.name = name;
    return this;
  }

  public boolean isAnyOrigin() {
    this.initialize();
    return this.anyOrigin;
  }

  public CorsPolicy setAnyOrigin() {
    return setAllowOrigins(ANY);
  }

  public List<Pattern> getAllowOrigins() {
    this.initialize();
    return this.allowOrigins;
  }

  public CorsPolicy setAllowOrigins(String... allowOrigins) {
    this.assertNotInitialized();
    if (allowOrigins != null) {
      this.anyOrigin = Arrays.stream(allowOrigins).anyMatch(o -> ANY.equals(o));

      if (this.anyOrigin) {
        this.allowOrigins = null;
      } else {
        this.allowOrigins = CollectionUtils.toList(Patterns.compile(allowOrigins));
      }
    }
    return this;
  }

  public boolean isVaryOrigin() {
    this.initialize();
    return this.varyOrigin;
  }

  public CorsPolicy setVaryOrigin(boolean varyOrigin) {
    this.assertNotInitialized();
    this.varyOrigin = varyOrigin;
    return this;
  }

  public Set<String> getAllowHeaders() {
    this.initialize();
    return this.allowHeaders;
  }

  public CorsPolicy setAllowHeaders(String... allowHeaders) {
    this.assertNotInitialized();
    if (allowHeaders != null) {
      if (Arrays.stream(allowHeaders).anyMatch(o -> ANY.equals(o))) {

        this.allowHeaders = CollectionUtils.toSet(ANY);
      } else {
        this.allowHeaders = CollectionUtils.toSet(new CaseInsensitiveSet(new LinkedHashSet<>()), allowHeaders);
      }
    }
    return this;
  }

  public CorsPolicy setAnyHeader() {
    return setAllowHeaders(ANY);
  }

  public String getAllowHeadersAsString() {
    this.initialize();
    return this.allowHeadersString;
  }

  public Set<String> getExposeHeaders() {
    this.initialize();
    return this.exposeHeaders;
  }

  public CorsPolicy setExposeHeaders(String... exposeHeaders) {
    this.assertNotInitialized();
    if (exposeHeaders != null) {
      this.exposeHeaders = CollectionUtils.toSet(new CaseInsensitiveSet(new LinkedHashSet<>()), exposeHeaders);
    }
    return this;
  }

  public String getExposeHeadersString() {
    this.initialize();
    return this.exposeHeadersString;
  }

  public boolean isAllowCredentials() {
    this.initialize();
    return this.allowCredentials;
  }

  public CorsPolicy setAllowCredentials(boolean allowCredentials) {
    this.assertNotInitialized();
    this.allowCredentials = allowCredentials;
    return this;
  }

  public int getMaxAge() {
    this.initialize();
    return this.maxAge;
  }

  public CorsPolicy setMaxAge(int maxAge, TimeUnit unit) {
    this.assertNotInitialized();
    var ageInSec = TimeUnit.SECONDS.convert(maxAge, unit);
    this.maxAge = ValueUtils.valueOrMin((int) ageInSec, 0);
    return this;
  }
}
