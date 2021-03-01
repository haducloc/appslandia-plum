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

package com.appslandia.plum.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import com.appslandia.common.base.BOMOutputStream;
import com.appslandia.common.base.FormatProvider;
import com.appslandia.common.base.PropertyConfig;
import com.appslandia.common.cdi.BeanInstance;
import com.appslandia.common.crypto.DigesterImpl;
import com.appslandia.common.utils.AssertUtils;
import com.appslandia.common.utils.HexUtils;
import com.appslandia.common.utils.IOUtils;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.common.utils.URLEncoding;
import com.appslandia.common.utils.ValueUtils;
import com.appslandia.plum.base.ActionDesc;
import com.appslandia.plum.base.ActionDescProvider;
import com.appslandia.plum.base.BeanInstanceContextListener;
import com.appslandia.plum.base.Messages;
import com.appslandia.plum.base.ModelState;
import com.appslandia.plum.base.MutexContextListener;
import com.appslandia.plum.base.MutexSessionListener;
import com.appslandia.plum.base.PrefCookie;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.base.Resources;
import com.appslandia.plum.base.TempData;
import com.appslandia.plum.base.UserPrincipal;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ServletUtils {

	public static final String ACTION_INDEX = "index";
	public static final String REQUEST_ATTRIBUTE_MODEL = "model";

	public static final String PARAM_RETURN_URL = "returnUrl";
	public static final String PARAM_SERVER_NAME = "__SERVER_NAME";

	public static final String COOKIE_CLIENT_ZONE_ID = "clientZoneId";

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
		RequestContext requestContext = getRequestContext(request);
		ActionDesc formLogin = getAppScoped(request, ActionDescProvider.class).getFormLogin(requestContext.getModule());

		StringBuilder url = newUrlBuilder();
		url.append(request.getServletContext().getContextPath());

		if (requestContext.isPathLanguage()) {
			url.append('/').append(requestContext.getLanguageId());
		}
		url.append('/').append(formLogin.getController()).append('/').append(formLogin.getAction());
		return url;
	}

	public static String getSecureUrl(HttpServletRequest request, int httpsPort) {
		StringBuilder url = newUrlBuilder();

		// https://{serverName}:{httpsPort}
		url.append("https://").append(request.getServerName());

		if (httpsPort != 443) {
			url.append(':').append(httpsPort);
		}

		// URI & QueryString
		appendUriQuery(request, url);
		return url.toString();
	}

	public static String getRequestUrl(HttpServletRequest request) {
		StringBuilder url = newUrlBuilder();

		// {scheme}://{serverName}:{serverPort}
		url.append(request.getScheme()).append("://").append(request.getServerName());

		if ((request.getServerPort() != 80) && (request.getServerPort() != 443)) {
			url.append(':').append(request.getServerPort());
		}

		// URI & QueryString
		appendUriQuery(request, url);
		return url.toString();
	}

	public static String getRequestUrl(HttpServletRequest request, String pathLang) {
		StringBuilder url = newUrlBuilder();

		// {scheme}://{serverName}:{serverPort}
		url.append(request.getScheme()).append("://").append(request.getServerName());

		if ((request.getServerPort() != 80) && (request.getServerPort() != 443)) {
			url.append(':').append(request.getServerPort());
		}
		// ContextPath
		url.append(request.getServletContext().getContextPath());

		// Language
		url.append('/').append(pathLang);

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

	public static StringBuilder getClientBuilder(HttpServletRequest request, StringBuilder builder) {
		if (builder.length() > 0) {
			builder.append(", ");
		}
		builder.append("remoteAddr=").append(request.getRemoteAddr());
		builder.append(", remotePort=").append(request.getRemotePort());

		String value = request.getHeader("X-Forwarded-For");
		if (value != null) {
			builder.append(", X-Forwarded-For=").append(value);
		}

		if (request.getRemoteUser() != null) {
			builder.append(", remoteUser=").append(request.getRemoteUser());
		}

		value = request.getHeader("User-Agent");
		if (value != null) {
			builder.append(", User-Agent=").append(value);
		}
		return builder;
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
		value = value.substring(0, idx).trim();
		return !value.isEmpty() ? value : request.getRemoteAddr();
	}

	public static boolean isLocalhost(String serverName) throws IllegalArgumentException {
		try {
			byte[] b = InetAddress.getByName(serverName).getAddress();

			// IPv4
			if (b.length == 4) {
				return b[0] == 127 && b[1] == 0 && b[2] == 0 && b[3] == 1;
			}

			// IPv6
			if (b[15] != 1)
				return false;
			return !IntStream.range(0, 15).anyMatch(i -> b[i] != 0);

		} catch (UnknownHostException ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	public static String getAppDir(ServletContext sc) {
		String appDir = sc.getRealPath("/");
		AssertUtils.assertNotNull(appDir, "Can't determine appDir.");
		return appDir;
	}

	public static boolean isGzipAccepted(HttpServletRequest request) {
		String ae = request.getHeader("Accept-Encoding");
		return (ae != null) && (ae.toLowerCase(Locale.ENGLISH).contains("gzip"));
	}

	public static boolean isAjaxRequest(HttpServletRequest request) {
		String xrw = request.getHeader("X-Requested-With");
		return "XMLHttpRequest".equalsIgnoreCase(xrw);
	}

	public static void setContentDisposition(HttpServletResponse response, String fileName, boolean inline) {
		String dispositionType = inline ? "inline" : "attachment";
		response.setHeader("Content-Disposition", dispositionType + "; filename=\"" + URLEncoding.encodePath(fileName) + "\"");
	}

	public static boolean allowContentType(String contentType, String allowType) {
		if (contentType == null) {
			return false;
		}
		int idx = contentType.indexOf(';');
		String mimeType = (idx < 0) ? contentType : contentType.substring(0, idx);
		return allowType.equalsIgnoreCase(mimeType);
	}

	public static String parseLanguage(HttpServletRequest request, Collection<String> supportedLanguages) {
		Enumeration<Locale> requestLocales = request.getLocales();
		while (requestLocales.hasMoreElements()) {
			Locale requestLocale = requestLocales.nextElement();

			String matchedLang = supportedLanguages.stream().filter(sl -> new Locale(sl).getLanguage().equals(requestLocale.getLanguage())).findFirst()
					.orElse(null);
			if (matchedLang != null) {
				return matchedLang;
			}
		}
		return null;
	}

	public static Object getMutex(HttpSession session) {
		Object mutex = session.getAttribute(MutexSessionListener.ATTRIBUTE_MUTEX);
		return (mutex != null) ? mutex : session;
	}

	public static Object getMutex(ServletContext context) {
		Object mutex = context.getAttribute(MutexContextListener.ATTRIBUTE_MUTEX);
		return (mutex != null) ? mutex : context;
	}

	public static Writer getWriter(HttpServletResponse response) throws IOException {
		try {
			return response.getWriter();
		} catch (IllegalStateException ex) {
			return new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), response.getCharacterEncoding()));
		}
	}

	public static PrintWriter getPrintWriter(HttpServletResponse response) throws IOException {
		try {
			return response.getWriter();
		} catch (IllegalStateException ex) {
			return new PrintWriter(new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), response.getCharacterEncoding())));
		}
	}

	public static PrintWriter getBOMWriter(HttpServletResponse response) throws IOException, IllegalStateException {
		return new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(new BOMOutputStream(response.getOutputStream(), response.getCharacterEncoding()), response.getCharacterEncoding())));
	}

	public static HttpServletResponse assertNotCommitted(HttpServletResponse response) throws IllegalStateException {
		if (response.isCommitted()) {
			throw new IllegalStateException("The response is already committed.");
		}
		return response;
	}

	public static RequestDispatcher getRequestDispatcher(HttpServletRequest request, String path) {
		RequestDispatcher dispatcher = request.getRequestDispatcher(path);
		if (dispatcher == null) {
			throw new IllegalArgumentException("getRequestDispatcher (path=" + path + ")");
		}
		return dispatcher;
	}

	public static void forward(HttpServletRequest request, HttpServletResponse response, String path) throws ServletException, IOException {
		getRequestDispatcher(request, path).forward(request, assertNotCommitted(response));
	}

	public static void include(HttpServletRequest request, HttpServletResponse response, String path) throws ServletException, IOException {
		getRequestDispatcher(request, path).include(request, response);
	}

	public static void sendRedirect(HttpServletResponse response, String location, int status) throws IllegalStateException {
		assertNotCommitted(response);

		response.resetBuffer();
		response.setHeader("Location", location);
		response.setStatus(status);
	}

	public static String toWrapperPath(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		HttpServletRequest req = request;
		while (true) {
			if (sb.length() == 0) {
				sb.append(ObjectUtils.toHashInfo(req));
			} else {
				sb.append(" > ").append(ObjectUtils.toHashInfo(req));
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
				sb.append(ObjectUtils.toHashInfo(resp));
			} else {
				sb.append(" > ").append(ObjectUtils.toHashInfo(resp));
			}
			if (!(resp instanceof HttpServletResponseWrapper)) {
				return sb.toString();
			}
			resp = (HttpServletResponse) ((HttpServletResponseWrapper) resp).getResponse();
		}
	}

	public static <T> T unwrapRequest(HttpServletRequest request, Class<T> toImpl) {
		HttpServletRequest req = request;
		while (true) {
			if (req.getClass() == toImpl) {
				return ObjectUtils.cast(req);
			}
			if (!(req instanceof HttpServletRequestWrapper)) {
				return null;
			}
			req = (HttpServletRequest) ((HttpServletRequestWrapper) req).getRequest();
		}
	}

	public static <T> T unwrapResponse(HttpServletResponse response, Class<T> toImpl) {
		HttpServletResponse resp = response;
		while (true) {
			if (resp.getClass() == toImpl) {
				return ObjectUtils.cast(resp);
			}
			if (!(resp instanceof HttpServletResponseWrapper)) {
				return null;
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

	public static ZoneOffset getClientZone(HttpServletRequest request) {
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

	public static boolean checkNotModified(HttpServletRequest request, HttpServletResponse response, long lastModifiedMs) {
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

	public static boolean checkPrecondition(HttpServletRequest request, HttpServletResponse response, long lastModifiedMs) {
		response.setDateHeader("Last-Modified", lastModifiedMs);
		long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
		return ifUnmodifiedSince == (lastModifiedMs / 1000 * 1000);
	}

	public static boolean checkPrecondition(HttpServletRequest request, HttpServletResponse response, String etag) {
		response.setHeader("ETag", etag);
		String ifMatch = request.getHeader("If-Match");
		return etag.equals(ifMatch);
	}

	public static UserPrincipal getUserPrincipal(HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal == null) {
			return null;
		}
		if (!(principal instanceof UserPrincipal)) {
			throw new IllegalStateException("request.getUserPrincipal() must be UserPrincipal.");
		}
		return (UserPrincipal) principal;
	}

	public static UserPrincipal getRequiredPrincipal(HttpServletRequest request) {
		return AssertUtils.assertStateNotNull(getUserPrincipal(request), "request.getUserPrincipal() must be not null.");
	}

	public static void setWWWAuthenticate(HttpServletResponse response, String authType, String realmName) {
		response.setHeader("WWW-Authenticate", authType + " realm=\"" + realmName + "\"");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
		if (obj == null) {
			throw new IllegalStateException("requestContext is null.");
		}
		return obj;
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

	public static PrefCookie getPrefCookie(HttpServletRequest request) {
		return (PrefCookie) request.getAttribute(PrefCookie.REQUEST_ATTRIBUTE_ID);
	}

	public static Resources getResources(HttpServletRequest request) {
		return ServletUtils.getRequestContext(request).getResources();
	}

	public static FormatProvider getFormatProvider(HttpServletRequest request) {
		return ServletUtils.getRequestContext(request).getFormatProvider();
	}

	public static void addError(HttpServletRequest request, String fieldName, String msgKey) {
		ServletUtils.getModelState(request).addError(fieldName, getResources(request).get(msgKey));
	}

	public static void addError(HttpServletRequest request, String fieldName, String msgKey, Map<String, Object> msgParams) {
		ServletUtils.getModelState(request).addError(fieldName, getResources(request).get(msgKey, msgParams));
	}

	public static <T> T getAppScoped(HttpServletRequest request, Class<T> beanType) {
		return getAppScoped(request.getServletContext(), beanType);
	}

	public static <T> T getAppScoped(ServletContext sc, Class<T> beanType) {
		Map<Class<?>, BeanInstance<?>> beanInsts = BeanInstanceContextListener.getBeanInstances(sc);

		BeanInstance<T> bi = ObjectUtils.cast(beanInsts.computeIfAbsent(beanType, t -> {

			Instance<T> inst = ObjectUtils.cast(CDI.current().select(t));
			return new BeanInstance<T>(inst.get(), inst);
		}));

		return bi.get();
	}

	public static boolean loadProps(ServletContext sc, String resourcePath, Properties props) throws IOException {
		InputStream is = sc.getResourceAsStream(resourcePath);
		if (is == null) {
			return false;
		}
		try {
			props.load(is);
			return true;
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	public static boolean loadProps(ServletContext sc, String resourcePath, PropertyConfig config) throws IOException {
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
