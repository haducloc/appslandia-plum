// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.SessionCookieConfig;

/**
 *
 * @author Loc Ha
 *
 */
public class MockSessionCookieConfig implements SessionCookieConfig {

  private Map<String, String> attributes = new HashMap<>();

  public MockSessionCookieConfig() {
    setAttribute("name", "JSESSIONID");
    setAttribute("domain", "localhost");

    setAttribute("httpOnly", Boolean.toString(false));
    setAttribute("secure", Boolean.toString(false));
    setAttribute("maxAge", Integer.toString(-1));
  }

  @Override
  public void setName(String name) {
    setAttribute("name", name);
  }

  @Override
  public String getName() {
    return getAttribute("name");
  }

  @Override
  public void setDomain(String domain) {
    setAttribute("domain", domain);
  }

  @Override
  public String getDomain() {
    return getAttribute("domain");
  }

  @Override
  public void setPath(String path) {
    setAttribute("path", path);
  }

  @Override
  public String getPath() {
    return getAttribute("path");
  }

  @Override
  public void setComment(String comment) {
    setAttribute("comment", comment);
  }

  @Override
  public String getComment() {
    return getAttribute("comment");
  }

  @Override
  public void setHttpOnly(boolean httpOnly) {
    setAttribute("httpOnly", Boolean.toString(httpOnly));
  }

  @Override
  public boolean isHttpOnly() {
    return Boolean.parseBoolean(getAttribute("httpOnly"));
  }

  @Override
  public void setSecure(boolean secure) {
    setAttribute("secure", Boolean.toString(secure));
  }

  @Override
  public boolean isSecure() {
    return Boolean.parseBoolean(getAttribute("secure"));
  }

  @Override
  public void setMaxAge(int maxAge) {
    setAttribute("maxAge", Integer.toString(maxAge));
  }

  @Override
  public int getMaxAge() {
    return Integer.parseInt(getAttribute("maxAge"));
  }

  @Override
  public void setAttribute(String name, String value) {
    attributes.put(name, value);
  }

  @Override
  public String getAttribute(String name) {
    return attributes.get(name);
  }

  @Override
  public Map<String, String> getAttributes() {
    return attributes;
  }
}
