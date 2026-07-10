// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.appslandia.common.base.InitializingObject;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.IpNetwork;
import com.appslandia.common.utils.NetUtils;
import com.appslandia.common.utils.SplitUtils;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.utils.HttpSecurityUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class RequestOriginParser extends InitializingObject {

  public static final String CONFIG_BEHIND_PROXIES = RequestOriginParser.class.getName() + ".behind_proxies";
  public static final String CONFIG_KNOWN_PROXIES = RequestOriginParser.class.getName() + ".known_proxies";
  public static final String CONFIG_KNOWN_NETWORKS = RequestOriginParser.class.getName() + ".known_networks";
  public static final String CONFIG_ALLOWED_HOSTS = RequestOriginParser.class.getName() + ".allowed_hosts";

  protected static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
  protected static final String HEADER_X_FORWARDED_PROTO = "X-Forwarded-Proto";
  protected static final String HEADER_X_FORWARDED_HOST = "X-Forwarded-Host";
  protected static final String HEADER_X_FORWARDED_PORT = "X-Forwarded-Port";

  @Inject
  protected AppConfig appConfig;

  protected boolean behindProxies;
  protected Set<InetAddress> knownProxies;
  protected Set<IpNetwork> knownNetworks;
  protected Set<String> allowedHosts;

  @Override
  protected void init() throws Exception {
    // behindProxies
    this.behindProxies = appConfig.getBool(CONFIG_BEHIND_PROXIES, false);

    // knownProxies
    var knownProxies = new HashSet<InetAddress>();
    for (var proxy : appConfig.getStringArray(CONFIG_KNOWN_PROXIES)) {
      var ip = NetUtils.toIpAddress(proxy);
      Arguments.notNull(ip, "Invalid known proxy: " + proxy);

      knownProxies.add(ip);
    }
    this.knownProxies = Collections.unmodifiableSet(knownProxies);

    // knownNetworks
    var knownNetworks = new HashSet<IpNetwork>();
    for (var network : appConfig.getStringArray(CONFIG_KNOWN_NETWORKS)) {
      var ipNetwork = IpNetwork.parse(network);
      Arguments.notNull(ipNetwork, "Invalid known network: " + network);

      knownNetworks.add(ipNetwork);
    }
    this.knownNetworks = Collections.unmodifiableSet(knownNetworks);

    // allowedHosts
    var allowedHosts = new HashSet<String>();
    for (var host : appConfig.getStringArray(CONFIG_ALLOWED_HOSTS)) {
      allowedHosts.add(host.toLowerCase(Locale.ROOT));
    }
    this.allowedHosts = Collections.unmodifiableSet(allowedHosts);
  }

  public RequestOrigin parse(HttpServletRequest request) {
    this.initialize();

    var remoteIp = NetUtils.toIpAddress(request.getRemoteAddr());
    Asserts.notNull(remoteIp);

    if (this.behindProxies && isTrustedProxy(remoteIp)) {
      return doParse(request, remoteIp);
    }

    var scheme = request.getScheme();
    var host = request.getServerName();
    var port = request.getServerPort();

    return new RequestOrigin(scheme, host, port, remoteIp);
  }

  protected RequestOrigin doParse(HttpServletRequest request, InetAddress remoteIp) {

    var scheme = getFirstHeaderValue(request, HEADER_X_FORWARDED_PROTO);
    if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
      scheme = request.getScheme();
    }

    var host = request.getServerName();
    var port = request.getServerPort();

    var hostHeader = getFirstHeaderValue(request, HEADER_X_FORWARDED_HOST);
    if (hostHeader == null) {
      hostHeader = request.getHeader("Host");
    }

    // Sanitize hostHeader
    hostHeader = HttpSecurityUtils.sanitizeHostHeaderValue(hostHeader);

    if (hostHeader != null) {
      var parsed = parseHostAndPort(hostHeader, scheme);

      if (parsed != null && isAllowedHost(parsed.host)) {
        host = parsed.host;
        port = parsed.port;
      }
    }

    var forwardedPort = parseForwardedPort(request);
    if (forwardedPort > 0) {
      port = forwardedPort;
    }

    var clientIp = parseClientIp(request);
    if (clientIp == null) {
      clientIp = remoteIp;
    }

    return new RequestOrigin(scheme.toLowerCase(Locale.ROOT), host, port, clientIp);
  }

  protected InetAddress parseClientIp(HttpServletRequest request) {
    var value = StringUtils.trimToNull(request.getHeader(HEADER_X_FORWARDED_FOR));
    if (value == null) {
      return null;
    }

    var ips = SplitUtils.splitByComma(value);

    for (var i = ips.length - 1; i >= 0; i--) {
      var ip = NetUtils.toIpAddress(ips[i]);

      if (ip != null && !isTrustedProxy(ip)) {
        return ip;
      }
    }

    return null;
  }

  protected boolean isTrustedProxy(InetAddress ip) {
    if (this.knownProxies.contains(ip)) {
      return true;
    }
    for (var network : this.knownNetworks) {
      if (network.contains(ip)) {
        return true;
      }
    }
    return false;
  }

  protected boolean isAllowedHost(String parsedHost) {
    if (this.allowedHosts.isEmpty()) {
      return true;
    }
    return this.allowedHosts.contains(parsedHost);
  }

  protected static String getFirstHeaderValue(HttpServletRequest request, String name) {
    var value = StringUtils.trimToNull(request.getHeader(name));
    if (value == null) {
      return null;
    }

    var idx = value.indexOf(',');
    if (idx >= 0) {
      value = value.substring(0, idx);
    }
    return StringUtils.trimToNull(value);
  }

  protected static int parseForwardedPort(HttpServletRequest request) {
    var value = getFirstHeaderValue(request, HEADER_X_FORWARDED_PORT);
    if (value == null) {
      return -1;
    }

    try {
      var port = Integer.parseInt(value);
      return (port > 0 && port <= 65535) ? port : -1;

    } catch (NumberFormatException ex) {
      return -1;
    }
  }

  protected static HostAndPort parseHostAndPort(String hostHeader, String scheme) {
    var host = hostHeader;
    var port = defaultPort(scheme);

    if (hostHeader.startsWith("[")) {
      // IPv6 host: [ipv6]:port
      var endBracket = hostHeader.indexOf(']');
      if (endBracket < 0) {
        return null;
      }

      host = hostHeader.substring(1, endBracket);

      if (endBracket + 1 < hostHeader.length()) {
        if (hostHeader.charAt(endBracket + 1) != ':') {
          return null;
        }

        var portText = hostHeader.substring(endBracket + 2);
        try {
          port = Integer.parseInt(portText);

        } catch (NumberFormatException ex) {
          return null;
        }
      }

    } else {
      var firstColon = hostHeader.indexOf(':');
      var lastColon = hostHeader.lastIndexOf(':');

      // Reject unbracketed IPv6 addresses.
      // IPv6 with port must use brackets: [::1]:8080
      if (firstColon != lastColon || firstColon == 0) {
        return null;
      }

      if (firstColon > 0) {
        host = hostHeader.substring(0, firstColon);

        try {
          port = Integer.parseInt(hostHeader.substring(firstColon + 1));

        } catch (NumberFormatException ex) {
          return null;
        }
      }
    }

    host = StringUtils.trimToNull(host);
    if (host == null || port <= 0 || port > 65535) {
      return null;
    }

    // Normalize trailing dot: example.com. -> example.com
    while (host.endsWith(".")) {
      host = host.substring(0, host.length() - 1);
    }

    host = StringUtils.trimToNull(host);
    return (host != null) ? new HostAndPort(host.toLowerCase(Locale.ROOT), port) : null;
  }

  protected static int defaultPort(String scheme) {
    return "https".equalsIgnoreCase(scheme) ? 443 : 80;
  }

  protected record HostAndPort(String host, int port) {
  }
}
