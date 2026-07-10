// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.appslandia.common.base.InitializingObject;
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
public class DynFilterRegistration extends InitializingObject {

  private String filterName;
  private Class<? extends Filter> filterClass;
  private String filterClassName;

  private String[] servletNames;
  private String[] urlPatterns;

  // Default dispatcher types used by both mappings unless overridden below.
  private DispatcherType[] dispatcherTypes;

  // Optional mapping-specific dispatcher types.
  private DispatcherType[] servletDispatcherTypes;
  private DispatcherType[] urlDispatcherTypes;

  private boolean isMatchAfter = true;

  final Map<String, String> initParameters = new HashMap<>();
  private boolean asyncSupported;

  @Override
  protected void init() throws Exception {
    Arguments.notNull(filterName);
    Arguments.isTrue((filterClass != null) || (filterClassName != null), "No filter provided.");
    Arguments.isTrue(ArrayUtils.hasElements(servletNames) || ArrayUtils.hasElements(urlPatterns),
        "No mapping provided.");
  }

  public DynFilterRegistration registerTo(ServletContext sc) {
    initialize();
    var reg = (filterClass != null) ? sc.addFilter(filterName, filterClass) : sc.addFilter(filterName, filterClassName);

    if (ArrayUtils.hasElements(servletNames)) {
      reg.addMappingForServletNames(toTypes(servletDispatcherTypes), isMatchAfter, servletNames);
    }

    if (ArrayUtils.hasElements(urlPatterns)) {
      reg.addMappingForUrlPatterns(toTypes(urlDispatcherTypes), isMatchAfter, urlPatterns);
    }

    reg.setInitParameters(initParameters);
    reg.setAsyncSupported(asyncSupported);
    return this;
  }

  private EnumSet<DispatcherType> toTypes(DispatcherType[] specificTypes) {
    var types = ArrayUtils.hasElements(specificTypes) ? specificTypes : dispatcherTypes;
    return ArrayUtils.hasElements(types) ? EnumSet.copyOf(CollectionUtils.toList(types)) : null;
  }

  public DynFilterRegistration filterName(String filterName) {
    assertNotInitialized();
    this.filterName = StringUtils.trimToNull(filterName);
    return this;
  }

  public DynFilterRegistration filterClassName(String filterClassName) {
    assertNotInitialized();
    this.filterClassName = StringUtils.trimToNull(filterClassName);
    return this;
  }

  public DynFilterRegistration filterClass(Class<? extends Filter> filterClass) {
    assertNotInitialized();
    this.filterClass = filterClass;
    if (filterName == null) {
      filterName = StringUtils.trimToNull(filterClass.getSimpleName());
    }
    return this;
  }

  public DynFilterRegistration servletNames(String... servletNames) {
    assertNotInitialized();
    this.servletNames = servletNames;
    return this;
  }

  public DynFilterRegistration urlPatterns(String... urlPatterns) {
    assertNotInitialized();
    this.urlPatterns = urlPatterns;
    return this;
  }

  /**
   * Default dispatcher types for both servlet-name and URL-pattern mappings. Can be overridden by
   * servletDispatcherTypes(...) or urlDispatcherTypes(...).
   */
  public DynFilterRegistration dispatcherTypes(DispatcherType... dispatcherTypes) {
    assertNotInitialized();
    this.dispatcherTypes = dispatcherTypes;
    return this;
  }

  public DynFilterRegistration servletDispatcherTypes(DispatcherType... dispatcherTypes) {
    assertNotInitialized();
    this.servletDispatcherTypes = dispatcherTypes;
    return this;
  }

  public DynFilterRegistration urlDispatcherTypes(DispatcherType... dispatcherTypes) {
    assertNotInitialized();
    this.urlDispatcherTypes = dispatcherTypes;
    return this;
  }

  public DynFilterRegistration initParameter(String name, String value) {
    assertNotInitialized();
    initParameters.put(name, value);
    return this;
  }

  public DynFilterRegistration isMatchAfter(boolean isMatchAfter) {
    assertNotInitialized();
    this.isMatchAfter = isMatchAfter;
    return this;
  }

  public DynFilterRegistration asyncSupported(boolean asyncSupported) {
    assertNotInitialized();
    this.asyncSupported = asyncSupported;
    return this;
  }
}
