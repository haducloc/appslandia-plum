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

package com.appslandia.plum.mocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Loc Ha
 *
 */
public class MockHttpHeader {

  final List<String> values = new ArrayList<>();

  public void addValues(String... values) {
    for (String value : values) {
      if (!this.values.contains(value)) {
        this.values.add(value);
      }
    }
  }

  public void setValues(String... values) {
    this.values.clear();
    addValues(values);
  }

  public List<String> getValues() {
    return this.values;
  }

  public String getValue() {
    return !this.values.isEmpty() ? this.values.get(0) : null;
  }

  public static MockHttpHeader getByName(Map<String, MockHttpHeader> headers, String name) {
    for (String headerName : headers.keySet()) {
      if (headerName.equalsIgnoreCase(name)) {
        return headers.get(headerName);
      }
    }
    return null;
  }
}
