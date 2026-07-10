// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.StringUtils;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;

/**
 *
 * @author Loc Ha
 *
 */
public class DynServletRegistration extends InitializingObject {

  private String servletName;
  private Class<? extends Servlet> servletClass;
  private String servletClassName;

  private String[] urlPatterns;
  final Map<String, String> initParameters = new HashMap<>();

  private boolean asyncSupported;
  private int loadOnStartup = -1;

  private DynMultipartConfig multipartConfig;

  @Override
  protected void init() throws Exception {
    Arguments.notNull(servletName);
    Arguments.isTrue((servletClass != null) || (servletClassName != null), "No servlet provided.");
    Arguments.hasElements(urlPatterns, "urlPatterns is required.");
  }

  public DynServletRegistration registerTo(ServletContext sc) {
    initialize();

    var reg = (servletClass != null) ? sc.addServlet(servletName, servletClass)
        : sc.addServlet(servletName, servletClassName);
    reg.addMapping(urlPatterns);
    reg.setInitParameters(initParameters);

    reg.setAsyncSupported(asyncSupported);
    reg.setLoadOnStartup(loadOnStartup);

    if (multipartConfig != null) {
      reg.setMultipartConfig(multipartConfig.toMultipartConfigElement());
    }
    return this;
  }

  public DynServletRegistration servletName(String servletName) {
    assertNotInitialized();
    this.servletName = StringUtils.trimToNull(servletName);
    return this;
  }

  public DynServletRegistration servletClassName(String servletClassName) {
    assertNotInitialized();
    this.servletClassName = StringUtils.trimToNull(servletClassName);
    return this;
  }

  public DynServletRegistration servletClass(Class<? extends Servlet> servletClass) {
    assertNotInitialized();
    this.servletClass = servletClass;
    if (servletName == null) {
      servletName = StringUtils.trimToNull(servletClass.getSimpleName());
    }
    return this;
  }

  public DynServletRegistration urlPatterns(String... urlPatterns) {
    assertNotInitialized();
    this.urlPatterns = urlPatterns;
    return this;
  }

  public DynServletRegistration initParameter(String name, String value) {
    assertNotInitialized();
    initParameters.put(name, value);
    return this;
  }

  public DynServletRegistration asyncSupported(boolean asyncSupported) {
    assertNotInitialized();
    this.asyncSupported = asyncSupported;
    return this;
  }

  public DynServletRegistration loadOnStartup(int loadOnStartup) {
    assertNotInitialized();
    this.loadOnStartup = loadOnStartup;
    return this;
  }

  public DynServletRegistration multipartConfig(DynMultipartConfig multipartConfig) {
    assertNotInitialized();
    this.multipartConfig = multipartConfig;
    return this;
  }
}
