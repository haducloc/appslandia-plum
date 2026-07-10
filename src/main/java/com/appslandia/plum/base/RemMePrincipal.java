// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.Serializable;

/**
 *
 * @author Loc Ha
 *
 */
public class RemMePrincipal implements Serializable {
  private static final long serialVersionUID = 1L;

  final String module;
  final String seriesToken;
  final String identity;
  final int maxAge;

  public RemMePrincipal(String module, String seriesToken, String identity, int maxAge) {
    this.module = module;
    this.seriesToken = seriesToken;
    this.identity = identity;
    this.maxAge = maxAge;
  }

  public String getModule() {
    return module;
  }

  public String getSeriesToken() {
    return seriesToken;
  }

  public String getIdentity() {
    return identity;
  }

  public int getMaxAge() {
    return maxAge;
  }
}
