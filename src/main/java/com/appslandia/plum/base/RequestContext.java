// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.appslandia.common.base.FormatProvider;
import com.appslandia.common.base.Language;
import com.appslandia.common.base.ToStringBuilder.TSIdHash;
import com.appslandia.common.converters.ConverterProvider;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.CollectionUtils;

/**
 *
 * @author Loc Ha
 *
 */
public class RequestContext {
  public static final String REQUEST_ATTRIBUTE_ID = "ctx";

  private String contextPath;
  private boolean pathLanguage;

  @TSIdHash
  private FormatProvider formatProvider;

  @TSIdHash
  private Resources resources;

  @TSIdHash
  private ConverterProvider converterProvider;

  private boolean getOrHead;

  @TSIdHash
  private ActionDesc actionDesc;

  private Map<String, String> pathParams;

  private RequestOrigin requestOrigin;
  private String module;
  private String nonce;
  private String traceId;
  private String relativePath;
  private PrefCookie prefCookie;

  private AtomicBoolean asyncTimedOut;

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

  public boolean isAsyncTimedOut() {
    Asserts.notNull(asyncTimedOut);
    return asyncTimedOut.get();
  }

  /**
   * Called from {@code AsyncListener.onTimeout(AsyncEvent)}.
   *
   */
  public void markAsyncTimedOut() {
    Asserts.notNull(asyncTimedOut);
    asyncTimedOut.set(true);
  }

  /**
   * Called immediately before {@code request.startAsync(request, response)}.
   *
   */
  public void initAsyncTimedOut() {
    this.asyncTimedOut = new AtomicBoolean(false);
  }

  public String getClientAddress() {
    return this.requestOrigin.getClientIp().getHostAddress();
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
    this.pathParams = CollectionUtils.toUnmodifiableMap(pathParams);
  }

  public RequestOrigin getRequestOrigin() {
    return requestOrigin;
  }

  protected void setRequestOrigin(RequestOrigin requestOrigin) {
    this.requestOrigin = requestOrigin;
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

  public String getTraceId() {
    return traceId;
  }

  protected void setTraceId(String traceId) {
    this.traceId = traceId;
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

  public String res(String key) {
    return resources.get(key);
  }

  public String res(String key, Object... params) {
    return resources.get(key, params);
  }

  public String res(String key, Map<String, Object> params) {
    return resources.get(key, params);
  }

  public String res(ResKey key) {
    return key.resolve(resources);
  }
}
