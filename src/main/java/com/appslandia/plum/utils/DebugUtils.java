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

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.TreeSet;

import com.appslandia.common.base.TextBuilder;
import com.appslandia.common.cdi.AnyLiteral;
import com.appslandia.common.utils.MimeTypes;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.plum.base.AppConfig;

import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class DebugUtils {

  public static void writeBeanInfo(BeanManager manager, HttpServletResponse response) throws Exception {
    response.setContentType(MimeTypes.TEXT_PLAIN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    var out = response.getWriter();
    var sb = new TextBuilder();

    var sortedBeans = new TreeSet<Bean<?>>(
        (b1, b2) -> b1.getBeanClass().getName().compareTo(b2.getBeanClass().getName()));
    sortedBeans.addAll(manager.getBeans(Object.class, AnyLiteral.IMPL));

    for (Bean<?> bean : sortedBeans) {
      sb.append(bean.getBeanClass()).append(", scope=").append(bean.getScope().getSimpleName()).append(", qualifiers=")
          .append(ObjectUtils.asString(bean.getQualifiers()));
      sb.appendln();
    }
    out.write(sb.toString());
    out.flush();
  }

  public static void writeServletInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    response.setContentType(MimeTypes.TEXT_PLAIN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    var out = response.getWriter();

    for (ServletRegistration registration : request.getServletContext().getServletRegistrations().values()) {
      out.append("servletName=").append(registration.getName()).append(", servletClass=")
          .append(registration.getClassName()).append(", urlPatterns=")
          .append(ObjectUtils.asString(registration.getMappings()));
      out.println();
    }
    out.println();

    for (FilterRegistration registration : request.getServletContext().getFilterRegistrations().values()) {
      out.append("filterName=").append(registration.getName()).append(", filterClass=")
          .append(registration.getClassName()).append(", urlPatterns=")
          .append(ObjectUtils.asString(registration.getUrlPatternMappings())).append(", servletNames=")
          .append(ObjectUtils.asString(registration.getServletNameMappings()));
      out.println();
    }
    out.flush();
  }

  public static void writeTcpInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    response.setContentType(MimeTypes.TEXT_PLAIN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    var out = response.getWriter();

    try {
      out.append("InetAddress.getLocalHost(): ").append(InetAddress.getLocalHost().toString());
      out.println();
    } catch (Exception ex) {
      out.append("InetAddress.getLocalHost(): ").append(ex.getMessage());
      out.println();
    }
    out.println();

    out.append("request.getServerName(): ").append(request.getServerName());
    out.println();
    out.append("request.getServerPort(): ").append(String.valueOf(request.getServerPort()));
    out.println();
    out.append("request.getLocalName(): ").append(request.getLocalName());
    out.println();
    out.append("request.getLocalAddr(): ").append(request.getLocalAddr());
    out.println();
    out.append("request.getLocalPort(): ").append(String.valueOf(request.getLocalPort()));
    out.println();
    out.append("request.getRemoteHost(): ").append(request.getRemoteHost());
    out.println();
    out.append("request.getRemoteAddr(): ").append(request.getRemoteAddr());
    out.println();
    out.append("request.getRemotePort(): ").append(String.valueOf(request.getRemotePort()));
    out.println();
    out.append("request.getRemoteUser(): ").append(request.getRemoteUser());
    out.println();
    out.append("request.getScheme(): ").append(request.getScheme());
    out.println();
    out.append("request.getProtocol(): ").append(request.getProtocol());
    out.println();
    out.append("ServletUtils.getScheme(request): ").append(ServletUtils.getScheme(request));
    out.println();
    out.append("ServletUtils.getHost(request): ").append(ServletUtils.getHost(request));
    out.println();
    out.append("ServletUtils.getPort(request): ").append(ServletUtils.getPort(request));
    out.println();
    out.append("ServletUtils.getClientIp(request): ").append(ServletUtils.getClientIp(request));
    out.println();
    out.flush();
  }

  public static void writeConfigs(AppConfig appConfig, HttpServletResponse response) throws Exception {
    response.setContentType(MimeTypes.TEXT_PLAIN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    var out = response.getWriter();
    var sb = new TextBuilder();

    final var keys = new TreeSet<String>();
    var iter = appConfig.getKeys();

    while (iter.hasNext()) {
      keys.add(iter.next());
    }

    for (String key : keys) {
      out.append(key).append("=").append(appConfig.getString(key));
      out.println();
    }

    out.write(sb.toString());
    out.flush();
  }

  public static void writeEnvsProps(HttpServletRequest request, HttpServletResponse response) throws Exception {
    response.setContentType(MimeTypes.TEXT_PLAIN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    var out = response.getWriter();

    for (Map.Entry<String, String> env : System.getenv().entrySet()) {
      out.append(env.getKey()).append("=").append(env.getValue());
      out.println();
    }
    out.println();
    for (Map.Entry<Object, Object> env : System.getProperties().entrySet()) {
      out.append(env.getKey().toString()).append("=").append(env.getValue().toString());
      out.println();
    }
    out.flush();
  }

  public static void writeSysDateTime(HttpServletRequest request, HttpServletResponse response) throws Exception {
    response.setContentType(MimeTypes.TEXT_PLAIN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    var out = response.getWriter();

    out.append("OffsetDateTime.now(): ").append(OffsetDateTime.now().toString());
    out.println();
    out.append("ZoneId.systemDefault(): ").append(ZoneId.systemDefault().toString());
    out.println();

    out.flush();
  }

  public static void writeRequestLocales(HttpServletRequest request, HttpServletResponse response) throws Exception {
    response.setContentType(MimeTypes.TEXT_PLAIN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    var out = response.getWriter();
    out.append("request.getLocales()");
    out.println();

    var requestLocales = request.getLocales();
    while (requestLocales.hasMoreElements()) {
      var requestLocale = requestLocales.nextElement();

      out.append(" - ").append(requestLocale.toString());
      out.println();
    }
    out.flush();
  }
}
