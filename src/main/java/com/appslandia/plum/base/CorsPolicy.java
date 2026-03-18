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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.appslandia.common.base.CaseInsensitiveSet;
import com.appslandia.common.json.JsonIgnore;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.ConfigUtils;
import com.appslandia.common.utils.SplitUtils;
import com.appslandia.common.utils.ValueUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class CorsPolicy extends AntPathPolicy {

  public static final String HEADER_AC_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
  public static final String HEADER_AC_ALLOW_METHODS = "Access-Control-Allow-Methods";
  public static final String HEADER_AC_ALLOW_HEADERS = "Access-Control-Allow-Headers";
  public static final String HEADER_AC_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

  public static final String HEADER_AC_MAX_AGE = "Access-Control-Max-Age";
  public static final String HEADER_AC_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

  public static final String HEADER_ORIGIN = "Origin";
  public static final String HEADER_VARY = "Vary";

  public static final String HEADER_AC_REQUEST_METHOD = "Access-Control-Request-Method";
  public static final String HEADER_AC_REQUEST_HEADERS = "Access-Control-Request-Headers";

  public static final String CORS_ANY = "*";
  public static final Set<String> SET_ANY = CollectionUtils.unmodifiableSet(CORS_ANY);

  @JsonIgnore
  protected List<Pattern> allowPatterns;
  protected Set<String> allowOrigins;

  @JsonIgnore
  protected Set<String> allowHeaders;
  protected String allowHeadersString;

  @JsonIgnore
  protected Set<String> exposeHeaders;
  protected String exposeHeadersString;

  protected boolean allowCredentials;
  protected int maxAge;

  @Override
  protected void init() throws Exception {
    super.init();

    // allowOrigins
    Arguments.notNull(allowOrigins, "allowOrigins are required.");

    if (allowOrigins.contains(CORS_ANY)) {
      allowOrigins = SET_ANY;
    } else {
      allowPatterns = allowOrigins.stream().map(p -> Pattern.compile(p, Pattern.CASE_INSENSITIVE))
          .collect(Collectors.toCollection(ArrayList::new));

      allowPatterns = Collections.unmodifiableList(allowPatterns);
      allowOrigins = Collections.unmodifiableSet(allowOrigins);
    }

    // allowHeaders
    if (allowHeaders != null) {
      if (allowHeaders.contains(CORS_ANY)) {

        allowHeadersString = CORS_ANY;
        allowHeaders = SET_ANY;

      } else {
        allowHeadersString = String.join(", ", allowHeaders);
        allowHeaders = Collections.unmodifiableSet(allowHeaders);
      }
    }

    // exposeHeaders
    if (exposeHeaders != null) {
      exposeHeadersString = String.join(", ", exposeHeaders);
      exposeHeaders = Collections.unmodifiableSet(exposeHeaders);
    }
  }

  public boolean allowOrigin(String origin) {
    initialize();

    if (allowOrigins == SET_ANY) {
      return true;
    }
    for (Pattern p : allowPatterns) {
      if (p.matcher(origin).matches()) {
        return true;
      }
    }
    return false;
  }

  public boolean allowHeaders(String headers) {
    initialize();
    Arguments.notNull(headers);

    if (allowHeaders == null) {
      return false;
    }
    if (allowHeaders == SET_ANY) {
      return true;
    }

    for (String header : SplitUtils.splitByComma(headers)) {
      if (!allowHeaders.contains(header)) {
        return false;
      }
    }
    return true;
  }

  public boolean isAnyOrigin() {
    initialize();
    return allowOrigins == SET_ANY;
  }

  public CorsPolicy setAllowOrigins(String multilineAllowOrigins) {
    assertNotInitialized();

    var origins = ConfigUtils.toMultilineValues(multilineAllowOrigins, ',');
    if (origins.length > 0) {
      allowOrigins = CollectionUtils.toSet(new LinkedHashSet<>(), origins);
    }
    return this;
  }

  public Set<String> getAllowHeaders() {
    initialize();
    return allowHeaders;
  }

  public CorsPolicy setAllowHeaders(String multilineAllowHeaders) {
    assertNotInitialized();

    var headers = ConfigUtils.toMultilineValues(multilineAllowHeaders, ',');
    if (headers.length > 0) {
      allowHeaders = CollectionUtils.toSet(new CaseInsensitiveSet(new LinkedHashSet<>()), headers);
    }
    return this;
  }

  public String getAllowHeadersAsString() {
    initialize();
    return allowHeadersString;
  }

  public Set<String> getExposeHeaders() {
    initialize();
    return exposeHeaders;
  }

  public CorsPolicy setExposeHeaders(String multilineExposeHeaders) {
    assertNotInitialized();

    var headers = ConfigUtils.toMultilineValues(multilineExposeHeaders, ',');
    if (headers.length > 0) {
      exposeHeaders = CollectionUtils.toSet(new CaseInsensitiveSet(new LinkedHashSet<>()), headers);
    }
    return this;
  }

  public String getExposeHeadersString() {
    initialize();
    return exposeHeadersString;
  }

  public boolean isAllowCredentials() {
    initialize();
    return allowCredentials;
  }

  public CorsPolicy setAllowCredentials(boolean allowCredentials) {
    assertNotInitialized();
    this.allowCredentials = allowCredentials;
    return this;
  }

  public int getMaxAge() {
    initialize();
    return maxAge;
  }

  public CorsPolicy setMaxAge(int maxAge, TimeUnit unit) {
    assertNotInitialized();
    var ageInSec = TimeUnit.SECONDS.convert(maxAge, unit);
    this.maxAge = ValueUtils.valueOrMin((int) ageInSec, 0);
    return this;
  }

  @Override
  public CorsPolicy setName(String name) {
    super.setName(name);
    return this;
  }

  @Override
  public CorsPolicy includeAll() {
    super.includeAll();
    return this;
  }

  @Override
  public CorsPolicy setIncludePaths(String multilinePaths) {
    super.setIncludePaths(multilinePaths);
    return this;
  }

  @Override
  public CorsPolicy setExcludePaths(String multilinePaths) {
    super.setExcludePaths(multilinePaths);
    return this;
  }

  public static boolean isCorsResponseHeader(String header) {
    return header.equalsIgnoreCase(HEADER_AC_ALLOW_ORIGIN) || header.equalsIgnoreCase(HEADER_AC_ALLOW_METHODS)
        || header.equalsIgnoreCase(HEADER_AC_ALLOW_HEADERS) || header.equalsIgnoreCase(HEADER_AC_EXPOSE_HEADERS)
        || header.equalsIgnoreCase(HEADER_AC_MAX_AGE) || header.equalsIgnoreCase(HEADER_AC_ALLOW_CREDENTIALS);
  }
}
