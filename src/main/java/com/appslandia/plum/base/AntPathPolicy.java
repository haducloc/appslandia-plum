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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.utils.AntPathMatcher;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.ConfigUtils;
import com.appslandia.common.utils.ObjectUtils;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class AntPathPolicy extends InitializingObject {

  public static final String PATH_ANY = "/**";
  public static final Set<String> SET_ANY = CollectionUtils.unmodifiableSet(PATH_ANY);

  protected String name;
  protected Set<String> includePaths;
  protected Set<String> excludePaths;
  protected final ConcurrentMap<String, Boolean> matchCache = new ConcurrentHashMap<>();

  @Override
  protected void init() throws Exception {
    Arguments.notNull(includePaths, "includePaths are required.");

    if (includePaths.contains(PATH_ANY)) {
      includePaths = SET_ANY;
    } else {
      includePaths = Collections.unmodifiableSet(includePaths);
    }

    if (excludePaths != null) {
      if (excludePaths.contains(PATH_ANY)) {
        excludePaths = SET_ANY;
      } else {
        excludePaths = Collections.unmodifiableSet(excludePaths);
      }
    }
  }

  public boolean matches(String path) {
    initialize();

    return matchCache.computeIfAbsent(path, p -> {
      var matcher = AntPathMatcher.DEFAULT;

      for (String inclPath : includePaths) {
        if (matcher.match(inclPath, p)) {

          if (excludePaths == null) {
            return true;
          }
          return !excludePaths.stream().anyMatch(excl -> matcher.match(excl, p));
        }
      }
      return false;
    });
  }

  public boolean isAllMatched() {
    initialize();
    return includePaths == SET_ANY;
  }

  public String getName() {
    initialize();
    return name;
  }

  public AntPathPolicy setName(String name) {
    assertNotInitialized();
    this.name = name;
    return this;
  }

  public AntPathPolicy includeAll() {
    return setIncludePaths(PATH_ANY);
  }

  public Set<String> getIncludePaths() {
    initialize();
    return includePaths;
  }

  public AntPathPolicy setIncludePaths(String multilinePaths) {
    assertNotInitialized();

    var paths = ConfigUtils.toMultilineValues(multilinePaths, ',');
    if (paths.length > 0) {
      includePaths = CollectionUtils.toSet(new LinkedHashSet<>(), paths);
    }
    return this;
  }

  public Set<String> getExcludePaths() {
    initialize();
    return excludePaths;
  }

  public AntPathPolicy setExcludePaths(String multilinePaths) {
    assertNotInitialized();

    var paths = ConfigUtils.toMultilineValues(multilinePaths, ',');
    if (paths.length > 0) {
      excludePaths = CollectionUtils.toSet(new LinkedHashSet<>(), paths);
    }
    return this;
  }

  @Override
  public String toString() {
    return ObjectUtils.toIdHash(this) + "(name=" + name + ")";
  }
}
