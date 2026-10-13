// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class MockRequestDispatcher implements RequestDispatcher {

  private String path;

  public MockRequestDispatcher(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }

  @Override
  public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
  }

  @Override
  public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
  }
}
