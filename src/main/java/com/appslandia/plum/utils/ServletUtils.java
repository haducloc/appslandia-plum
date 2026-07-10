// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import com.appslandia.common.base.BaseEncoder;
import com.appslandia.common.cdi.BeanInstance;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.NormalizeUtils;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.common.utils.URLEncoding;
import com.appslandia.plum.base.ActionDescProvider;
import com.appslandia.plum.base.AppContextInitializer;
import com.appslandia.plum.base.AppSessionInitializer;
import com.appslandia.plum.base.AuthPrincipal;
import com.appslandia.plum.base.BadRequestException;
import com.appslandia.plum.base.ForbiddenException;
import com.appslandia.plum.base.HttpException;
import com.appslandia.plum.base.InstanceKey;
import com.appslandia.plum.base.InternalServerException;
import com.appslandia.plum.base.LanguageProvider;
import com.appslandia.plum.base.Message;
import com.appslandia.plum.base.MethodNotAllowedException;
import com.appslandia.plum.base.ModelState;
import com.appslandia.plum.base.NotFoundException;
import com.appslandia.plum.base.PreconditionFailedException;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.base.ServiceUnavailableException;
import com.appslandia.plum.base.TempData;
import com.appslandia.plum.base.TooManyRequestsException;
import com.appslandia.plum.base.UnauthorizedException;
import com.appslandia.plum.base.UnsupportedMediaTypeException;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.DispatcherType;
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
  public static final String REQUEST_ATTRIBUTE_MESSAGES = "__messages";

  public static final String PARAM_RETURN_URL = "__return_url";
  public static final String PARAM_FORM_FIELD = "__form";
  public static final String PARAM_FORM_ACTION = "__form_action";
  public static final String PARAM_REAUTHENTICATION = "__reauthentication";
  public static final String PARAM_NONCE = "__nonce";

  public static StringBuilder newUriBuilder() {
    return new StringBuilder(255);
  }

  public static String getMvcBaseUrl(HttpServletRequest request) {
    var url = getHostUrl(request);
    url.append(request.getContextPath());

    var languageProvider = getAppScoped(request, LanguageProvider.class);
    if (languageProvider.isMultiLanguages()) {

      var requestContext = getRequestContext(request);
      url.append('/').append(requestContext.getLanguageId());
    }
    return url.toString();
  }

  public static String getUriWithQuery(HttpServletRequest request) {
    Asserts.isTrue(request.getDispatcherType() == DispatcherType.REQUEST,
        "request.getDispatcherType() must be DispatcherType.REQUEST.");

    var uri = newUriBuilder();

    // requestURI = contextPath + servletPath (+ pathInfo if not null)
    uri.append(request.getRequestURI());

    var queryString = request.getQueryString();
    if (queryString != null) {
      uri.append('?').append(queryString);
    }
    return uri.toString();
  }

  public static String getUriWithQuery(HttpServletRequest request, String pathLang) {
    Asserts.isTrue(request.getDispatcherType() == DispatcherType.REQUEST,
        "request.getDispatcherType() must be DispatcherType.REQUEST.");

    Arguments.notNull(pathLang);
    var uri = newUriBuilder();

    uri.append(request.getContextPath());
    uri.append('/').append(pathLang);

    uri.append(request.getServletPath());

    var pathInfo = request.getPathInfo();
    if (pathInfo != null) {
      uri.append(pathInfo);
    }

    var queryString = request.getQueryString();
    if (queryString != null) {
      uri.append('?').append(queryString);
    }
    return uri.toString();
  }

  public static StringBuilder getFormLoginUri(HttpServletRequest request) {
    var languageProvider = getAppScoped(request, LanguageProvider.class);
    var requestContext = getRequestContext(request);

    var uri = newUriBuilder();
    uri.append(request.getContextPath());

    if (languageProvider.isMultiLanguages()) {
      uri.append('/').append(requestContext.getLanguageId());
    }

    var actionDescProvider = getAppScoped(request.getServletContext(), ActionDescProvider.class);
    var formLogin = actionDescProvider.getFormLogin(requestContext.getModule());
    Asserts.notNull(formLogin, "No @FormLogin is registered for module '{}'.", requestContext.getModule());

    uri.append('/').append(formLogin.getController());
    uri.append('/').append(formLogin.getAction());
    return uri;
  }

  public static StringBuilder getHostUrl(HttpServletRequest request) {
    var requestOrigin = getRequestContext(request).getRequestOrigin();

    var url = newUriBuilder();
    url.append(requestOrigin.getScheme()).append("://").append(requestOrigin.getHost());

    if (!requestOrigin.isDefaultPort()) {
      url.append(':').append(requestOrigin.getPort());
    }
    return url;
  }

  public static String getNormUserAgent(HttpServletRequest request) {
    return getNormUserAgent(request, 512);
  }

  public static String getNormUserAgent(HttpServletRequest request, int maxLen) {
    var ua = request.getHeader("User-Agent");
    if (ua == null) {
      return null;
    }

    ua = NormalizeUtils.normalizeString(ua);
    if (ua == null) {
      return null;
    }

    if (ua.length() > maxLen) {
      ua = ua.substring(0, maxLen);
    }
    return ua;
  }

  public static String getClientBoundData(HttpServletRequest request, boolean boundClientIp, boolean boundUserAgent) {
    // IP
    var clientIp = boundClientIp ? ServletUtils.getRequestContext(request).getClientAddress() : "IP";

    // User-Agent
    var userAgent = boundUserAgent ? getNormUserAgent(request) : null;
    if (userAgent == null) {
      userAgent = "UA";
    }

    // IP | UA
    return clientIp + "|" + BaseEncoder.BASE64_NP.encode(userAgent.getBytes(StandardCharsets.UTF_8));
  }

  public static String getAppDir(ServletContext sc) {
    var appDir = sc.getRealPath("/");
    Asserts.notNull(appDir, "Couldn't determine appDir.");
    return appDir;
  }

  public static void writeNoCache(HttpServletResponse response) {
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Expires", "0");
  }

  public static void setFileDisposition(HttpServletResponse response, String fileName, boolean inline) {
    var dispositionType = inline ? "inline" : "attachment";

    fileName = HttpSecurityUtils.sanitizeHttpHeaderValue(fileName);
    if (fileName == null) {
      fileName = "file";
    }

    var asciiFileName = NormalizeUtils.normalizeAscii(fileName, "_");
    if (asciiFileName == null) {
      asciiFileName = "file";
    }

    var encodedFileName = URLEncoding.encodeRFC5987(fileName);
    if (encodedFileName == null) {
      encodedFileName = "file";
    }

    response.setHeader("Content-Disposition",
        dispositionType + "; filename=\"" + asciiFileName + "\"; filename*=UTF-8''" + encodedFileName);
  }

  public static String getMimeType(String contentType) {
    if (contentType == null) {
      return null;
    }

    int idx = contentType.indexOf(';');
    return (idx >= 0) ? contentType.substring(0, idx).strip() : contentType.strip();
  }

  public static boolean isMimeTypeSupported(String requestContentType, String supportedMimeType) {
    Arguments.notNull(supportedMimeType);

    var mimeType = getMimeType(requestContentType);
    return (mimeType != null) && supportedMimeType.equalsIgnoreCase(mimeType);
  }

  public static boolean isJsonMimeType(String requestContentType) {
    var mimeType = getMimeType(requestContentType);
    if (mimeType == null) {
      return false;
    }

    mimeType = mimeType.toLowerCase(Locale.ROOT);
    return mimeType.equals("application/json") || mimeType.endsWith("+json");
  }

  public static Object getMutex(HttpSession session) {
    var mutex = session.getAttribute(AppSessionInitializer.SESSION_ATTRIBUTE_MUTEX);
    return (mutex != null) ? mutex : session;
  }

  public static Object getMutex(ServletContext context) {
    var mutex = context.getAttribute(AppContextInitializer.CONTEXT_ATTRIBUTE_MUTEX);
    return (mutex != null) ? mutex : context;
  }

  public static HttpServletResponse assertNotCommitted(HttpServletResponse response) throws IllegalStateException {
    if (response.isCommitted()) {
      throw new IllegalStateException("The response is already committed.");
    }
    return response;
  }

  public static void logoutSession(HttpServletRequest request) throws ServletException {
    if (request.getUserPrincipal() != null) {
      request.logout();
    }

    var session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
  }

  public static <T extends HttpServletResponse> T unwrap(HttpServletResponse response, Class<T> toType) {
    var current = response;

    while (true) {
      if (toType.isInstance(current)) {
        return toType.cast(current);
      }

      if (!(current instanceof HttpServletResponseWrapper wrapper)
          || !(wrapper.getResponse() instanceof HttpServletResponse httpResponse)) {
        return null;
      }

      current = httpResponse;
    }
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

  public static void testErrorStatus(HttpServletRequest request, String parameterName) {
    var status = ParamUtils.getInt(request, parameterName);
    if (status == 0) {
      return;
    }

    // 4XX, 5XX
    if (status >= 400 && status < 600) {
      var message = "Test error status=" + status;

      switch (status) {
      case 400 -> throw new BadRequestException(message);
      case 401 -> throw new UnauthorizedException(message);
      case 403 -> throw new ForbiddenException(message);
      case 404 -> throw new NotFoundException(message);
      case 405 -> throw new MethodNotAllowedException(message, null);
      case 412 -> throw new PreconditionFailedException(message);
      case 415 -> throw new UnsupportedMediaTypeException(message);
      case 429 -> throw new TooManyRequestsException(message, null);
      case 500 -> throw new InternalServerException(message);
      case 503 -> throw new ServiceUnavailableException(message, null);
      default -> throw new HttpException(message, status);
      }

    } else {
      throw new BadRequestException(parameterName + " is invalid.");
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

  public static Cookie getCookie(HttpServletRequest request, String name) {
    Arguments.notNull(name);

    var cookies = request.getCookies();
    if (cookies == null) {
      return null;
    }
    var cookie = Arrays.stream(cookies).filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    return cookie;
  }

  public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
      String path) {
    saveCookie(request, response, cookieName, "", 0, path, null);
  }

  public static void saveCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
      String cookieValue, int maxAge, String path, Consumer<Cookie> cookieInit) {
    var cookie = new Cookie(cookieName, cookieValue);
    cookie.setMaxAge(maxAge);

    if (path == null) {
      path = request.getServletContext().getContextPath();
      if (path.isEmpty()) {
        path = "/";
      }
    }
    cookie.setPath(path);
    cookie.setHttpOnly(true);

    if (cookieInit != null) {
      cookieInit.accept(cookie);
    }

    if (cookie.getAttribute("SameSite") == null) {
      cookie.setAttribute("SameSite", "Lax");
    }
    response.addCookie(cookie);
  }

  public static boolean checkNotModified(HttpServletRequest request, HttpServletResponse response,
      long lastModifiedMs) {

    var method = request.getMethod();
    Arguments.isTrue("GET".equals(method) || "HEAD".equals(method), "HTTP method must be GET or HEAD.");

    lastModifiedMs = (lastModifiedMs / 1000) * 1000;
    response.setDateHeader("Last-Modified", lastModifiedMs);

    long ifModifiedSince;
    try {
      ifModifiedSince = request.getDateHeader("If-Modified-Since");
    } catch (IllegalArgumentException e) {
      return false;
    }

    if (ifModifiedSince < 0) {
      return false;
    }

    ifModifiedSince = (ifModifiedSince / 1000) * 1000;
    if (ifModifiedSince >= lastModifiedMs) {
      response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
      return true;
    }
    return false;
  }

  public static boolean checkNotModified(HttpServletRequest request, HttpServletResponse response, String etag) {
    Arguments.notNull(etag);

    var method = request.getMethod();
    Arguments.isTrue("GET".equals(method) || "HEAD".equals(method), "HTTP method must be GET or HEAD.");

    response.setHeader("ETag", etag);

    var ifNoneMatch = request.getHeader("If-None-Match");
    var matched = EtagUtils.containsWeakEtag(ifNoneMatch, etag);

    if (matched) {
      response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
    }
    return matched;
  }

  public static boolean checkPrecondition(HttpServletRequest request, HttpServletResponse response,
      long lastModifiedMs) {

    var method = request.getMethod();
    Arguments.isTrue(!"GET".equals(method) && !"HEAD".equals(method), "HTTP method must not be GET or HEAD.");

    lastModifiedMs = (lastModifiedMs / 1000) * 1000;
    response.setDateHeader("Last-Modified", lastModifiedMs);

    long ifUnmodifiedSince;
    try {
      ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
    } catch (IllegalArgumentException e) {
      return true;
    }

    if (ifUnmodifiedSince < 0) {
      return true;
    }

    ifUnmodifiedSince = (ifUnmodifiedSince / 1000) * 1000;
    var ok = lastModifiedMs <= ifUnmodifiedSince;
    if (!ok) {
      response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
    }
    return ok;
  }

  public static boolean checkPrecondition(HttpServletRequest request, HttpServletResponse response, String etag) {
    Arguments.notNull(etag);

    var method = request.getMethod();
    Arguments.isTrue(!"GET".equals(method) && !"HEAD".equals(method), "HTTP method must not be GET or HEAD.");
    response.setHeader("ETag", etag);

    // If-Match
    var ifMatch = request.getHeader("If-Match");
    if (ifMatch != null) {
      var ok = EtagUtils.containsStrongEtag(ifMatch, etag);
      if (!ok) {
        response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
      }
      return ok;
    }

    // If-None-Match
    var ifNoneMatch = request.getHeader("If-None-Match");
    if (ifNoneMatch != null) {
      var matched = EtagUtils.containsWeakEtag(ifNoneMatch, etag);
      if (matched) {
        response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
        return false;
      }
    }

    return true;
  }

  public static AuthPrincipal getPrincipal(HttpServletRequest request) {
    var principal = request.getUserPrincipal();
    if (principal == null) {
      return null;
    }
    if (!(principal instanceof AuthPrincipal)) {
      throw new IllegalStateException("request.getUserPrincipal() must be AuthPrincipal.");
    }
    return (AuthPrincipal) principal;
  }

  public static AuthPrincipal getPrincipalReq(HttpServletRequest request) {
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
    return Asserts.notNull(obj, "No RequestContext request attribute found.");
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

  @SuppressWarnings("unchecked")
  public static List<Message> getMessages(ServletRequest request) {
    var obj = (List<Message>) request.getAttribute(REQUEST_ATTRIBUTE_MESSAGES);
    if (obj == null) {
      obj = new ArrayList<>();
      request.setAttribute(REQUEST_ATTRIBUTE_MESSAGES, obj);
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
}
