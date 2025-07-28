// The MIT License (MIT)
// Copyright © 2015 Loc Ha

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.appslandia.plum.base;

import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.StringUtils;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;

/**
 *
 * @author Loc Ha
 *
 */
public class DynServletRegister extends InitializeObject {

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
    Arguments.notNull(this.servletName);
    Arguments.isTrue((this.servletClass != null) || (this.servletClassName != null), "No servlet provided.");
    Arguments.hasElements(this.urlPatterns, "urlPatterns is required.");
  }

  public DynServletRegister registerTo(ServletContext sc) {
    initialize();

    var reg = (this.servletClass != null) ? sc.addServlet(this.servletName, this.servletClass)
        : sc.addServlet(this.servletName, this.servletClassName);
    reg.addMapping(this.urlPatterns);
    reg.setInitParameters(this.initParameters);

    reg.setAsyncSupported(this.asyncSupported);
    reg.setLoadOnStartup(this.loadOnStartup);

    if (this.multipartConfig != null) {
      reg.setMultipartConfig(this.multipartConfig.toMultipartConfigElement());
    }
    return this;
  }

  public DynServletRegister servletName(String servletName) {
    assertNotInitialized();
    this.servletName = StringUtils.trimToNull(servletName);
    return this;
  }

  public DynServletRegister servletClassName(String servletClassName) {
    assertNotInitialized();
    this.servletClassName = StringUtils.trimToNull(servletClassName);
    return this;
  }

  public DynServletRegister servletClass(Class<? extends Servlet> servletClass) {
    assertNotInitialized();
    this.servletClass = servletClass;
    if (this.servletName == null) {
      this.servletName = StringUtils.trimToNull(servletClass.getSimpleName());
    }
    return this;
  }

  public DynServletRegister urlPatterns(String... urlPatterns) {
    assertNotInitialized();
    this.urlPatterns = urlPatterns;
    return this;
  }

  public DynServletRegister initParameter(String name, String value) {
    assertNotInitialized();
    this.initParameters.put(name, value);
    return this;
  }

  public DynServletRegister asyncSupported(boolean asyncSupported) {
    assertNotInitialized();
    this.asyncSupported = asyncSupported;
    return this;
  }

  public DynServletRegister loadOnStartup(int loadOnStartup) {
    assertNotInitialized();
    this.loadOnStartup = loadOnStartup;
    return this;
  }

  public DynServletRegister multipartConfig(DynMultipartConfig multipartConfig) {
    assertNotInitialized();
    this.multipartConfig = multipartConfig;
    return this;
  }
}
