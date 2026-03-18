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

import java.net.InetAddress;
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
  private Map<String, String> pathParams;

  private ClientUrlInfo clientUrlInfo;
  private String module;
  private String nonce;
  private String relativePath;
  private PrefCookie prefCookie;
  private InetAddress clientAddress;

  public boolean isRoute(String controller) {
    if (actionDesc == null) {
      return false;
    }
    return actionDesc.getController().equalsIgnoreCase(controller);
  }

  public boolean isRoute(String controller, String action) {
    if (actionDesc == null) {
      return false;
    }
    return actionDesc.getAction().equalsIgnoreCase(action) && actionDesc.getController().equalsIgnoreCase(controller);
  }

  public boolean isEnableJsonError() {
    if (actionDesc == null) {
      return false;
    }
    return actionDesc.getEnableJsonError() != null;
  }

  public String getClientIp() {
    return getClientAddress().getHostAddress();
  }

  public String getLanguageId() {
    return getLanguage().getId();
  }

  public Language getLanguage() {
    return formatProvider.getLanguage();
  }

  public String getContextPath() {
    return contextPath;
  }

  protected void setContextPath(String contextPath) {
    this.contextPath = contextPath;
  }

  public boolean isPathLanguage() {
    return pathLanguage;
  }

  protected void setPathLanguage(boolean pathLanguage) {
    this.pathLanguage = pathLanguage;
  }

  public FormatProvider getFormatProvider() {
    return formatProvider;
  }

  protected void setFormatProvider(FormatProvider formatProvider) {
    this.formatProvider = formatProvider;
  }

  public Resources getResources() {
    return resources;
  }

  protected void setResources(Resources resources) {
    this.resources = resources;
  }

  public ConverterProvider getConverterProvider() {
    return converterProvider;
  }

  protected void setConverterProvider(ConverterProvider converterProvider) {
    this.converterProvider = converterProvider;
  }

  public boolean isGetOrHead() {
    return getOrHead;
  }

  protected void setGetOrHead(boolean getOrHead) {
    this.getOrHead = getOrHead;
  }

  public ActionDesc getActionDesc() {
    return actionDesc;
  }

  protected void setActionDesc(ActionDesc actionDesc) {
    this.actionDesc = actionDesc;
  }

  public Map<String, String> getPathParams() {
    return pathParams;
  }

  protected void setPathParams(Map<String, String> pathParams) {
    this.pathParams = pathParams;
  }

  public ClientUrlInfo getClientUrlInfo() {
    return clientUrlInfo;
  }

  protected void setClientUrlInfo(ClientUrlInfo clientUrlInfo) {
    this.clientUrlInfo = clientUrlInfo;
  }

  public String getModule() {
    return module;
  }

  protected void setModule(String module) {
    this.module = module;
  }

  public String getNonce() {
    return nonce;
  }

  protected void setNonce(String nonce) {
    this.nonce = nonce;
  }

  public String getRelativePath() {
    return relativePath;
  }

  protected void setRelativePath(String relativePath) {
    this.relativePath = relativePath;
  }

  public PrefCookie getPrefCookie() {
    return prefCookie;
  }

  protected void setPrefCookie(PrefCookie prefCookie) {
    this.prefCookie = prefCookie;
  }

  public InetAddress getClientAddress() {
    return clientAddress;
  }

  protected void setClientAddress(InetAddress clientAddress) {
    this.clientAddress = clientAddress;
  }

  public String res(String key) {
    return resources.get(key);
  }

  public String res(String key, Object p1) {
    return resources.get(key, p1);
  }

  public String res(String key, Object p1, Object p2) {
    return resources.get(key, p1, p2);
  }

  public String res(String key, Object p1, Object p2, Object p3) {
    return resources.get(key, p1, p2, p3);
  }
}
