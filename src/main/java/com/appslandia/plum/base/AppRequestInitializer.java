// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Observes;
import jakarta.servlet.ServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class AppRequestInitializer {

  public void requestInitialized(@Observes @Initialized(RequestScoped.class) ServletRequest request) {
    if (request.getCharacterEncoding() == null) {
      try {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

      } catch (UnsupportedEncodingException ex) {
        throw new Error(ex);
      }
    }
  }
}
