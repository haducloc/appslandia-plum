// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;

/**
 *
 * @author Loc Ha
 *
 */
public class MockFilterConfig implements FilterConfig {

  private ServletContext servletContext;
  private Map<String, String> initParameters = new HashMap<>();
  private String filterName;

  public MockFilterConfig(ServletContext servletContext, String filterName) {
    this.servletContext = servletContext;
    this.filterName = filterName;
  }

  public Map<String, String> getInitParameters() {
    return initParameters;
  }

  @Override
  public String getFilterName() {
    return filterName;
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
