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

package com.appslandia.plum.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.appslandia.common.base.FormatProvider;
import com.appslandia.common.base.SimpleConfig;
import com.appslandia.common.cdi.BeanInstance;
import com.appslandia.common.crypto.DigesterImpl;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.HexUtils;
import com.appslandia.common.utils.IOUtils;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.ParseUtils;
import com.appslandia.common.utils.STR;
import com.appslandia.common.utils.SplitUtils;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.common.utils.URLEncoding;
import com.appslandia.common.utils.ValueUtils;
import com.appslandia.plum.base.ActionDescProvider;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.AppContextInitializer;
import com.appslandia.plum.base.AppSessionInitializer;
import com.appslandia.plum.base.BadRequestException;
import com.appslandia.plum.base.HttpException;
import com.appslandia.plum.base.InstanceKey;
import com.appslandia.plum.base.LanguageProvider;
import com.appslandia.plum.base.Messages;
import com.appslandia.plum.base.ModelState;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.base.Resources;
import com.appslandia.plum.base.TempData;
import com.appslandia.plum.base.UserPrincipal;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Loc Ha
 *
 */
public class ServletUtils {

  public static final String ACTION_INDEX = "index";
  public static final String REQUEST_ATTRIBUTE_MODEL = "model";
  public static final String COOKIE_CLIENT_ZONE_ID = "__client_zone_id";

  public static final String PARAM_RETURN_URL = "__return_url";
  public static final String PARAM_FORM_FIELD = "__form";
  public static final String PARAM_FORM_ACTION = "__form_action";
  public static final String PARAM_REAUTHENTICATION = "__reauthentication";

  public static StringBuilder newUrlBuilder() {
    return new StringBuilder(255);
  }

  public static String getUriQuery(HttpServletRequest request) {
    return appendUriQuery(request, newUrlBuilder()).toString();
  }

  public static StringBuilder appendUriQuery(HttpServletRequest request, StringBuilder sb) {
    sb.append(request.getRequestURI());

    if (request.getQueryString() != null) {
      sb.append('?').append(request.getQueryString());
    }
    return sb;
  }

  public static StringBuilder getLoginUrl(HttpServletRequest request) {
    var languageProvider = getAppScoped(request, LanguageProvider.class);
    var requestContext = getRequestContext(request);

    // URL
    var url = newUrlBuilder();
    url.append(request.getContextPath());

    // Language
    if (languageProvider.isMultiLanguages()) {
      url.append('/').append(requestContext.getLanguageId());
    }

    var actionDescProvider = getAppScoped(request.getServletContext(), ActionDescProvider.class);
    var formLogin = actionDescProvider.getFormLogin(requestContext.getModule());
    Asserts.notNull(formLogin, "No @FormLogin is registered for module '{}'.", requestContext.getModule());

    url.append('/').append(formLogin.getController());
    url.append('/').append(formLogin.getAction());
    return url;
  }

  public static String getScheme(HttpServletRequest request) {
    return ValueUtils.valueOrAlt(request.getHeader("X-Forwarded-Proto"), request.getScheme());
  }

  public static String getHost(HttpServletRequest request) {
    return ValueUtils.valueOrAlt(request.getHeader("X-Forwarded-Host"), request.getServerName());
  }

  static final Pattern X_FORWARDED_PORTS_PATTERN = Pattern.compile("\\s*\\d+\\s*,\\s*\\d+\\s*");

  public static String getPort(HttpServletRequest request) {
    // X-Forwarded-Port
    var port = request.getHeader("X-Forwarded-Port");
    if (port != null) {
      return port;
    }

    // config.x_forwarded_ports
    var appConfig = getAppScoped(request.getServletContext(), AppConfig.class);
    var httpPorts = appConfig.getString(AppConfig.CONFIG_X_FORWARDED_PORTS);
    if (httpPorts != null) {

      if (!X_FORWARDED_PORTS_PATTERN.matcher(httpPorts).matches()) {
        throw new IllegalStateException(STR.fmt("X-Forwarded-Ports '{}' is invalid.", httpPorts));
      }
      var ports = SplitUtils.splitByComma(httpPorts);

      var scheme = getScheme(request);
      return "https".equals(scheme) ? ports[1] : ports[0];

    } else {

      // request.getServerPort()
      return Integer.toString(request.getServerPort());
    }
  }

  public static StringBuilder absHostBase(HttpServletRequest request) {
    var scheme = getScheme(request);
    var host = getHost(request);
    var port = getPort(request);

    var url = newUrlBuilder();
    url.append(scheme).append("://").append(host);

    if ("https".equals(scheme)) {
      if (!"443".equals(port)) {
        url.append(':').append(port);
      }
    } else {
      if (!"80".equals(port)) {
        url.append(':').append(port);
      }
    }
    return url;
  }

  public static String getRequestUrl(HttpServletRequest request) {
    var url = newUrlBuilder();

    // URI & QueryString
    appendUriQuery(request, url);
    return url.toString();
  }

  /**
   * Returns the request URL with the embedded path language removed if it exists and pathLang is null.
   *
   */
  public static String getRequestUrl(HttpServletRequest request, String pathLang) {
    var url = newUrlBuilder();

    // ContextPath
    url.append(request.getContextPath());

    // Language
    if (pathLang != null) {
      url.append('/').append(pathLang);
    }

    // ServletPath
    var requestContext = getRequestContext(request);
    if (requestContext.isPathLanguage()) {
      url.append(request.getServletPath().substring(requestContext.getLanguageId().length() + 1));
    } else {
      url.append(request.getServletPath());
    }

    // PathInfo
    if (request.getPathInfo() != null) {
      url.append(request.getPathInfo());
    }

    // QueryString
    if (request.getQueryString() != null) {
      url.append('?').append(request.getQueryString());
    }
    return url.toString();
  }

  public static String getClientIp(HttpServletRequest request) {
    var value = request.getHeader("X-Forwarded-For");
    if (value == null) {
      return request.getRemoteAddr();
    }
    var idx = value.indexOf(',');
    if (idx < 0) {
      return request.getRemoteAddr();
    }
    value = value.substring(0, idx).strip();
    return !value.isEmpty() ? value : request.getRemoteAddr();
  }

  public static String getUserAgent(HttpServletRequest request) {
    return request.getHeader("User-Agent");
  }

  public static String getAppDir(ServletContext sc) {
    var appDir = sc.getRealPath("/");
    Asserts.notNull(appDir, "Couldn't determine appDir.");
    return appDir;
  }

  public static boolean isAjaxRequest(HttpServletRequest request) {
    var xrw = request.getHeader("X-Requested-With");
    return "XMLHttpRequest".equalsIgnoreCase(xrw);
  }

  public static void setContentDisposition(HttpServletResponse response, String fileName, boolean inline) {
    var dispositionType = inline ? "inline" : "attachment";
    response.setHeader("Content-Disposition",
        dispositionType + "; filename=\"" + URLEncoding.encodePath(fileName) + "\"");
  }

  public static boolean isContentSupported(String checkingContentType, String supportedType) {
    if (checkingContentType == null) {
      return false;
    }
    var idx = checkingContentType.indexOf(';');
    var mimeType = (idx < 0) ? checkingContentType : checkingContentType.substring(0, idx);
    return supportedType.equalsIgnoreCase(mimeType);
  }

  public static String getBestLanguage(HttpServletRequest request, Collection<String> supportedLanguages) {
    var requestLocales = request.getLocales();
    while (requestLocales.hasMoreElements()) {
      var requestLocale = requestLocales.nextElement();

      var matchedLang = supportedLanguages.stream()
          .filter(sl -> Locale.of(sl).getLanguage().equals(requestLocale.getLanguage())).findFirst().orElse(null);
      if (matchedLang != null) {
        return matchedLang;
      }
    }
    return null;
  }

  public static String getBestEncoding(HttpServletRequest request, Collection<String> supportedEncodings) {
    var acceptEncoding = request.getHeader("Accept-Encoding");
    if (acceptEncoding == null || acceptEncoding.isEmpty()) {
      return null;
    }

    String bestEncType = null;
    var bestQValue = 0.0;

    var encodings = acceptEncoding.split(",");
    for (String encoding : encodings) {

      var parts = encoding.split(";");
      var encType = parts[0].strip();
      var qValue = 1.0;

      if (parts.length > 1 && parts[1].strip().startsWith("q=")) {
        try {
          qValue = Double.parseDouble(parts[1].strip().substring(2));
        } catch (NumberFormatException ex) {
        }
      }

      // identity & *
      if (("identity".equalsIgnoreCase(encType) || "*".equals(encType)) && (qValue > bestQValue)) {
        bestEncType = encType;
        bestQValue = qValue;
      }

      // supportedEncodings
      for (String type : supportedEncodings) {
        if (type.equals(encType) && qValue > bestQValue) {

          bestEncType = encType;
          bestQValue = qValue;
        }
      }
    }

    if ("identity".equalsIgnoreCase(bestEncType) || "*".equals(bestEncType)) {
      bestEncType = null;
    }
    return bestEncType;
  }

  public static Object getMutex(HttpSession session) {
    var mutex = session.getAttribute(AppSessionInitializer.ATTRIBUTE_MUTEX);
    return (mutex != null) ? mutex : session;
  }

  public static Object getMutex(ServletContext context) {
    var mutex = context.getAttribute(AppContextInitializer.ATTRIBUTE_MUTEX);
    return (mutex != null) ? mutex : context;
  }

  public static HttpServletResponse assertNotCommitted(HttpServletResponse response) throws IllegalStateException {
    if (response.isCommitted()) {
      throw new IllegalStateException("The response is already committed.");
    }
    return response;
  }

  public static RequestDispatcher getRequestDispatcher(HttpServletRequest request, String path) {
    var dispatcher = request.getRequestDispatcher(path);
    return Arguments.notNull(dispatcher, "Couldn't obtain a dispatcher for the path '{}'.", path);
  }

  public static void forward(HttpServletRequest request, HttpServletResponse response, String path)
      throws ServletException, IOException {
    getRequestDispatcher(request, path).forward(request, assertNotCommitted(response));
  }

  public static void include(HttpServletRequest request, HttpServletResponse response, String path)
      throws ServletException, IOException {
    getRequestDispatcher(request, path).include(request, response);
  }

  public static void sendRedirect(HttpServletResponse response, String location) throws IllegalStateException {
    sendRedirect(response, location, HttpServletResponse.SC_MOVED_TEMPORARILY);
  }

  public static void sendRedirect(HttpServletResponse response, String location, int status)
      throws IllegalStateException {
    assertNotCommitted(response);

    response.resetBuffer();
    response.setHeader("Location", location);
    response.setStatus(status);
  }

  public static void testErrorStatus(HttpServletRequest request, String parameterName) {
    var statusValue = request.getParameter(parameterName);
    if (statusValue == null) {
      return;
    }

    // 4XX, 5XX
    var status = ParseUtils.parseInt(statusValue, 0);
    if (status >= 400 && status < 600) {

      throw new HttpException(status, parameterName + "=" + status);
    } else {
      throw new BadRequestException(parameterName + " is invalid.");
    }
  }

  public static void testOutStream(HttpServletRequest request, HttpServletResponse response, String parameterName)
      throws IOException {
    var streamType = request.getParameter(parameterName);
    if (streamType == null) {
      return;
    }
    if ("writer".equals(streamType)) {
      response.getWriter();

    } else if ("stream".equals(streamType)) {
      response.getOutputStream();
    }
  }

  public static String toWrapperPath(HttpServletRequest request) {
    var sb = new StringBuilder();
    var req = request;
    while (true) {
      if (sb.length() == 0) {
        sb.append(ObjectUtils.toIdHash(req));
      } else {
        sb.append(" > ").append(ObjectUtils.toIdHash(req));
      }
      if (!(req instanceof HttpServletRequestWrapper)) {
        return sb.toString();
      }
      req = (HttpServletRequest) ((HttpServletRequestWrapper) req).getRequest();
    }
  }

  public static String toWrapperPath(HttpServletResponse response) {
    var sb = new StringBuilder();
    var resp = response;
    while (true) {
      if (sb.length() == 0) {
        sb.append(ObjectUtils.toIdHash(resp));
      } else {
        sb.append(" > ").append(ObjectUtils.toIdHash(resp));
      }
      if (!(resp instanceof HttpServletResponseWrapper)) {
        return sb.toString();
      }
      resp = (HttpServletResponse) ((HttpServletResponseWrapper) resp).getResponse();
    }
  }

  public static String getCookieValue(HttpServletRequest request, String name) {
    var cookies = request.getCookies();
    if (cookies == null) {
      return null;
    }
    var cookie = Arrays.stream(cookies).filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    return (cookie != null) ? cookie.getValue() : null;
  }

  public static void removeCookie(HttpServletResponse response, String name, String domain, String path) {
    var cookie = new Cookie(name, StringUtils.EMPTY_STRING);
    cookie.setMaxAge(0);
    if (domain != null) {
      cookie.setDomain(domain);
    }
    cookie.setPath(path);
    response.addCookie(cookie);
  }

  public static String getCookiePath(ServletContext sc) {
    var path = ValueUtils.valueOrAlt(sc.getSessionCookieConfig().getPath(), sc.getContextPath());
    return !path.isEmpty() ? path : "/";
  }

  public static ZoneOffset getClientZoneId(HttpServletRequest request) {
    var clientZoneId = getCookieValue(request, COOKIE_CLIENT_ZONE_ID);
    if (clientZoneId == null) {
      return null;
    }
    try {
      return ZoneOffset.of(clientZoneId);

    } catch (DateTimeException ex) {
      return null;
    }
  }

  public static String toEtag(byte[] md5) {
    var sb = new StringBuilder(34);
    sb.append('"');
    HexUtils.appendAsHex(sb, md5);
    sb.append('"');
    return sb.toString();
  }

  public static String toEtag(Object... args) {
    List<String> list = Arrays.stream(args).map(o -> Objects.toString(o)).collect(Collectors.toList());
    return toEtag(new DigesterImpl("MD5").digest(String.join("|", list).getBytes(StandardCharsets.UTF_8)));
  }

  public static boolean checkNotModified(HttpServletRequest request, HttpServletResponse response,
      long lastModifiedMs) {
    response.setDateHeader("Last-Modified", lastModifiedMs);
    var ifModifiedSince = request.getDateHeader("If-Modified-Since");

    var notModified = ifModifiedSince >= (lastModifiedMs / 1000 * 1000);
    if (notModified) {
      response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
    }
    return notModified;
  }

  public static boolean checkNotModified(HttpServletRequest request, HttpServletResponse response, String etag) {
    response.setHeader("ETag", etag);
    var ifNoneMatch = request.getHeader("If-None-Match");

    var notModified = etag.equals(ifNoneMatch);
    if (notModified) {
      response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
    }
    return notModified;
  }

  public static boolean checkPrecondition(HttpServletRequest request, HttpServletResponse response,
      long lastModifiedMs) {
    response.setDateHeader("Last-Modified", lastModifiedMs);
    var ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
    return ifUnmodifiedSince == (lastModifiedMs / 1000 * 1000);
  }

  public static boolean checkPrecondition(HttpServletRequest request, HttpServletResponse response, String etag) {
    response.setHeader("ETag", etag);
    var ifMatch = request.getHeader("If-Match");
    return etag.equals(ifMatch);
  }

  public static UserPrincipal getPrincipal(HttpServletRequest request) {
    var principal = request.getUserPrincipal();
    if (principal == null) {
      return null;
    }
    if (!(principal instanceof UserPrincipal)) {
      throw new IllegalStateException("request.getUserPrincipal() must be UserPrincipal.");
    }
    return (UserPrincipal) principal;
  }

  public static UserPrincipal getPrincipalReq(HttpServletRequest request) {
    return Asserts.notNull(getPrincipal(request), "request.getUserPrincipal() is required.");
  }

  public static Object removeAttribute(ServletRequest request, String attribute) {
    var obj = request.getAttribute(attribute);
    if (obj != null) {
      request.removeAttribute(attribute);
    }
    return obj;
  }

  public static String getInitParam(FilterConfig config, String paramName, String defaultValue) {
    var paramVal = StringUtils.trimToNull(config.getInitParameter(paramName));
    return (paramVal != null) ? paramVal : defaultValue;
  }

  public static String getInitParam(ServletConfig config, String paramName, String defaultValue) {
    var paramVal = StringUtils.trimToNull(config.getInitParameter(paramName));
    return (paramVal != null) ? paramVal : defaultValue;
  }

  public static String getInitParam(ServletContext sc, String paramName, String defaultValue) {
    var paramVal = StringUtils.trimToNull(sc.getInitParameter(paramName));
    return (paramVal != null) ? paramVal : defaultValue;
  }

  public static RequestContext getRequestContext(ServletRequest request) {
    var obj = (RequestContext) request.getAttribute(RequestContext.REQUEST_ATTRIBUTE_ID);
    return Asserts.notNull(obj);
  }

  public static ModelState getModelState(ServletRequest request) {
    var obj = (ModelState) request.getAttribute(ModelState.REQUEST_ATTRIBUTE_ID);
    if (obj == null) {
      var form = StringUtils.trimToNull(request.getParameter(PARAM_FORM_FIELD));
      obj = new ModelState(form);
      request.setAttribute(ModelState.REQUEST_ATTRIBUTE_ID, obj);
    }
    return obj;
  }

  public static Messages getMessages(ServletRequest request) {
    var obj = (Messages) request.getAttribute(Messages.REQUEST_ATTRIBUTE_ID);
    if (obj == null) {
      obj = new Messages();
      request.setAttribute(Messages.REQUEST_ATTRIBUTE_ID, obj);
    }
    return obj;
  }

  public static TempData getTempData(ServletRequest request) {
    var obj = (TempData) request.getAttribute(TempData.REQUEST_ATTRIBUTE_ID);
    if (obj == null) {
      obj = new TempData();
      request.setAttribute(TempData.REQUEST_ATTRIBUTE_ID, obj);
    }
    return obj;
  }

  public static Resources getResources(ServletRequest request) {
    return getRequestContext(request).getResources();
  }

  public static FormatProvider getFormatProvider(ServletRequest request) {
    return getRequestContext(request).getFormatProvider();
  }

  public static void addError(ServletRequest request, String fieldName, String msgKey) {
    var msg = getResources(request).get(msgKey);
    getModelState(request).addError(fieldName, msg);
  }

  public static void addError(ServletRequest request, String fieldName, String msgKey, Object[] msgParams) {
    var msg = getResources(request).get(msgKey, msgParams);
    getModelState(request).addError(fieldName, msg);
  }

  public static <T> T getAppScoped(ServletRequest request, Class<T> type) {
    return getAppScoped(request.getServletContext(), type, null);
  }

  public static <T> T getAppScoped(ServletRequest request, Class<T> type, Annotation qualifier) {
    return getAppScoped(request.getServletContext(), type, qualifier);
  }

  public static <T> T getAppScoped(ServletContext sc, Class<T> type) {
    return getAppScoped(sc, type, null);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static <T> T getAppScoped(ServletContext sc, Class<T> type, Annotation qualifier) {
    var beanInsts = AppContextInitializer.getBeanInstances(sc);
    BeanInstance<?> bi = beanInsts.computeIfAbsent(new InstanceKey(type, qualifier), k -> {

      Instance<?> inst = null;
      if (k.getQualifier() == null) {

        inst = CDI.current().select(k.getType());
      } else {
        inst = CDI.current().select(k.getType(), k.getQualifier());
      }

      return ObjectUtils.cast(new BeanInstance(inst.get(), inst));
    });

    return ObjectUtils.cast(bi.get());
  }

  public static boolean loadProps(ServletContext sc, String resourcePath, SimpleConfig config) throws IOException {
    var is = sc.getResourceAsStream(resourcePath);
    if (is == null) {
      return false;
    }
    try {
      config.load(is);
      return true;
    } finally {
      IOUtils.closeQuietly(is);
    }
  }
}
