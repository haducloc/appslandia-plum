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

  @Deprecated
  @Override
  public void setComment(String comment) {
    setAttribute("comment", comment);
  }

  @Deprecated
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
    this.attributes.put(name, value);
  }

  @Override
  public String getAttribute(String name) {
    return this.attributes.get(name);
  }

  @Override
  public Map<String, String> getAttributes() {
    return this.attributes;
  }
}
