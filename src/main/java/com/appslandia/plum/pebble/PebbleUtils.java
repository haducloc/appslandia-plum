// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.pebble;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.appslandia.common.base.MapAccessor;
import com.appslandia.common.utils.ArrayUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class PebbleUtils {

  public static final String VARIABLE_REQUEST = "request";
  public static final String VARIABLE_RESPONSE = "response";
  public static final String VARIABLE_REQUEST_CONTEXT = "ctx";

  public static void executePebble(HttpServletRequest request, HttpServletResponse response, Writer out,
      String pebblePath, Map<String, Object> model, Locale locale) throws IOException {
    // Variables
    var variables = (model != null) ? new HashMap<>(model) : new HashMap<String, Object>();
    registerRequestBasedVariables(request, variables);

    variables.put(VARIABLE_REQUEST, request);
    variables.put(VARIABLE_RESPONSE, response);
    variables.put(VARIABLE_REQUEST_CONTEXT, ServletUtils.getRequestContext(request));

    // PebbleTemplateProvider
    var templateProvider = ServletUtils.getAppScoped(request, PebbleTemplateProvider.class);
    var template = templateProvider.getTemplate(pebblePath);

    // Evaluate template
    template.evaluate(out, variables, locale);
  }

  public static void registerRequestBasedVariables(HttpServletRequest request, Map<String, Object> variables) {
    // Maps
    variables.put("requestScope", new MapAccessor<String, Object>() {

      @Override
      public int size() {
        return count(request.getAttributeNames());
      }

      @Override
      public boolean isEmpty() {
        return size() == 0;
      }

      @Override
      public boolean containsKey(Object key) {
        var name = asString(key);
        return (name != null) && (request.getAttribute(name) != null);
      }

      @Override
      public Object get(Object key) {
        var name = asString(key);
        return (name != null) ? request.getAttribute(name) : null;
      }
    });

    variables.put("sessionScope", new MapAccessor<String, Object>() {

      @Override
      public int size() {
        var session = request.getSession(false);
        return (session != null) ? count(session.getAttributeNames()) : 0;
      }

      @Override
      public boolean isEmpty() {
        return size() == 0;
      }

      @Override
      public boolean containsKey(Object key) {
        var session = request.getSession(false);
        var name = asString(key);
        return (session != null && name != null) && (session.getAttribute(name) != null);
      }

      @Override
      public Object get(Object key) {
        var session = request.getSession(false);
        var name = asString(key);
        return (session != null && name != null) ? session.getAttribute(name) : null;
      }
    });

    variables.put("applicationScope", new MapAccessor<String, Object>() {

      @Override
      public int size() {
        return count(request.getServletContext().getAttributeNames());
      }

      @Override
      public boolean isEmpty() {
        return size() == 0;
      }

      @Override
      public boolean containsKey(Object key) {
        var name = asString(key);
        return (name != null) && (request.getServletContext().getAttribute(name) != null);
      }

      @Override
      public Object get(Object key) {
        var name = asString(key);
        return (name != null) ? request.getServletContext().getAttribute(name) : null;
      }
    });

    variables.put("param", new MapAccessor<String, String>() {

      @Override
      public int size() {
        return request.getParameterMap().size();
      }

      @Override
      public boolean isEmpty() {
        return request.getParameterMap().isEmpty();
      }

      @Override
      public boolean containsKey(Object key) {
        var name = asString(key);
        return (name != null) && request.getParameterMap().containsKey(name);
      }

      @Override
      public String get(Object key) {
        var name = asString(key);
        return (name != null) ? request.getParameter(name) : null;
      }
    });

    variables.put("paramValues", new MapAccessor<String, String[]>() {

      @Override
      public int size() {
        return request.getParameterMap().size();
      }

      @Override
      public boolean isEmpty() {
        return request.getParameterMap().isEmpty();
      }

      @Override
      public boolean containsKey(Object key) {
        var name = asString(key);
        return (name != null) && request.getParameterMap().containsKey(name);
      }

      @Override
      public String[] get(Object key) {
        var name = asString(key);
        return (name != null) ? request.getParameterValues(name) : null;
      }
    });

    variables.put("header", new MapAccessor<String, String>() {

      @Override
      public int size() {
        return count(request.getHeaderNames());
      }

      @Override
      public boolean isEmpty() {
        return size() == 0;
      }

      @Override
      public boolean containsKey(Object key) {
        var name = asString(key);
        return (name != null) && (request.getHeader(name) != null);
      }

      @Override
      public String get(Object key) {
        var name = asString(key);
        return (name != null) ? request.getHeader(name) : null;
      }
    });

    variables.put("headerValues", new MapAccessor<String, String[]>() {

      @Override
      public int size() {
        return count(request.getHeaderNames());
      }

      @Override
      public boolean isEmpty() {
        return size() == 0;
      }

      @Override
      public boolean containsKey(Object key) {
        var name = asString(key);
        return (name != null) && (request.getHeader(name) != null);
      }

      @Override
      public String[] get(Object key) {
        var name = asString(key);
        return (name != null) ? ArrayUtils.toArray(request.getHeaders(name), String.class) : null;
      }
    });

    variables.put("initParam", new MapAccessor<String, String>() {

      @Override
      public int size() {
        return count(request.getServletContext().getInitParameterNames());
      }

      @Override
      public boolean isEmpty() {
        return size() == 0;
      }

      @Override
      public boolean containsKey(Object key) {
        var name = asString(key);
        return (name != null) && (request.getServletContext().getInitParameter(name) != null);
      }

      @Override
      public String get(Object key) {
        var name = asString(key);
        return (name != null) ? request.getServletContext().getInitParameter(name) : null;
      }
    });

    // Cookies
    var cookieMap = new HashMap<String, Cookie>();
    var cookies = request.getCookies();

    if (cookies != null) {
      for (var c : cookies) {
        cookieMap.putIfAbsent(c.getName(), c);
      }
    }

    variables.put("cookie", new MapAccessor<String, Cookie>() {

      @Override
      public int size() {
        return cookieMap.size();
      }

      @Override
      public boolean isEmpty() {
        return cookieMap.isEmpty();
      }

      @Override
      public boolean containsKey(Object key) {
        var name = asString(key);
        return (name != null) && cookieMap.containsKey(name);
      }

      @Override
      public Cookie get(Object key) {
        var name = asString(key);
        return (name != null) ? cookieMap.get(name) : null;
      }
    });
  }

  private static String asString(Object key) {
    return (key instanceof String s) ? s : null;
  }

  private static int count(Enumeration<?> names) {
    var count = 0;
    while (names.hasMoreElements()) {
      names.nextElement();
      count++;
    }
    return count;
  }
}
