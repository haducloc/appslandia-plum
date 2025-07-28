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

import java.lang.reflect.Method;
import java.util.List;

/**
 *
 * @author Loc Ha
 *
 */
public class ActionDesc {

  private String controller;
  private String action;
  private Method method;
  private Class<?> controllerClass;

  private List<ParamDesc> paramDescs;
  private List<PathParam> pathParams;
  private int pathParamCount;
  private String module;

  private List<String> allowMethods;
  private String methodsAsString;
  private ConsumeType consumeType;
  private EnableFilters enableFilters;

  private Authorize authorize;
  private CacheControl cacheControl;
  private EnableCors enableCors;

  private EnableEncoding enableEncoding;
  private EnableParts enableParts;
  private EnableAsync enableAsync;

  private EnableEtag enableEtag;
  private EnableCsrf enableCsrf;
  private EnableCaptcha enableCaptcha;
  private EnableJsonError enableJsonError;

  private BypassAuthorization bypassAuthorization;

  public String getController() {
    return this.controller;
  }

  protected void setController(String controller) {
    this.controller = controller;
  }

  public String getAction() {
    return this.action;
  }

  protected void setAction(String action) {
    this.action = action;
  }

  public Method getMethod() {
    return this.method;
  }

  protected void setMethod(Method method) {
    this.method = method;
  }

  public Class<?> getControllerClass() {
    return this.controllerClass;
  }

  protected void setControllerClass(Class<?> controllerClass) {
    this.controllerClass = controllerClass;
  }

  public List<ParamDesc> getParamDescs() {
    return this.paramDescs;
  }

  protected void setParamDescs(List<ParamDesc> paramDescs) {
    this.paramDescs = paramDescs;
  }

  public List<PathParam> getPathParams() {
    return this.pathParams;
  }

  protected void setPathParams(List<PathParam> pathParams) {
    this.pathParams = pathParams;
    this.pathParamCount = ActionDescProvider.getPathParamCount(pathParams);
  }

  public int getPathParamCount() {
    return this.pathParamCount;
  }

  public String getModule() {
    return this.module;
  }

  protected void setModule(String module) {
    this.module = module;
  }

  public List<String> getAllowMethods() {
    return this.allowMethods;
  }

  protected void setAllowMethods(List<String> allowMethods) {
    this.allowMethods = allowMethods;
    this.methodsAsString = String.join(", ", allowMethods);
  }

  public String getMethodsAsString() {
    return this.methodsAsString;
  }

  public ConsumeType getConsumeType() {
    return this.consumeType;
  }

  protected void setConsumeType(ConsumeType consumeType) {
    this.consumeType = consumeType;
  }

  public EnableFilters getEnableFilters() {
    return this.enableFilters;
  }

  protected void setEnableFilters(EnableFilters enableFilters) {
    this.enableFilters = enableFilters;
  }

  public Authorize getAuthorize() {
    return this.authorize;
  }

  protected void setAuthorize(Authorize authorize) {
    this.authorize = authorize;
  }

  public CacheControl getCacheControl() {
    return this.cacheControl;
  }

  protected void setCacheControl(CacheControl cacheControl) {
    this.cacheControl = cacheControl;
  }

  public EnableCors getEnableCors() {
    return this.enableCors;
  }

  protected void setEnableCors(EnableCors enableCors) {
    this.enableCors = enableCors;
  }

  public EnableEncoding getEnableEncoding() {
    return this.enableEncoding;
  }

  protected void setEnableEncoding(EnableEncoding enableEncoding) {
    this.enableEncoding = enableEncoding;
  }

  public EnableParts getEnableParts() {
    return this.enableParts;
  }

  protected void setEnableParts(EnableParts enableParts) {
    this.enableParts = enableParts;
  }

  public EnableAsync getEnableAsync() {
    return this.enableAsync;
  }

  protected void setEnableAsync(EnableAsync enableAsync) {
    this.enableAsync = enableAsync;
  }

  public EnableEtag getEnableEtag() {
    return this.enableEtag;
  }

  protected void setEnableEtag(EnableEtag enableEtag) {
    this.enableEtag = enableEtag;
  }

  public EnableCsrf getEnableCsrf() {
    return this.enableCsrf;
  }

  protected void setEnableCsrf(EnableCsrf enableCsrf) {
    this.enableCsrf = enableCsrf;
  }

  public EnableCaptcha getEnableCaptcha() {
    return this.enableCaptcha;
  }

  protected void setEnableCaptcha(EnableCaptcha enableCaptcha) {
    this.enableCaptcha = enableCaptcha;
  }

  public EnableJsonError getEnableJsonError() {
    return this.enableJsonError;
  }

  protected void setEnableJsonError(EnableJsonError enableJsonError) {
    this.enableJsonError = enableJsonError;
  }

  public BypassAuthorization getBypassAuthorization() {
    return this.bypassAuthorization;
  }

  protected void setBypassAuthorization(BypassAuthorization bypassAuthorization) {
    this.bypassAuthorization = bypassAuthorization;
  }
}
