// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
    return values;
  }

  public String getValue() {
    return !values.isEmpty() ? values.get(0) : null;
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
