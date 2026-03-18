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

  private List<String> httpMethods;
  private String methodsAsString;
  private ConsumeType consumeType;

  private Authorize authorize;
  private EnableParts enableParts;
  private EnableAsync enableAsync;

  private EnableCsrf enableCsrf;
  private EnableCaptcha enableCaptcha;
  private EnableJsonError enableJsonError;

  public String getController() {
    return controller;
  }

  protected void setController(String controller) {
    this.controller = controller;
  }

  public String getAction() {
    return action;
  }

  protected void setAction(String action) {
    this.action = action;
  }

  public Method getMethod() {
    return method;
  }

  protected void setMethod(Method method) {
    this.method = method;
  }

  public Class<?> getControllerClass() {
    return controllerClass;
  }

  protected void setControllerClass(Class<?> controllerClass) {
    this.controllerClass = controllerClass;
  }

  public List<ParamDesc> getParamDescs() {
    return paramDescs;
  }

  protected void setParamDescs(List<ParamDesc> paramDescs) {
    this.paramDescs = paramDescs;
  }

  public List<PathParam> getPathParams() {
    return pathParams;
  }

  protected void setPathParams(List<PathParam> pathParams) {
    this.pathParams = pathParams;
    pathParamCount = ActionDescProvider.getPathParamCount(pathParams);
  }

  public int getPathParamCount() {
    return pathParamCount;
  }

  public String getModule() {
    return module;
  }

  protected void setModule(String module) {
    this.module = module;
  }

  public List<String> getHttpMethods() {
    return httpMethods;
  }

  protected void setHttpMethods(List<String> httpMethods) {
    this.httpMethods = httpMethods;
    methodsAsString = String.join(", ", httpMethods);
  }

  public String getMethodsAsString() {
    return methodsAsString;
  }

  public ConsumeType getConsumeType() {
    return consumeType;
  }

  protected void setConsumeType(ConsumeType consumeType) {
    this.consumeType = consumeType;
  }

  public Authorize getAuthorize() {
    return authorize;
  }

  protected void setAuthorize(Authorize authorize) {
    this.authorize = authorize;
  }

  public EnableParts getEnableParts() {
    return enableParts;
  }

  protected void setEnableParts(EnableParts enableParts) {
    this.enableParts = enableParts;
  }

  public EnableAsync getEnableAsync() {
    return enableAsync;
  }

  protected void setEnableAsync(EnableAsync enableAsync) {
    this.enableAsync = enableAsync;
  }

  public EnableCsrf getEnableCsrf() {
    return enableCsrf;
  }

  protected void setEnableCsrf(EnableCsrf enableCsrf) {
    this.enableCsrf = enableCsrf;
  }

  public EnableCaptcha getEnableCaptcha() {
    return enableCaptcha;
  }

  protected void setEnableCaptcha(EnableCaptcha enableCaptcha) {
    this.enableCaptcha = enableCaptcha;
  }

  public EnableJsonError getEnableJsonError() {
    return enableJsonError;
  }

  protected void setEnableJsonError(EnableJsonError enableJsonError) {
    this.enableJsonError = enableJsonError;
  }
}
