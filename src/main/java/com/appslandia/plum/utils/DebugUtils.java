// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.utils;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.TreeSet;

import com.appslandia.common.base.TextBuilder;
import com.appslandia.common.cdi.AnyLiteral;
import com.appslandia.common.utils.ContentTypes;
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
    response.setContentType(ContentTypes.TEXT_PLAIN);
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
    response.setContentType(ContentTypes.TEXT_PLAIN);
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

  public static void writeServerInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    response.setContentType(ContentTypes.TEXT_PLAIN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    var out = response.getWriter();

    out.append("request.getScheme(): ").append(request.getScheme());
    out.println();
    out.append("request.getServerName(): ").append(request.getServerName());
    out.println();
    out.append("request.getServerPort(): ").append(String.valueOf(request.getServerPort()));
    out.println();
    out.append("request.getRemoteAddr(): ").append(request.getRemoteAddr());
    out.println();

    var ctx = ServletUtils.getRequestContext(request);

    out.append("requestOrigin.getScheme(): ").append(ctx.getRequestOrigin().getScheme());
    out.println();
    out.append("requestOrigin.getHost(): ").append(ctx.getRequestOrigin().getHost());
    out.println();
    out.append("requestOrigin.getPort(): ").append(String.valueOf(ctx.getRequestOrigin().getPort()));
    out.println();
    out.append("requestOrigin.getClientIp(): ").append(ctx.getRequestOrigin().getClientIp().getHostAddress());
    out.println();

    out.flush();
  }

  public static void writeConfigs(AppConfig appConfig, HttpServletResponse response) throws Exception {
    response.setContentType(ContentTypes.TEXT_PLAIN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    var out = response.getWriter();
    var sb = new TextBuilder();

    final var keys = new TreeSet<String>();
    var iter = appConfig.getKeys();

    while (iter.hasNext()) {
      keys.add(iter.next());
    }

    for (String key : keys) {
      out.append(key).append('=').append(appConfig.getString(key));
      out.println();
    }

    out.write(sb.toString());
    out.flush();
  }

  public static void writeEnvsProps(HttpServletRequest request, HttpServletResponse response) throws Exception {
    response.setContentType(ContentTypes.TEXT_PLAIN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    var out = response.getWriter();

    for (Map.Entry<String, String> env : System.getenv().entrySet()) {
      out.append(env.getKey()).append('=').append(env.getValue());
      out.println();
    }
    out.println();
    for (Map.Entry<Object, Object> env : System.getProperties().entrySet()) {
      out.append(env.getKey().toString()).append('=').append(env.getValue().toString());
      out.println();
    }
    out.flush();
  }

  public static void writeSysDateTime(HttpServletRequest request, HttpServletResponse response) throws Exception {
    response.setContentType(ContentTypes.TEXT_PLAIN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    var out = response.getWriter();

    out.append("OffsetDateTime.now(): ").append(OffsetDateTime.now().toString());
    out.println();
    out.append("ZoneId.systemDefault(): ").append(ZoneId.systemDefault().toString());
    out.println();

    out.flush();
  }

  public static void writeRequestLocales(HttpServletRequest request, HttpServletResponse response) throws Exception {
    response.setContentType(ContentTypes.TEXT_PLAIN);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    var out = response.getWriter();
    out.append("request.getLocales():");
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
