// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class ResponseHeaders {

  private final Map<String, String[]> storedHeaders = new HashMap<>();

  public ResponseHeaders(HttpServletResponse response, Predicate<String> selector) {
    for (String header : response.getHeaderNames()) {
      if (selector.test(header)) {

        var values = response.getHeaders(header);
        if (!values.isEmpty()) {
          storedHeaders.put(header, values.toArray(new String[values.size()]));
        }
      }
    }
  }

  public void restore(HttpServletResponse response) {
    for (Map.Entry<String, String[]> entry : storedHeaders.entrySet()) {
      var name = entry.getKey();
      var values = entry.getValue();
      response.setHeader(name, values[0]);

      for (var i = 1; i < values.length; i++) {
        response.addHeader(name, values[i]);
      }
    }
  }
}
