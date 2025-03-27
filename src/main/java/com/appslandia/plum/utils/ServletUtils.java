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
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.DateTimeException;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import com.appslandia.plum.base.ActionDesc;
import com.appslandia.plum.base.ActionDescProvider;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.AuthHandler;
import com.appslandia.plum.base.AuthHandlerProvider;
import com.appslandia.plum.base.BadRequestException;
import com.appslandia.plum.base.BeanInstanceContextListener;
import com.appslandia.plum.base.HttpException;
import com.appslandia.plum.base.InstanceKey;
import com.appslandia.plum.base.LanguageProvider;
import com.appslandia.plum.base.Messages;
import com.appslandia.plum.base.ModelState;
import com.appslandia.plum.base.MutexContextListener;
import com.appslandia.plum.base.MutexSessionListener;
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
  public static final String COOKIE_CLIENT_ZONE_ID = "clientZoneId";

  public static final String PARAM_RETURN_URL = "returnUrl";
  public static final String PARAM_FROM_PAGE_URL = "fromPageUrl";
  public static final String PARAM_FORM_ACTION = "formAction";

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
    LanguageProvider languageProvider = ServletUtils.getAppScoped(request, LanguageProvider.class);
    RequestContext requestContext = getRequestContext(request);

    // URL
    StringBuilder url = newUrlBuilder();
    url.append(request.getContextPath());

    // Language
    if (languageProvider.isMultiLanguages()) {
      url.append('/').append(requestContext.getLanguageId());
    }

    ActionDescProvider actionDescProvider = getAppScoped(request.getServletContext(), ActionDescProvider.class);
    ActionDesc formLogin = actionDescProvider.getFormLogin(requestContext.getModule());

    // If @FormLogin
    if (formLogin != null) {
      url.append('/').append(formLogin.getController());
      url.append('/').append(formLogin.getAction());
      url.append('/');
    } else {
      // Use loginPage from the authHandler
      AuthHandlerProvider authHandlerProvider = ServletUtils.getAppScoped(request, AuthHandlerProvider.class);
      AuthHandler authHandler = authHandlerProvider.getAuthHandler(requestContext.getModule());

      url.append(authHandler.getLoginPage());
    }
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
    String port = request.getHeader("X-Forwarded-Port");
    if (port != null) {
      return port;
    }

    // config.x_forwarded_ports
    AppConfig appConfig = getAppScoped(request.getServletContext(), AppConfig.class);
    String httpPorts = appConfig.getString(AppConfig.CONFIG_X_FORWARDED_PORTS);
    if (httpPorts != null) {

      if (!X_FORWARDED_PORTS_PATTERN.matcher(httpPorts).matches()) {
        throw new IllegalStateException(STR.fmt("X-Forwarded-Ports '{}' is invalid.", httpPorts));
      }
      String[] ports = SplitUtils.splitByComma(httpPorts);

      String scheme = getScheme(request);
      return "https".equals(scheme) ? ports[1] : ports[0];

    } else {

      // request.getServerPort()
      return Integer.toString(request.getServerPort());
    }
  }

  public static StringBuilder absHostBase(HttpServletRequest request) {
    String scheme = getScheme(request);
    String host = getHost(request);
    String port = getPort(request);

    StringBuilder url = newUrlBuilder();
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
    StringBuilder url = newUrlBuilder();

    // URI & QueryString
    appendUriQuery(request, url);
    return url.toString();
  }

  /**
   * Returns the request URL with the embedded path language removed if it exists
   * and pathLang is null.
   *
   */
  public static String getRequestUrl(HttpServletRequest request, String pathLang) {
    StringBuilder url = newUrlBuilder();

    // ContextPath
    url.append(request.getContextPath());

    // Language
    if (pathLang != null) {
      url.append('/').append(pathLang);
    }

    // ServletPath
    RequestContext requestContext = ServletUtils.getRequestContext(request);
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
    String value = request.getHeader("X-Forwarded-For");
    if (value == null) {
      return request.getRemoteAddr();
    }
    int idx = value.indexOf(',');
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
    String appDir = sc.getRealPath("/");
    Asserts.notNull(appDir, "Couldn't determine appDir.");
    return appDir;
  }

  public static boolean isAjaxRequest(HttpServletRequest request) {
    String xrw = request.getHeader("X-Requested-With");
    return "XMLHttpRequest".equalsIgnoreCase(xrw);
  }

  public static void setContentDisposition(HttpServletResponse response, String fileName, boolean inline) {
    String dispositionType = inline ? "inline" : "attachment";
    response.setHeader("Content-Disposition",
        dispositionType + "; filename=\"" + URLEncoding.encodePath(fileName) + "\"");
  }

  public static boolean isContentSupported(String checkingContentType, String supportedType) {
    if (checkingContentType == null) {
      return false;
    }
    int idx = checkingContentType.indexOf(';');
    String mimeType = (idx < 0) ? checkingContentType : checkingContentType.substring(0, idx);
    return supportedType.equalsIgnoreCase(mimeType);
  }

  public static String getBestLanguage(HttpServletRequest request, Collection<String> supportedLanguages) {
    Enumeration<Locale> requestLocales = request.getLocales();
    while (requestLocales.hasMoreElements()) {
      Locale requestLocale = requestLocales.nextElement();

      String matchedLang = supportedLanguages.stream()
          .filter(sl -> Locale.of(sl).getLanguage().equals(requestLocale.getLanguage())).findFirst().orElse(null);
      if (matchedLang != null) {
        return matchedLang;
      }
    }
    return null;
  }

  public static String getBestEncoding(HttpServletRequest request, Collection<String> supportedEncodings) {
    String acceptEncoding = request.getHeader("Accept-Encoding");
    if (acceptEncoding == null || acceptEncoding.isEmpty()) {
      return null;
    }

    String bestEncType = null;
    double bestQValue = 0.0;

    String[] encodings = acceptEncoding.split(",");
    for (String encoding : encodings) {

      String[] parts = encoding.split(";");
      String encType = parts[0].strip();
      double qValue = 1.0;

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
    Object mutex = session.getAttribute(MutexSessionListener.ATTRIBUTE_MUTEX);
    return (mutex != null) ? mutex : session;
  }

  public static Object getMutex(ServletContext context) {
    Object mutex = context.getAttribute(MutexContextListener.ATTRIBUTE_MUTEX);
    return (mutex != null) ? mutex : context;
  }

  public static HttpServletResponse assertNotCommitted(HttpServletResponse response) throws IllegalStateException {
    if (response.isCommitted()) {
      throw new IllegalStateException("The response is already committed.");
    }
    return response;
  }

  public static RequestDispatcher getRequestDispatcher(HttpServletRequest request, String path) {
    RequestDispatcher dispatcher = request.getRequestDispatcher(path);
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
    String statusValue = request.getParameter(parameterName);
    if (statusValue == null) {
      return;
    }

    // 4XX, 5XX
    int status = ParseUtils.parseInt(statusValue, 0);
    if (status >= 400 && status < 600) {

      throw new HttpException(status, parameterName + "=" + status);
    } else {
      throw new BadRequestException(parameterName + " is invalid.");
    }
  }

  public static void testOutStream(HttpServletRequest request, HttpServletResponse response, String parameterName)
      throws IOException {
    String streamType = request.getParameter(parameterName);
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
    StringBuilder sb = new StringBuilder();
    HttpServletRequest req = request;
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
    StringBuilder sb = new StringBuilder();
    HttpServletResponse resp = response;
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
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return null;
    }
    Cookie cookie = Arrays.stream(cookies).filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    return (cookie != null) ? cookie.getValue() : null;
  }

  public static void removeCookie(HttpServletResponse response, String name, String domain, String path) {
    Cookie cookie = new Cookie(name, StringUtils.EMPTY_STRING);
    cookie.setMaxAge(0);
    if (domain != null) {
      cookie.setDomain(domain);
    }
    cookie.setPath(path);
    response.addCookie(cookie);
  }

  public static String getCookiePath(ServletContext sc) {
    String path = ValueUtils.valueOrAlt(sc.getSessionCookieConfig().getPath(), sc.getContextPath());
    return !path.isEmpty() ? path : "/";
  }

  public static ZoneOffset getClientZoneId(HttpServletRequest request) {
    String clientZoneId = getCookieValue(request, COOKIE_CLIENT_ZONE_ID);
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
    StringBuilder sb = new StringBuilder(34);
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
    long ifModifiedSince = request.getDateHeader("If-Modified-Since");

    boolean notModified = ifModifiedSince >= (lastModifiedMs / 1000 * 1000);
    if (notModified) {
      response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
    }
    return notModified;
  }

  public static boolean checkNotModified(HttpServletRequest request, HttpServletResponse response, String etag) {
    response.setHeader("ETag", etag);
    String ifNoneMatch = request.getHeader("If-None-Match");

    boolean notModified = etag.equals(ifNoneMatch);
    if (notModified) {
      response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
    }
    return notModified;
  }

  public static boolean checkPrecondition(HttpServletRequest request, HttpServletResponse response,
      long lastModifiedMs) {
    response.setDateHeader("Last-Modified", lastModifiedMs);
    long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
    return ifUnmodifiedSince == (lastModifiedMs / 1000 * 1000);
  }

  public static boolean checkPrecondition(HttpServletRequest request, HttpServletResponse response, String etag) {
    response.setHeader("ETag", etag);
    String ifMatch = request.getHeader("If-Match");
    return etag.equals(ifMatch);
  }

  public static UserPrincipal getPrincipal(HttpServletRequest request) {
    Principal principal = request.getUserPrincipal();
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

  public static Object removeAttribute(HttpServletRequest request, String attribute) {
    Object obj = request.getAttribute(attribute);
    if (obj != null) {
      request.removeAttribute(attribute);
    }
    return obj;
  }

  public static String getInitParam(FilterConfig config, String paramName, String defaultValue) {
    String paramVal = StringUtils.trimToNull(config.getInitParameter(paramName));
    return (paramVal != null) ? paramVal : defaultValue;
  }

  public static String getInitParam(ServletConfig config, String paramName, String defaultValue) {
    String paramVal = StringUtils.trimToNull(config.getInitParameter(paramName));
    return (paramVal != null) ? paramVal : defaultValue;
  }

  public static String getInitParam(ServletContext sc, String paramName, String defaultValue) {
    String paramVal = StringUtils.trimToNull(sc.getInitParameter(paramName));
    return (paramVal != null) ? paramVal : defaultValue;
  }

  public static RequestContext getRequestContext(HttpServletRequest request) {
    RequestContext obj = (RequestContext) request.getAttribute(RequestContext.REQUEST_ATTRIBUTE_ID);
    return Asserts.notNull(obj);
  }

  public static ModelState getModelState(HttpServletRequest request) {
    ModelState obj = (ModelState) request.getAttribute(ModelState.REQUEST_ATTRIBUTE_ID);
    if (obj == null) {
      obj = new ModelState();
      request.setAttribute(ModelState.REQUEST_ATTRIBUTE_ID, obj);
    }
    return obj;
  }

  public static Messages getMessages(HttpServletRequest request) {
    Messages obj = (Messages) request.getAttribute(Messages.REQUEST_ATTRIBUTE_ID);
    if (obj == null) {
      obj = new Messages();
      request.setAttribute(Messages.REQUEST_ATTRIBUTE_ID, obj);
    }
    return obj;
  }

  public static TempData getTempData(HttpServletRequest request) {
    TempData obj = (TempData) request.getAttribute(TempData.REQUEST_ATTRIBUTE_ID);
    if (obj == null) {
      obj = new TempData();
      request.setAttribute(TempData.REQUEST_ATTRIBUTE_ID, obj);
    }
    return obj;
  }

  public static Resources getResources(HttpServletRequest request) {
    return ServletUtils.getRequestContext(request).getResources();
  }

  public static FormatProvider getFormatProvider(HttpServletRequest request) {
    return ServletUtils.getRequestContext(request).getFormatProvider();
  }

  public static void addError(HttpServletRequest request, String fieldName, String msgKey) {
    String msg = getResources(request).get(msgKey);
    ServletUtils.getModelState(request).addError(fieldName, msg);
  }

  public static void addError(HttpServletRequest request, String fieldName, String msgKey, Object[] msgParams) {
    String msg = getResources(request).get(msgKey, msgParams);
    ServletUtils.getModelState(request).addError(fieldName, msg);
  }

  public static <T> T getAppScoped(HttpServletRequest request, Class<T> type) {
    return getAppScoped(request.getServletContext(), type, null);
  }

  public static <T> T getAppScoped(ServletContext sc, Class<T> type) {
    return getAppScoped(sc, type, null);
  }

  public static <T> T getAppScoped(HttpServletRequest request, Class<T> type, Annotation qualifier) {
    return getAppScoped(request.getServletContext(), type, qualifier);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static <T> T getAppScoped(ServletContext sc, Class<T> type, Annotation qualifier) {
    Map<InstanceKey, BeanInstance<?>> beanInsts = BeanInstanceContextListener.getBeanInstances(sc);
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
    InputStream is = sc.getResourceAsStream(resourcePath);
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
