// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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
import java.util.Map;

import com.appslandia.common.converters.Converter;
import com.appslandia.common.converters.ConverterException;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class RequestAccessor extends HttpServletRequestWrapper {

    public static final String PARAM_FORM_ACTION = "formAction";

    public RequestAccessor(HttpServletRequest request) {
	super(request);
    }

    public <T> T getParamOrNull(String name, Class<T> targetType) {
	String value = getParamOrNull(name);
	Converter<T> converter = getRequestContext().getConverterProvider().getConverter(targetType);
	Asserts.notNull(converter);

	try {
	    return converter.parse(value, getRequestContext().getFormatProvider());
	} catch (ConverterException ex) {
	    return null;
	}
    }

    public String getParamOrNull(String name) {
	return StringUtils.trimToNull(getParameter(name));
    }

    public boolean isFormAction(String action) {
	return action.equalsIgnoreCase(getParamOrNull(PARAM_FORM_ACTION));
    }

    public boolean isGetOrHead() {
	return getRequestContext().isGetOrHead();
    }

    public boolean isAjaxRequest() {
	return ServletUtils.isAjaxRequest(this);
    }

    public ZoneId getClientZone(ZoneId orZone) {
	ZoneId zone = ServletUtils.getClientZone(this);
	if (zone != null) {
	    return zone;
	}
	return (orZone != null) ? orZone : ZoneId.systemDefault();
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

    public boolean isModuleAuthenticated() {
	return (getUserPrincipal() != null) && getUserPrincipal().getModule().equalsIgnoreCase(getRequestContext().getModule());
    }

    @Override
    public UserPrincipal getUserPrincipal() {
	return ServletUtils.getUserPrincipal((HttpServletRequest) super.getRequest());
    }

    public UserPrincipal getRequiredPrincipal() {
	return Asserts.notNull(getUserPrincipal(), "getUserPrincipal() is required.");
    }

    public boolean isUserInRoles(String... roles) {
	Asserts.hasElements(roles);
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
	return ServletUtils.getPrefCookie(this);
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

    public void assertInRoles(String[] roles) throws ForbiddenException {
	Asserts.hasElements(roles);

	if (!isUserInRoles(roles)) {
	    throw new ForbiddenException(res(Resources.ERROR_FORBIDDEN)).setTitleKey(Resources.ERROR_FORBIDDEN);
	}
    }

    public void assertForbidden(boolean expr) throws ForbiddenException {
	if (!expr) {
	    throw new ForbiddenException(res(Resources.ERROR_FORBIDDEN)).setTitleKey(Resources.ERROR_FORBIDDEN);
	}
    }

    public void assertNotFound(boolean expr) throws ForbiddenException {
	if (!expr) {
	    throw new NotFoundException(res(Resources.ERROR_NOT_FOUND)).setTitleKey(Resources.ERROR_NOT_FOUND);
	}
    }

    @Override
    public String toString() {
	return "[" + getClass().getSimpleName() + "] " + getRequest();
    }
}
