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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.base.InitializeObject;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.ArrayUtils;
import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.StringUtils;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletContext;

/**
 *
 * @author Loc Ha
 *
 */
public class DynFilterRegister extends InitializeObject {

  private String filterName;
  private Class<? extends Filter> filterClass;
  private String filterClassName;

  private String[] servletNames;
  private String[] urlPatterns;
  private DispatcherType[] dispatcherTypes;
  private boolean isMatchAfter = true;

  final Map<String, String> initParameters = new HashMap<>();
  private boolean asyncSupported;

  @Override
  protected void init() throws Exception {
    Arguments.notNull(this.filterName);
    Arguments.isTrue((this.filterClass != null) || (this.filterClassName != null), "No filter provided.");
    Arguments.isTrue(ArrayUtils.hasElements(this.servletNames) || ArrayUtils.hasElements(this.urlPatterns),
        "No mapping provided.");
  }

  public DynFilterRegister registerTo(ServletContext sc) {
    initialize();
    var reg = (this.filterClass != null) ? sc.addFilter(this.filterName, this.filterClass)
        : sc.addFilter(this.filterName, this.filterClassName);

    var types = ArrayUtils.hasElements(this.dispatcherTypes)
        ? EnumSet.copyOf(CollectionUtils.toList(this.dispatcherTypes))
        : null;

    if (ArrayUtils.hasElements(this.servletNames)) {
      reg.addMappingForServletNames(types, this.isMatchAfter, this.servletNames);
    }

    if (ArrayUtils.hasElements(this.urlPatterns)) {
      reg.addMappingForUrlPatterns(types, this.isMatchAfter, this.urlPatterns);
    }

    reg.setInitParameters(this.initParameters);
    reg.setAsyncSupported(this.asyncSupported);
    return this;
  }

  public DynFilterRegister filterName(String filterName) {
    assertNotInitialized();
    this.filterName = StringUtils.trimToNull(filterName);
    return this;
  }

  public DynFilterRegister filterClassName(String filterClassName) {
    assertNotInitialized();
    this.filterClassName = StringUtils.trimToNull(filterClassName);
    return this;
  }

  public DynFilterRegister filterClass(Class<? extends Filter> filterClass) {
    assertNotInitialized();
    this.filterClass = filterClass;
    if (this.filterName == null) {
      this.filterName = StringUtils.trimToNull(filterClass.getSimpleName());
    }
    return this;
  }

  public DynFilterRegister servletNames(String... servletNames) {
    assertNotInitialized();
    this.servletNames = servletNames;
    return this;
  }

  public DynFilterRegister urlPatterns(String... urlPatterns) {
    assertNotInitialized();
    this.urlPatterns = urlPatterns;
    return this;
  }

  public DynFilterRegister dispatcherTypes(DispatcherType... dispatcherTypes) {
    assertNotInitialized();
    this.dispatcherTypes = dispatcherTypes;
    return this;
  }

  public DynFilterRegister initParameter(String name, String value) {
    assertNotInitialized();
    this.initParameters.put(name, value);
    return this;
  }

  public DynFilterRegister isMatchAfter(boolean isMatchAfter) {
    assertNotInitialized();
    this.isMatchAfter = isMatchAfter;
    return this;
  }

  public DynFilterRegister asyncSupported(boolean asyncSupported) {
    assertNotInitialized();
    this.asyncSupported = asyncSupported;
    return this;
  }
}
