// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import java.io.IOException;

import com.appslandia.plum.base.ExceptionHandler;
import com.appslandia.plum.base.Problem;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class MockExceptionHandler extends ExceptionHandler {

  @Override
  public void handleException(HttpServletRequest request, HttpServletResponse response, Throwable exception)
      throws ServletException, IOException {

    try {
      super.handleException(request, response, exception);

    } finally {
      var problem = (Problem) request.getAttribute(Problem.class.getName());
      if (problem != null) {
        response.setStatus(problem.getStatus());
      }
    }
  }
}
