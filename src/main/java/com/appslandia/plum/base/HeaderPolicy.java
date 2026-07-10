// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.appslandia.common.base.DeployEnv;
import com.appslandia.common.base.Params;
import com.appslandia.common.json.JsonIgnore;
import com.appslandia.common.utils.ConfigUtils;
import com.appslandia.common.utils.STR;
import com.appslandia.common.utils.StringFormat;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public class HeaderPolicy extends AntPathPolicy {

  protected Map<EnvHeader, String> headers = new LinkedHashMap<>();

  @JsonIgnore
  private final transient ConcurrentMap<EnvHeader, StringFormat> headerFmtMap = new ConcurrentHashMap<>();

  @Override
  protected void init() throws Exception {
    super.init();

    headers = Collections.unmodifiableMap(headers);
  }

  public boolean writePolicy(HttpServletRequest request, HttpServletResponse response, RequestContext requestContext) {
    initialize();

    final var deployEnv = DeployEnv.getCurrent();
    for (Map.Entry<EnvHeader, String> entry : headers.entrySet()) {

      var envHeader = entry.getKey();
      var value = entry.getValue();

      if (envHeader.getEnv() != null && !deployEnv.isEnv(envHeader.getEnv())) {
        continue;
      }

      // HSTS
      if ("Strict-Transport-Security".equalsIgnoreCase(envHeader.getName())) {
        if ("https".equals(requestContext.getRequestOrigin().getScheme())) {
          response.setHeader(envHeader.getName(), value);
        }
        continue;
      }

      // CSP Related
      var isCspHeader = "Content-Security-Policy".equalsIgnoreCase(envHeader.getName());
      if (isCspHeader || "Reporting-Endpoints".equalsIgnoreCase(envHeader.getName())) {

        var params = new Params().set("__nonce", requestContext.getNonce()).set("__mvc_base_url",
            ServletUtils.getMvcBaseUrl(request));

        var headerFmt = headerFmtMap.computeIfAbsent(envHeader, header -> STR.toStringFormat(value));
        var fmtVal = headerFmt.format(params);

        if (isCspHeader) {
          var appConfig = ServletUtils.getAppScoped(request, AppConfig.class);

          if (appConfig.getBool(AppConfig.CONFIG_CSP_REPORT_ONLY)) {
            response.setHeader("Content-Security-Policy-Report-Only", fmtVal);
          } else {
            response.setHeader(envHeader.getName(), fmtVal);
          }
        } else {
          response.setHeader(envHeader.getName(), fmtVal);
        }
        continue;
      }

      // Others
      response.setHeader(envHeader.getName(), value);
    }
    return true;
  }

  public boolean containsHeader(String headerName) {
    if (headers.containsKey(new EnvHeader(null, headerName))) {
      return true;
    }
    var deployEnv = DeployEnv.getCurrent();
    return headers.containsKey(new EnvHeader(deployEnv.getName(), headerName));
  }

  @Override
  public HeaderPolicy setName(String name) {
    super.setName(name);
    return this;
  }

  @Override
  public HeaderPolicy includeAll() {
    super.includeAll();
    return this;
  }

  @Override
  public HeaderPolicy setIncludePaths(String multilinePaths) {
    super.setIncludePaths(multilinePaths);
    return this;
  }

  @Override
  public HeaderPolicy setExcludePaths(String multilinePaths) {
    super.setExcludePaths(multilinePaths);
    return this;
  }

  public HeaderPolicy setHeader(String name, String value) {
    assertNotInitialized();
    var envHeader = new EnvHeader(null, name);
    headers.put(envHeader, value);
    return this;
  }

  public HeaderPolicy setHeader(String name, int value) {
    return setHeader(name, Integer.toString(value));
  }

  public HeaderPolicy setDateHeader(String name, long timeInMs) {
    var formattedDate = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC)
        .format(Instant.ofEpochMilli(timeInMs));

    return setHeader(name, formattedDate);
  }

  public HeaderPolicy setHeader(EnvHeader envHeader, String value) {
    assertNotInitialized();
    headers.put(envHeader, value);
    return this;
  }

  public HeaderPolicy setHeaders(String multilineHeaders) {
    assertNotInitialized();

    var lines = ConfigUtils.toMultilineValues(multilineHeaders);
    for (String headerKeyVal : lines) {

      var kv = ConfigUtils.splitPair(headerKeyVal, ':');
      if (kv == null) {
        continue;
      }

      var envHeader = EnvHeader.parse(kv[0]);
      if (envHeader == null) {
        continue;
      }

      if (kv[1] != null) {
        headers.put(envHeader, kv[1]);
      } else {
        headers.remove(envHeader);
      }
    }
    return this;
  }

  public HeaderPolicy setDirectiveHeader(String name, String multilineValue, Map<String, String> envParams,
      char directiveSeparator) {
    assertNotInitialized();

    if (envParams == null || envParams.isEmpty()) {
      var envHeader = new EnvHeader(null, name);

      var value = ConfigUtils.toSinglelineValues(multilineValue, directiveSeparator);
      headers.put(envHeader, value);
    } else {

      for (Map.Entry<String, String> envParam : envParams.entrySet()) {
        var params = ConfigUtils.toPairMap(envParam.getValue(), ':');

        var value = STR.format(multilineValue, (pname, expr) -> {
          var val = params.get(pname);
          return val != null ? val : expr;
        });
        value = ConfigUtils.toSinglelineValues(value, directiveSeparator);

        var envHeader = new EnvHeader(envParam.getKey(), name);
        headers.put(envHeader, value);
      }
    }
    return this;
  }
}
