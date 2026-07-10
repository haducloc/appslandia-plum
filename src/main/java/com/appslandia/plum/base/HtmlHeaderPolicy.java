// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

/**
 *
 * @author Loc Ha
 *
 */
public class HtmlHeaderPolicy extends HeaderPolicy {

  @Override
  public HtmlHeaderPolicy setIncludePaths(String multilinePaths) {
    super.setIncludePaths(multilinePaths);
    return this;
  }

  @Override
  public HtmlHeaderPolicy setExcludePaths(String multilinePaths) {
    super.setIncludePaths(multilinePaths);
    return this;
  }

  @Override
  public HtmlHeaderPolicy setHeader(String name, String value) {
    super.setHeader(name, value);
    return this;
  }

  @Override
  public HtmlHeaderPolicy setHeader(String name, int value) {
    super.setHeader(name, value);
    return this;
  }

  @Override
  public HtmlHeaderPolicy setDateHeader(String name, long timeInMs) {
    super.setDateHeader(name, timeInMs);
    return this;
  }

  @Override
  public HtmlHeaderPolicy setHeaders(String multilineHeaders) {
    super.setHeaders(multilineHeaders);
    return this;
  }

  public HtmlHeaderPolicy setCspPolicy(CspPolicy policy) {
    if (policy != null) {
      policy.registerTo(this);
    }
    return this;
  }
}
