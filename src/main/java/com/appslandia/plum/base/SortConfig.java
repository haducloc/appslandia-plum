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
import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 *
 */
public class SortConfig extends InitializeObject {

  private Map<String, Boolean> fields = new HashMap<>();
  private String defBy;

  @Override
  protected void init() throws Exception {
    Arguments.notNull(this.defBy);
    this.fields = Collections.unmodifiableMap(this.fields);
  }

  public SortConfig asc(String... fieldNames) {
    assertNotInitialized();

    for (String fieldName : fieldNames) {
      this.fields.put(fieldName, true);
    }
    return this;
  }

  public SortConfig desc(String... fieldNames) {
    assertNotInitialized();

    for (String fieldName : fieldNames) {
      this.fields.put(fieldName, false);
    }
    return this;
  }

  public SortConfig defBy(String fieldName) {
    assertNotInitialized();

    Arguments.isTrue(this.fields.containsKey(fieldName));
    this.defBy = fieldName;
    return this;
  }

  public String getDefBy() {
    initialize();
    return this.defBy;
  }

  public Map<String, Boolean> getFields() {
    initialize();
    return this.fields;
  }
}
