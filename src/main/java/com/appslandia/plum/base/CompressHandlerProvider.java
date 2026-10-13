// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.appslandia.common.base.CaseInsensitiveMap;
import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.utils.Arguments;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
public class CompressHandlerProvider extends InitializingObject {

  private Map<String, CompressHandler> handlers = new CaseInsensitiveMap<>();
  private List<String> priorityEncodings;

  @Override
  protected void init() throws Exception {
    priorityEncodings = handlers.values().stream().sorted(Comparator.comparing(CompressHandler::priority))
        .map(h -> h.getType()).toList();

    handlers = Collections.unmodifiableMap(handlers);
  }

  public void registerHandler(CompressHandler impl) {
    assertNotInitialized();

    var existing = this.handlers.get(impl.getType());

    // Replace existing handler if:
    // - none is registered yet, OR
    // - the new handler has higher priority (lower value), OR
    // - the new handler has equal priority (latest registration wins)

    if (existing == null || existing.priority() >= impl.priority()) {
      handlers.put(impl.getType(), impl);
    }
  }

  public CompressHandler getHandler(String type) {
    initialize();
    var impl = handlers.get(type);
    return Arguments.notNull(impl, "No compressHandler is registered for type '{}'.", type);
  }

  public CompressHandler getHandler(HttpServletRequest request) {
    initialize();
    var type = resolveContentEncoding(request, this.priorityEncodings);
    if (type == null) {
      return null;
    }
    return handlers.get(type);
  }

  // Note: This method ignores "identity;q=0" and never returns 406.
  // If no supported encoding is found, the caller should send identity (no encoding).

  static String resolveContentEncoding(HttpServletRequest request, List<String> supportedEncodings) {
    var headers = request.getHeaders("Accept-Encoding");
    if (headers == null || !headers.hasMoreElements()) {
      return null;
    }

    String bestMatch = null;
    var bestQValue = -1.0;
    var bestServerPriority = Integer.MAX_VALUE;
    var wildcardQValue = -1.0;

    while (headers.hasMoreElements()) {
      var headerValue = headers.nextElement();

      for (String entry : headerValue.split(",")) {
        var parts = entry.split(";");
        var encType = parts[0].strip();
        var qValue = 1.0;

        for (var i = 1; i < parts.length; i++) {
          var param = parts[i].strip();
          if (param.regionMatches(true, 0, "q=", 0, 2)) {
            try {
              qValue = Double.parseDouble(param.substring(2));
            } catch (NumberFormatException ignored) {
            }
          }
        }

        if (qValue <= 0.0) {
          continue;
        }

        if ("*".equals(encType)) {
          wildcardQValue = Math.max(wildcardQValue, qValue);
          continue;
        }

        for (var i = 0; i < supportedEncodings.size(); i++) {
          var supported = supportedEncodings.get(i);

          if (!"identity".equalsIgnoreCase(supported) && supported.equalsIgnoreCase(encType)) {
            if (qValue > bestQValue || (qValue == bestQValue && i < bestServerPriority)) {
              bestMatch = supported;
              bestQValue = qValue;
              bestServerPriority = i;
            }
            break;
          }
        }
      }
    }

    if (bestMatch != null && bestQValue >= wildcardQValue) {
      return bestMatch;
    }

    if (wildcardQValue > 0.0) {
      return supportedEncodings.stream().filter(e -> !"identity".equalsIgnoreCase(e)).findFirst().orElse(null);
    }
    return null;
  }
}
