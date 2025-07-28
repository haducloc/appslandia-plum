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

package com.appslandia.plum.pebble;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.appslandia.common.base.MapAccessor;
import com.appslandia.common.utils.ArrayUtils;
import com.appslandia.common.utils.StreamUtils;
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
    // out.flush();
  }

  public static void registerRequestBasedVariables(HttpServletRequest request, Map<String, Object> variables) {
    // Maps
    variables.put("requestScope", new MapAccessor<String, Object>() {

      @Override
      public int size() {
        return (int) StreamUtils.stream(request.getAttributeNames()).count();
      }

      @Override
      public boolean isEmpty() {
        return size() == 0;
      }

      @Override
      public boolean containsKey(Object key) {
        return StreamUtils.stream(request.getAttributeNames()).anyMatch(a -> a.equals(key));
      }

      @Override
      public Object get(Object key) {
        return request.getAttribute((String) key);
      }
    });

    variables.put("sessionScope", new MapAccessor<String, Object>() {

      @Override
      public int size() {
        var session = request.getSession(false);
        if (session == null) {
          return 0;
        }
        return (int) StreamUtils.stream(session.getAttributeNames()).count();
      }

      @Override
      public boolean isEmpty() {
        return size() == 0;
      }

      @Override
      public boolean containsKey(Object key) {
        var session = request.getSession(false);
        if (session == null) {
          return false;
        }
        return StreamUtils.stream(session.getAttributeNames()).anyMatch(a -> a.equals(key));
      }

      @Override
      public Object get(Object key) {
        var session = request.getSession(false);
        if (session == null) {
          return null;
        }
        return session.getAttribute((String) key);
      }
    });

    variables.put("applicationScope", new MapAccessor<String, Object>() {

      @Override
      public int size() {
        return (int) StreamUtils.stream(request.getServletContext().getAttributeNames()).count();
      }

      @Override
      public boolean isEmpty() {
        return size() == 0;
      }

      @Override
      public boolean containsKey(Object key) {
        return StreamUtils.stream(request.getServletContext().getAttributeNames()).anyMatch(a -> a.equals(key));
      }

      @Override
      public Object get(Object key) {
        return request.getServletContext().getAttribute((String) key);
      }
    });

    variables.put("param", new MapAccessor<String, String>() {

      @Override
      public int size() {
        return request.getParameterMap().size();
      }

      @Override
      public boolean isEmpty() {
        return size() == 0;
      }

      @Override
      public boolean containsKey(Object key) {
        return request.getParameterMap().containsKey(key);
      }

      @Override
      public String get(Object key) {
        return request.getParameter((String) key);
      }
    });

    variables.put("paramValues", new MapAccessor<String, String[]>() {

      @Override
      public int size() {
        return request.getParameterMap().size();
      }

      @Override
      public boolean isEmpty() {
        return size() == 0;
      }

      @Override
      public boolean containsKey(Object key) {
        return request.getParameterMap().containsKey(key);
      }

      @Override
      public String[] get(Object key) {
        return request.getParameterValues((String) key);
      }
    });

    variables.put("header", new MapAccessor<String, String>() {

      final String[] headers = ArrayUtils.toArray(request.getHeaderNames(), String.class);

      @Override
      public int size() {
        return this.headers.length;
      }

      @Override
      public boolean isEmpty() {
        return size() == 0;
      }

      @Override
      public boolean containsKey(Object key) {
        return Arrays.stream(this.headers).anyMatch(h -> h.equalsIgnoreCase((String) key));
      }

      @Override
      public String get(Object key) {
        return request.getHeader((String) key);
      }
    });

    variables.put("headerValues", new MapAccessor<String, String[]>() {

      final String[] headers = ArrayUtils.toArray(request.getHeaderNames(), String.class);

      @Override
      public int size() {
        return this.headers.length;
      }

      @Override
      public boolean isEmpty() {
        return size() == 0;
      }

      @Override
      public boolean containsKey(Object key) {
        return Arrays.stream(this.headers).anyMatch(h -> h.equalsIgnoreCase((String) key));
      }

      @Override
      public String[] get(Object key) {
        return ArrayUtils.toArray(request.getHeaders((String) key), String.class);
      }
    });

    variables.put("initParam", new MapAccessor<String, String>() {

      final String[] initParams = ArrayUtils.toArray(request.getServletContext().getInitParameterNames(), String.class);

      @Override
      public int size() {
        return this.initParams.length;
      }

      @Override
      public boolean isEmpty() {
        return size() == 0;
      }

      @Override
      public boolean containsKey(Object key) {
        return Arrays.stream(this.initParams).anyMatch(h -> h.equals(key));
      }

      @Override
      public String get(Object key) {
        return request.getServletContext().getInitParameter((String) key);
      }
    });

    variables.put("cookie", new MapAccessor<String, Cookie>() {

      @Override
      public int size() {
        return (request.getCookies() != null) ? request.getCookies().length : 0;
      }

      @Override
      public boolean isEmpty() {
        return size() == 0;
      }

      @Override
      public boolean containsKey(Object key) {
        if (request.getCookies() == null) {
          return false;
        }
        return Arrays.stream(request.getCookies()).anyMatch(c -> c.getName().equals((String) key));
      }

      @Override
      public Cookie get(Object key) {
        if (request.getCookies() == null) {
          return null;
        }
        return Arrays.stream(request.getCookies()).filter(c -> c.getName().equals((String) key)).findFirst()
            .orElse(null);
      }
    });
  }
}
