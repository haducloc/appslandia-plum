// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.UUID;

/**
 *
 * @author Loc Ha
 *
 */
public class SeriesToken {

  private UUID series;
  private String token;

  public UUID getSeries() {
    return series;
  }

  public SeriesToken setSeries(UUID series) {
    this.series = series;
    return this;
  }

  public String getToken() {
    return token;
  }

  public SeriesToken setToken(String token) {
    this.token = token;
    return this;
  }
}
