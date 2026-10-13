// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class FinishableResponseWrapper extends HttpServletResponseWrapper {

  public FinishableResponseWrapper(HttpServletResponse response) {
    super(response);
  }

  public abstract void finishResponse() throws IOException;
}
