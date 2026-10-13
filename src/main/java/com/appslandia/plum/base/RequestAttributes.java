// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
public class RequestAttributes {

  final Map<String, Object> storedAttributes = new HashMap<>();

  public RequestAttributes(HttpServletRequest request) {
    this(request, (attr) -> true);
  }

  public RequestAttributes(HttpServletRequest request, Predicate<String> storeSelector) {
    var attributes = request.getAttributeNames();
    while (attributes.hasMoreElements()) {
      var attribute = attributes.nextElement();

      if (storeSelector.test(attribute)) {
        storedAttributes.put(attribute, request.getAttribute(attribute));
      }
    }
  }

  public void restore(HttpServletRequest request) {
    var attributes = request.getAttributeNames();
    Set<String> needToRemove = null;

    while (attributes.hasMoreElements()) {
      var attribute = attributes.nextElement();

      if (!storedAttributes.containsKey(attribute)) {
        if (needToRemove == null) {
          needToRemove = new HashSet<>();
        }
        needToRemove.add(attribute);
      }
    }

    if (needToRemove != null) {
      for (String attribute : needToRemove) {
        request.removeAttribute(attribute);
      }
    }

    for (Entry<String, Object> attribute : storedAttributes.entrySet()) {
      request.setAttribute(attribute.getKey(), attribute.getValue());
    }
  }
}
