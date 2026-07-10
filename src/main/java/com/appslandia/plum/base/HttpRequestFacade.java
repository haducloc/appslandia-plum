// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.ArrayUtils;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ParseUtils;
import com.appslandia.common.utils.STR;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.common.utils.ValueUtils;
import com.appslandia.plum.base.Message.MsgType;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

/**
 *
 * @author Loc Ha
 *
 */
public class HttpRequestFacade extends HttpServletRequestWrapper {

  final Map<String, String[]> mergedParams;

  public HttpRequestFacade(HttpServletRequest request, Map<String, String> pathParamMap) {
    super(request);
    mergedParams = !pathParamMap.isEmpty() ? mergeParameters(request.getParameterMap(), pathParamMap) : null;
  }

  public String getParamOrNull(String name) {
    var value = getParameter(name);
    return StringUtils.trimToNull(value);
  }

  public String getCookieOrNull(String name) {
    var cookie = ServletUtils.getCookie(this, name);
    return (cookie != null) ? StringUtils.trimToNull(cookie.getValue()) : null;
  }

  public boolean isParamTrue(String name) {
    var value = getParameter(name);
    return ParseUtils.isTrueValue(value);
  }

  public boolean isFormAction(String action) {
    var value = getParamOrNull(ServletUtils.PARAM_FORM_ACTION);
    return action.equalsIgnoreCase(value);
  }

  public boolean isGetOrHead() {
    return getRequestContext().isGetOrHead();
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

  public String resolveMessage(Throwable exception) {
    Arguments.notNull(exception);

    // ProblemException
    if (exception instanceof ProblemException pe) {
      var status = ValueUtils.valueOrAlt(pe.getStatus(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

      if (pe.getDetailKey() != null) {
        return pe.getDetailKey().resolve(getResources());
      }
      if (pe.getTitleKey() != null) {
        return getResources().get(pe.getTitleKey());
      }
      return getResources().get(Resources.getMsgKey(status));
    }
    return getResources().get(Resources.getMsgKey(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
  }

  public String resolveMessage(String invalidCode) {
    var msgKey = AuthResult.getResKey(invalidCode);
    return getResources().get(msgKey);
  }

  public String resolveMessage(CredentialValidationResult invalidResult) {
    var msgKey = AuthResult.getResKey(invalidResult);
    return getResources().get(msgKey);
  }

  @Override
  public AsyncContext startAsync() throws IllegalStateException {
    throw new UnsupportedOperationException("Use startAsync(request, response) instead.");
  }

  @Override
  public AsyncContext startAsync(ServletRequest request, ServletResponse response) throws IllegalStateException {
    var actionDesc = Asserts.notNull(getRequestContext().getActionDesc());

    if (actionDesc.getEnableAsync() != null) {
      return super.startAsync(request, response);

    } else {
      if (actionDesc.getEnableEtag() != null) {
        throw new IllegalStateException(
            STR.fmt("@EnableEtag is not supported in manual async mode. Action: {}", actionDesc.getMethod()));
      }

      if (actionDesc.getEnableCompress() != null) {
        throw new IllegalStateException(
            STR.fmt("@EnableCompress is not supported in manual async mode. Action: {}", actionDesc.getMethod()));
      }
      return super.startAsync(request, response);
    }
  }

  public String getModule() {
    return getRequestContext().getModule();
  }

  public AuthPrincipal getPrincipalReq() {
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

  public void addFormError(String errorMessage) {
    getModelState().addError(errorMessage);
  }

  public void addFormError(String errorMessage, boolean escXml) {
    getModelState().addError(errorMessage, escXml);
  }

  public void addFieldError(String fieldPath, String errorMessage) {
    getModelState().addError(fieldPath, errorMessage);
  }

  public void addFieldError(String fieldPath, String errorMessage, boolean escXml) {
    getModelState().addError(fieldPath, errorMessage, escXml);
  }

  public TempData getTempData() {
    return ServletUtils.getTempData(this);
  }

  public PrefCookie getPrefCookie() {
    return getRequestContext().getPrefCookie();
  }

  public List<Message> getMessages() {
    return ServletUtils.getMessages(this);
  }

  public void addInfo(String message) {
    getMessages().add(new Message(MsgType.INFO, message));
  }

  public void addNotice(String message) {
    getMessages().add(new Message(MsgType.NOTICE, message));
  }

  public void addWarn(String message) {
    getMessages().add(new Message(MsgType.WARN, message));
  }

  public void addError(String message) {
    getMessages().add(new Message(MsgType.ERROR, message));
  }

  public Resources getResources() {
    return getRequestContext().getResources();
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

  public String res(ResKey key) {
    return key.resolve(getResources());
  }

  public void assertTrue(boolean expr, String devMessage) throws BadRequestException {
    if (!expr) {
      throw new BadRequestException(devMessage, Resources.ERROR_BAD_REQUEST, null);
    }
  }

  public <T> T assertNotNull(T value, String devMessage) throws BadRequestException {
    if (value == null) {
      throw new BadRequestException(devMessage, Resources.ERROR_BAD_REQUEST, null);
    }
    return value;
  }

  public void assertPositive(int value, String devMessage) throws BadRequestException {
    if (value <= 0) {
      throw new BadRequestException(devMessage, Resources.ERROR_BAD_REQUEST, null);
    }
  }

  public void assertValidFields(String... fieldNames) throws BadRequestException {
    Map<String, List<String>> errorMap = null;

    for (var fieldName : fieldNames) {
      if (!getModelState().isValid(fieldName)) {
        if (errorMap == null) {
          errorMap = new LinkedHashMap<>();
        }

        var fieldErrors = getModelState().getFieldErrors(fieldName).stream().map(Message::getText).toList();
        errorMap.put(fieldName, fieldErrors);
      }
    }

    if (errorMap != null) {
      var devMessage = STR.fmt("Fields are invalid: {}", String.join(", ", errorMap.keySet()));
      throw new BadRequestException(devMessage, Resources.ERROR_BAD_REQUEST, Resources.RES_KEY_FIELDS_INVALID, null,
          errorMap, null);
    }
  }

  public void assertValidModel() throws BadRequestException {
    if (!getModelState().isValid()) {

      var errorMap = getModelState().getErrors().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
          e -> e.getValue().stream().map(Message::getText).toList(), (a, b) -> a, LinkedHashMap::new));

      var devMessage = STR.fmt("Fields are invalid: {}", String.join(", ", errorMap.keySet()));

      throw new BadRequestException(devMessage, Resources.ERROR_BAD_REQUEST, Resources.RES_KEY_FIELDS_INVALID, null,
          errorMap, null);
    }
  }

  public void assertInRoles(String devMessage, String... roles) throws ForbiddenException {
    Arguments.hasElements(roles);

    if (!isUserInRoles(roles)) {
      throw new ForbiddenException(devMessage, Resources.ERROR_FORBIDDEN, null);
    }
  }

  public void assertForbidden(boolean expr, String devMessage) throws ForbiddenException {
    if (!expr) {
      throw new ForbiddenException(devMessage, Resources.ERROR_FORBIDDEN, null);
    }
  }

  public void assertNotFound(boolean expr, String devMessage) throws NotFoundException {
    if (!expr) {
      throw new NotFoundException(devMessage, Resources.ERROR_NOT_FOUND, null);
    }
  }

  @Override
  public AuthPrincipal getUserPrincipal() {
    return ServletUtils.getPrincipal((HttpServletRequest) super.getRequest());
  }

  @Override
  public Part getPart(String name) throws ServletException, IOException {
    var desc = Asserts.notNull(getRequestContext().getActionDesc());
    Asserts.notNull(desc.getEnablePart(), "No @EnablePart annotated.");

    return super.getPart(name);
  }

  @Override
  public Collection<Part> getParts() throws ServletException, IOException {
    var desc = Asserts.notNull(getRequestContext().getActionDesc());
    Asserts.notNull(desc.getEnablePart(), "No @EnablePart annotated.");

    return super.getParts();
  }

  @Override
  public String getParameter(String name) {
    if (mergedParams == null) {
      return super.getParameter(name);
    }
    var values = mergedParams.get(name);
    return (values != null) ? values[0] : null;
  }

  @Override
  public String[] getParameterValues(String name) {
    if (mergedParams == null) {
      return super.getParameterValues(name);
    }
    var values = mergedParams.get(name);
    return (values != null) ? ArrayUtils.copy(values) : null;
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    if (mergedParams == null) {
      return super.getParameterMap();
    }
    return mergedParams;
  }

  @Override
  public Enumeration<String> getParameterNames() {
    if (mergedParams == null) {
      return super.getParameterNames();
    }
    return Collections.enumeration(mergedParams.keySet());
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
