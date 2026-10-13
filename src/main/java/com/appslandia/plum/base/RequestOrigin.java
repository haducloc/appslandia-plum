// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.net.InetAddress;
import java.util.Locale;

import com.appslandia.common.utils.Arguments;

/**
 *
 * @author Loc Ha
 *
 */
public class RequestOrigin {

  final String scheme;
  final String host;
  final int port;
  final InetAddress clientIp;

  public RequestOrigin(String scheme, String host, int port, InetAddress clientIp) {
    this.scheme = Arguments.notNull(scheme).toLowerCase(Locale.ROOT);
    this.host = Arguments.notNull(host);
    this.port = port;
    this.clientIp = Arguments.notNull(clientIp);
  }

  public String getScheme() {
    return scheme;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public InetAddress getClientIp() {
    return clientIp;
  }

  public boolean isDefaultPort() {
    return ("https".equals(scheme) && port == 443) || ("http".equals(scheme) && port == 80);
  }
}
