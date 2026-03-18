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

package com.appslandia.plum.defaults;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.appslandia.common.utils.Cidr;
import com.appslandia.common.utils.NetUtils;
import com.appslandia.common.utils.SplitUtils;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.ClientUrlInfo;
import com.appslandia.plum.base.ClientUrlInfoParser;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultClientUrlInfoParser implements ClientUrlInfoParser {

  @Inject
  protected AppConfig appConfig;

  @Inject
  protected AppLogger appLogger;

  private boolean behindProxy;
  private List<Cidr> trustedCidrs;
  private List<String> clientIpHeaders;

  @PostConstruct
  protected void initialize() {
    behindProxy = appConfig.getBool(AppConfig.CONFIG_PROXY_BEHIND, false);

    // PROXIES
    var proxyTrusted = appConfig.getString(AppConfig.CONFIG_PROXY_CIDRS);
    var cidrs = new ArrayList<Cidr>();

    if (proxyTrusted != null) {
      for (String value : SplitUtils.splitByComma(proxyTrusted)) {
        var cidr = cidrOrNull(value);
        if (cidr != null) {
          cidrs.add(cidr);
        } else {
          appLogger.warn("Ignored invalid CIDR in trusted proxies: {0}", value);
        }
      }
    }
    trustedCidrs = Collections.unmodifiableList(cidrs);

    // CLIENT IP HEADERS
    var ipHeaders = appConfig.getString(AppConfig.CONFIG_PROXY_IP_HEADERS);
    clientIpHeaders = (ipHeaders != null) ? List.of(SplitUtils.splitByComma(ipHeaders)) : List.of();

    // LOGGING
    appLogger.info("Configured proxy-behind: ${0}", behindProxy);

    appLogger.info("Configured trusted proxies: ${0}",
        trustedCidrs.stream().map(Cidr::toString).collect(Collectors.joining(", ")));

    appLogger.info("Configured client IP headers: ${0}", clientIpHeaders.stream().collect(Collectors.joining(", ")));
  }

  @Override
  public ClientUrlInfo parse(HttpServletRequest request) {
    var scheme = behindProxy ? ServletUtils.getXFScheme(request) : request.getScheme();
    var host = behindProxy ? ServletUtils.getXFHost(request) : ServletUtils.getHost(request);
    var port = behindProxy ? ServletUtils.getXFPort(request) : request.getServerPort();

    return new ClientUrlInfo(scheme.toLowerCase(Locale.ENGLISH), host.toLowerCase(Locale.ENGLISH), port);
  }

  @Override
  public InetAddress getClientIp(HttpServletRequest request) {
    var remoteAddr = NetUtils.toIpAddress(request.getRemoteAddr());

    // PROXY_BEHIND
    if (!behindProxy) {
      return remoteAddr;
    }

    // PROXIES
    var trusted = false;
    for (Cidr cidr : trustedCidrs) {
      if (cidr.matches(remoteAddr)) {
        trusted = true;
        break;
      }
    }
    if (!trusted) {
      return remoteAddr;
    }

    // CLIENT IP HEADERS
    for (String headerName : clientIpHeaders) {
      if ("X-Forwarded-For".equalsIgnoreCase(headerName)) {
        continue;
      }
      var headerValue = request.getHeader(headerName);
      var addr = NetUtils.toIpAddress(headerValue);
      if (addr != null) {
        return addr;
      }
    }

    // X-FORWARDED-FOR
    var xff = request.getHeader("X-Forwarded-For");
    if (xff != null) {

      for (String xip : SplitUtils.split(xff, ',')) {
        var addr = NetUtils.toIpAddress(xip);
        if (addr != null) {
          return addr;
        }
      }
    }
    return remoteAddr;
  }

  private static Cidr cidrOrNull(String value) {
    try {
      return Cidr.parse(value);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }
}
