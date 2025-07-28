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

package com.appslandia.plum.base;

import java.util.Map;

import com.appslandia.common.base.FormatProvider;
import com.appslandia.common.base.Language;
import com.appslandia.common.converters.ConverterProvider;

/**
 *
 * @author Loc Ha
 *
 */
public class RequestContext {

  public static final String REQUEST_ATTRIBUTE_ID = "ctx";

  private String contextPath;
  private boolean pathLanguage;

  private FormatProvider formatProvider;
  private Resources resources;
  private ConverterProvider converterProvider;

  private boolean getOrHead;
  private ActionDesc actionDesc;
  private Map<String, String> pathParamMap;

  private String clientId;
  private String module;
  private String cspNonce;
  private PrefCookie prefCookie;

  public boolean isRoute(String controller) {
    if (this.actionDesc == null) {
      return false;
    }
    return this.actionDesc.getController().equalsIgnoreCase(controller);
  }

  public boolean isRoute(String controller, String action) {
    if (this.actionDesc == null) {
      return false;
    }
    return this.actionDesc.getAction().equalsIgnoreCase(action)
        && this.actionDesc.getController().equalsIgnoreCase(controller);
  }

  public String getLanguageId() {
    return this.formatProvider.getLanguage().getId();
  }

  public Language getLanguage() {
    return this.formatProvider.getLanguage();
  }

  public String getContextPath() {
    return this.contextPath;
  }

  protected void setContextPath(String contextPath) {
    this.contextPath = contextPath;
  }

  public boolean isPathLanguage() {
    return this.pathLanguage;
  }

  protected void setPathLanguage(boolean pathLanguage) {
    this.pathLanguage = pathLanguage;
  }

  public FormatProvider getFormatProvider() {
    return this.formatProvider;
  }

  protected void setFormatProvider(FormatProvider formatProvider) {
    this.formatProvider = formatProvider;
  }

  public Resources getResources() {
    return this.resources;
  }

  protected void setResources(Resources resources) {
    this.resources = resources;
  }

  public ConverterProvider getConverterProvider() {
    return this.converterProvider;
  }

  protected void setConverterProvider(ConverterProvider converterProvider) {
    this.converterProvider = converterProvider;
  }

  public boolean isGetOrHead() {
    return this.getOrHead;
  }

  protected void setGetOrHead(boolean getOrHead) {
    this.getOrHead = getOrHead;
  }

  public ActionDesc getActionDesc() {
    return this.actionDesc;
  }

  protected void setActionDesc(ActionDesc actionDesc) {
    this.actionDesc = actionDesc;
  }

  public Map<String, String> getPathParamMap() {
    return this.pathParamMap;
  }

  protected void setPathParamMap(Map<String, String> pathParamMap) {
    this.pathParamMap = pathParamMap;
  }

  public String getClientId() {
    return this.clientId;
  }

  protected void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getModule() {
    return this.module;
  }

  protected void setModule(String module) {
    this.module = module;
  }

  public String getCspNonce() {
    return this.cspNonce;
  }

  protected void setCspNonce(String cspNonce) {
    this.cspNonce = cspNonce;
  }

  public PrefCookie getPrefCookie() {
    return this.prefCookie;
  }

  protected void setPrefCookie(PrefCookie prefCookie) {
    this.prefCookie = prefCookie;
  }

  public String res(String key) {
    return this.resources.get(key);
  }

  public String res(String key, Object p1) {
    return this.resources.get(key, p1);
  }

  public String res(String key, Object p1, Object p2) {
    return this.resources.get(key, p1, p2);
  }

  public String res(String key, Object p1, Object p2, Object p3) {
    return this.resources.get(key, p1, p2, p3);
  }
}
