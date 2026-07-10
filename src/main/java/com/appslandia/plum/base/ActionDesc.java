// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.lang.reflect.Method;
import java.util.List;

import com.appslandia.common.utils.CollectionUtils;

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
  private EnablePart enablePart;
  private EnableAsync enableAsync;

  private EnableCsrf enableCsrf;
  private EnableEtag enableEtag;
  private EnableCompress enableCompress;

  private EnableCors enableCors;
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
    this.paramDescs = CollectionUtils.toUnmodifiableList(paramDescs);
  }

  public List<PathParam> getPathParams() {
    return pathParams;
  }

  protected void setPathParams(List<PathParam> pathParams) {
    this.pathParams = CollectionUtils.toUnmodifiableList(pathParams);
    pathParamCount = ActionDescProvider.getPathParamCount(this.pathParams);
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
    this.httpMethods = CollectionUtils.toUnmodifiableList(httpMethods);
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

  public EnablePart getEnablePart() {
    return enablePart;
  }

  protected void setEnablePart(EnablePart enablePart) {
    this.enablePart = enablePart;
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

  public EnableEtag getEnableEtag() {
    return enableEtag;
  }

  protected void setEnableEtag(EnableEtag enableEtag) {
    this.enableEtag = enableEtag;
  }

  public EnableCompress getEnableCompress() {
    return enableCompress;
  }

  protected void setEnableCompress(EnableCompress enableCompress) {
    this.enableCompress = enableCompress;
  }

  public EnableCors getEnableCors() {
    return enableCors;
  }

  protected void setEnableCors(EnableCors enableCors) {
    this.enableCors = enableCors;
  }

  public EnableJsonError getEnableJsonError() {
    return enableJsonError;
  }

  protected void setEnableJsonError(EnableJsonError enableJsonError) {
    this.enableJsonError = enableJsonError;
  }
}
