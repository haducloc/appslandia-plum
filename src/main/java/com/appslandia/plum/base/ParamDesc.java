// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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

import com.appslandia.common.base.Bind;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ParamDesc {

  private Bind bind;
  private Model model;

  private Parameter parameter;
  private boolean pathParam;

  public String getParamName() {
    return ((this.bind != null) && !this.bind.name().isEmpty()) ? this.bind.name() : this.parameter.getName();
  }

  public String getConverter() {
    return ((this.bind != null) && !this.bind.converter().isEmpty()) ? this.bind.converter() : null;
  }

  public String getDefaultValue() {
    return ((this.bind != null) && !this.bind.defaultValue().isEmpty()) ? this.bind.defaultValue() : null;
  }

  public Bind getBind() {
    return this.bind;
  }

  protected void setBind(Bind bind) {
    this.bind = bind;
  }

  public Model getModel() {
    return this.model;
  }

  protected void setModel(Model model) {
    this.model = model;
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
