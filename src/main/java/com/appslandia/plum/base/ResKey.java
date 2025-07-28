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

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 *
 */
public class ResKey implements Serializable {
  private static final long serialVersionUID = 1L;

  private String key;
  private Map<String, Object> params;

  public ResKey(String key) {
    this(key, null);
  }

  public ResKey(String key, Map<String, Object> params) {
    this.key = Arguments.notNull(key);
    this.params = params;
  }

  public String getKey() {
    return this.key;
  }

  public Map<String, Object> getParams() {
    return this.params;
  }

  public String getRes(Resources resources) {
    if (this.params == null) {
      return resources.get(this.key);
    }
    return resources.get(this.key, this.params);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.key);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ResKey that)) {
      return false;
    }
    return Objects.equals(this.key, that.key);
  }
}
