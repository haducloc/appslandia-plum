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

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.appslandia.common.converters.Converter;
import com.appslandia.common.converters.ConverterException;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.ArrayUtils;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author Loc Ha
 *
 */
public class RequestWrapper extends HttpServletRequestWrapper {

  final Map<String, String[]> mergedParams;

  public RequestWrapper(HttpServletRequest request, Map<String, String> pathParamMap) {
    super(request);
    this.mergedParams = !pathParamMap.isEmpty() ? mergeParameters(request.getParameterMap(), pathParamMap) : null;
  }

  public <T> T getParamOrNull(String name, Class<T> targetType) {
    Converter<T> converter = getRequestContext().getConverterProvider().getConverter(targetType);
    try {
      var value = getParamOrNull(name);
      return converter.parse(value, getRequestContext().getFormatProvider());
    } catch (ConverterException ex) {
      return null;
    }
  }

  public String getParamOrNull(String name) {
    return StringUtils.trimToNull(getParameter(name));
  }

  public <T> T getCookieOrNull(String name, Class<T> targetType) {
    Converter<T> converter = getRequestContext().getConverterProvider().getConverter(targetType);
    try {
      var value = getCookieOrNull(name);
      return converter.parse(value, getRequestContext().getFormatProvider());
    } catch (ConverterException ex) {
      return null;
    }
  }

  public String getCookieOrNull(String name) {
    return StringUtils.trimToNull(ServletUtils.getCookieValue(this, name));
  }

  public boolean getBoolParam(String name) {
    return "true".equals(getParameter(name));
  }

  public boolean isFormAction(String action) {
    return action.equalsIgnoreCase(getParamOrNull(ServletUtils.PARAM_FORM_ACTION));
  }

  public boolean isGetOrHead() {
    return getRequestContext().isGetOrHead();
  }

  public boolean isAjaxRequest() {
    return ServletUtils.isAjaxRequest(this);
  }

  public ZoneId getClientZoneId(ZoneId orZoneId) {
    Arguments.notNull(orZoneId);

    ZoneId zoneId = ServletUtils.getClientZoneId(this);
    return (zoneId != null) ? zoneId : orZoneId;
  }

  public void store(String key, Object value) {
    setAttribute(key, value);
  }

  public void storeModel(Object model) {
    store(ServletUtils.REQUEST_ATTRIBUTE_MODEL, model);
  }

  public void storePagerModel(PagerModel model) {
    store(PagerModel.REQUEST_ATTRIBUTE_ID, model);
  }

  public void storeSortModel(SortModel model) {
    store(SortModel.REQUEST_ATTRIBUTE_ID, model);
  }

  public String getModule() {
    return getRequestContext().getModule();
  }

  public UserPrincipal getPrincipalReq() {
    return Asserts.notNull(getUserPrincipal(), "getUserPrincipal() is required.");
  }

  public boolean isUserInRoles(String... roles) {
    Arguments.hasElements(roles);
    return Arrays.stream(roles).anyMatch(role -> isUserInRole(role));
  }

  public RequestContext getRequestContext() {
    return ServletUtils.getRequestContext(this);
  }

  public ModelState getModelState() {
    return ServletUtils.getModelState(this);
  }

  public TempData getTempData() {
    return ServletUtils.getTempData(this);
  }

  public Messages getMessages() {
    return ServletUtils.getMessages(this);
  }

  public Resources getResources() {
    return getRequestContext().getResources();
  }

  public PrefCookie getPrefCookie() {
    return getRequestContext().getPrefCookie();
  }

  public String res(String key) {
    return getResources().get(key);
  }

  public String res(String key, Object... params) {
    return getResources().get(key, params);
  }

  public String res(String key, Map<String, Object> params) {
    return getResources().get(key, params);
  }

  public void assertTrue(boolean expr) throws BadRequestException {
    if (!expr) {
      throw new BadRequestException(res(Resources.ERROR_BAD_REQUEST)).setTitleKey(Resources.ERROR_BAD_REQUEST);
    }
  }

  public <T> T assertNotNull(T value) throws BadRequestException {
    if (value == null) {
      throw new BadRequestException(res(Resources.ERROR_BAD_REQUEST)).setTitleKey(Resources.ERROR_BAD_REQUEST);
    }
    return value;
  }

  public void assertPositive(int value) throws BadRequestException {
    if (value <= 0) {
      throw new BadRequestException(res(Resources.ERROR_BAD_REQUEST)).setTitleKey(Resources.ERROR_BAD_REQUEST);
    }
  }

  public void assertValidFields(String... fieldNames) throws BadRequestException {
    if (!getModelState().areValid(fieldNames)) {
      throw new BadRequestException(res(Resources.ERROR_BAD_REQUEST)).setTitleKey(Resources.ERROR_BAD_REQUEST);
    }
  }

  public void assertValidModel() throws BadRequestException {
    if (!getModelState().isValid()) {
      throw new BadRequestException(res(Resources.ERROR_BAD_REQUEST)).setTitleKey(Resources.ERROR_BAD_REQUEST);
    }
  }

  public void assertInRoles(String... roles) throws ForbiddenException {
    Arguments.hasElements(roles);

    if (!isUserInRoles(roles)) {
      throw new ForbiddenException(res(Resources.ERROR_FORBIDDEN)).setTitleKey(Resources.ERROR_FORBIDDEN);
    }
  }

  public void assertForbidden(boolean expr) throws ForbiddenException {
    if (!expr) {
      throw new ForbiddenException(res(Resources.ERROR_FORBIDDEN)).setTitleKey(Resources.ERROR_FORBIDDEN);
    }
  }

  public void assertNotFound(boolean expr) throws NotFoundException {
    if (!expr) {
      throw new NotFoundException(res(Resources.ERROR_NOT_FOUND)).setTitleKey(Resources.ERROR_NOT_FOUND);
    }
  }

  @Override
  public String toString() {
    return ObjectUtils.toIdHash(this);
  }

  @Override
  public UserPrincipal getUserPrincipal() {
    return ServletUtils.getPrincipal((HttpServletRequest) super.getRequest());
  }

  @Override
  public String getParameter(String name) {
    if (this.mergedParams == null) {
      return super.getParameter(name);
    }
    var values = this.mergedParams.get(name);
    return (values != null) ? values[0] : null;
  }

  @Override
  public String[] getParameterValues(String name) {
    if (this.mergedParams == null) {
      return super.getParameterValues(name);
    }
    var values = this.mergedParams.get(name);
    return (values != null) ? ArrayUtils.copy(values) : null;
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    if (this.mergedParams == null) {
      return super.getParameterMap();
    }
    return this.mergedParams;
  }

  @Override
  public Enumeration<String> getParameterNames() {
    if (this.mergedParams == null) {
      return super.getParameterNames();
    }
    return Collections.enumeration(this.mergedParams.keySet());
  }

  static Map<String, String[]> mergeParameters(Map<String, String[]> params, Map<String, String> pathParamMap) {
    final Map<String, String[]> merged = new HashMap<>();

    for (Entry<String, String> param : pathParamMap.entrySet()) {
      merged.put(param.getKey(), new String[] { param.getValue() });
    }

    for (Entry<String, String[]> param : params.entrySet()) {
      var values = merged.get(param.getKey());
      if (values == null) {
        merged.put(param.getKey(), param.getValue());
      } else {
        merged.put(param.getKey(), ArrayUtils.append(values, param.getValue()));
      }
    }
    return Collections.unmodifiableMap(merged);
  }
}
