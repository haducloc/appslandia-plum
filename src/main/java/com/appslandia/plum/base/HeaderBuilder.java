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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.appslandia.common.base.InitializeObject;

/**
 *
 * @author Loc Ha
 *
 */
public class HeaderBuilder extends InitializeObject {

  protected final Map<String, String> entries = new LinkedHashMap<>();

  final String entrySep;
  final char kvSep;

  public HeaderBuilder() {
    this(", ");
  }

  public HeaderBuilder(String entrySep) {
    this(entrySep, '=');
  }

  public HeaderBuilder(String entrySep, char kvSep) {
    this.entrySep = entrySep;
    this.kvSep = kvSep;
  }

  @Override
  protected void init() throws Exception {
  }

  public HeaderBuilder addValue(String key) {
    assertNotInitialized();
    this.entries.put(key, null);
    return this;
  }

  public HeaderBuilder addValues(String... keys) {
    assertNotInitialized();
    for (String key : keys) {
      this.entries.put(key, null);
    }
    return this;
  }

  public HeaderBuilder addPair(String key, String value) {
    assertNotInitialized();
    this.entries.put(key, value);
    return this;
  }

  @Override
  public String toString() {
    initialize();
    var sb = new StringBuilder();
    for (Entry<String, String> pair : this.entries.entrySet()) {
      if (sb.length() > 0) {
        sb.append(this.entrySep);
      }
      sb.append(pair.getKey());
      if (pair.getValue() != null) {
        sb.append(this.kvSep).append(pair.getValue());
      }
    }
    return sb.length() > 0 ? sb.toString() : null;
  }
}
