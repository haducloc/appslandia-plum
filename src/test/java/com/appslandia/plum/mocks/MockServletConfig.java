// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;

/**
 *
 * @author Loc Ha
 *
 */
public class MockServletConfig implements ServletConfig {

  private ServletContext servletContext;
  private Map<String, String> initParameters = new HashMap<>();
  private String servletName;

  public MockServletConfig(ServletContext servletContext, String servletName) {
    this.servletContext = servletContext;
    this.servletName = servletName;
  }

  public Map<String, String> getInitParameters() {
    return initParameters;
  }

  @Override
  public String getServletName() {
    return servletName;
  }

  @Override
  public ServletContext getServletContext() {
    return servletContext;
  }

  @Override
  public String getInitParameter(String name) {
    return initParameters.get(name);
  }

  public void setInitParameter(String name, String value) {
    initParameters.put(name, value);
  }

  @Override
  public Enumeration<String> getInitParameterNames() {
    return Collections.enumeration(initParameters.keySet());
  }
}
