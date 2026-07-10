// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.time.Instant;

/**
 *
 * @author Loc Ha
 *
 */
public class RequestPrincipal extends AuthPrincipal {
  private static final long serialVersionUID = 1L;

  final AuthPrincipal principal;
  final boolean rememberMe;
  final Instant reauthAt;

  public RequestPrincipal(AuthPrincipal principal, boolean rememberMe, boolean reauthentication) {
    super(null, null, null, null);

    this.principal = principal;
    this.rememberMe = rememberMe;
    reauthAt = reauthentication ? Instant.now() : null;
  }

  @Override
  public String getName() {
    return principal.getName();
  }

  @Override
  public String getDisplayName() {
    return principal.getDisplayName();
  }

  @Override
  public Object get(String attributeName) {
    return principal.get(attributeName);
  }

  @Override
  public Object getReq(String attributeName) {
    return principal.getReq(attributeName);
  }

  @Override
  public String getModule() {
    return principal.getModule();
  }

  @Override
  public String getStoreId() {
    return principal.getStoreId();
  }

  @Override
  public String getCallerUniqueId() {
    return principal.getCallerUniqueId();
  }

  @Override
  public boolean isRememberMe() {
    return rememberMe;
  }

  @Override
  public Instant getReauthAt() {
    return reauthAt;
  }
}
