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

import java.lang.reflect.Parameter;

/**
 *
 * @author Loc Ha
 *
 */
public class ParamDesc {

  private BindModel bindModel;
  private BindValue bindValue;

  private Parameter parameter;
  private boolean pathParam;

  public String getParamName() {
    return ((this.bindValue != null) && !this.bindValue.name().isEmpty()) ? this.bindValue.name()
        : this.parameter.getName();
  }

  public String getConverter() {
    return ((this.bindValue != null) && !this.bindValue.conv().isEmpty()) ? this.bindValue.conv() : null;
  }

  public String getDefval() {
    return ((this.bindValue != null) && !this.bindValue.defval().isEmpty()) ? this.bindValue.defval() : null;
  }

  public BindModel getBindModel() {
    return this.bindModel;
  }

  protected void setBindModel(BindModel bindModel) {
    this.bindModel = bindModel;
  }

  public BindValue getBindValue() {
    return this.bindValue;
  }

  protected void setBindValue(BindValue bindValue) {
    this.bindValue = bindValue;
  }

  public Parameter getParameter() {
    return this.parameter;
  }

  protected void setParameter(Parameter parameter) {
    this.parameter = parameter;
  }

  public boolean isPathParam() {
    return this.pathParam;
  }

  protected void setPathParam(boolean pathParam) {
    this.pathParam = pathParam;
  }
}
