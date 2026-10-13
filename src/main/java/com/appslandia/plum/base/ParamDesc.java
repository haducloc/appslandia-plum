// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.lang.reflect.Parameter;

/**
 *
 * @author Loc Ha
 *
 */
public class ParamDesc {

  private BindModel bindModel;
  private BindField bindField;

  private Parameter parameter;
  private boolean pathParam;

  public String getParamName() {
    return ((bindField != null) && !bindField.name().isEmpty()) ? bindField.name() : parameter.getName();
  }

  public String getConverter() {
    return ((bindField != null) && !bindField.converter().isEmpty()) ? bindField.converter() : null;
  }

  public String getDefaultValue() {
    return ((bindField != null) && !bindField.defaultValue().isEmpty()) ? bindField.defaultValue() : null;
  }

  public BindModel getBindModel() {
    return bindModel;
  }

  protected void setBindModel(BindModel bindModel) {
    this.bindModel = bindModel;
  }

  public BindField getBindField() {
    return bindField;
  }

  protected void setBindField(BindField bindField) {
    this.bindField = bindField;
  }

  public Parameter getParameter() {
    return parameter;
  }

  protected void setParameter(Parameter parameter) {
    this.parameter = parameter;
  }

  public boolean isPathParam() {
    return pathParam;
  }

  protected void setPathParam(boolean pathParam) {
    this.pathParam = pathParam;
  }
}
