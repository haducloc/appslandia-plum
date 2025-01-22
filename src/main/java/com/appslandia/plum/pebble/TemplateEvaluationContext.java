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

package com.appslandia.plum.pebble;

import java.util.Map;
import java.util.stream.Collectors;

import com.appslandia.common.utils.ParseUtils;
import com.appslandia.common.utils.STR;
import com.appslandia.plum.base.ModelState;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.jsp.TagUtils;
import com.appslandia.plum.utils.ServletUtils;

import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import jakarta.el.ELProcessor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@SuppressWarnings("unchecked")
public class TemplateEvaluationContext {

  final Map<String, Object> arguments;
  final PebbleTemplate template;
  final EvaluationContext evaluationContext;

  public TemplateEvaluationContext(Map<String, Object> arguments, PebbleTemplate template,
      EvaluationContext evaluationContext) {
    this.arguments = arguments;
    this.template = template;
    this.evaluationContext = evaluationContext;
  }

  public Map<String, Object> parseParameters() {
    return this.arguments.entrySet().stream().filter(entry -> TagUtils.isForParameter(entry.getKey()))
        .collect(Collectors.toMap(e -> TagUtils.getParameterName(e.getKey()), e -> e.getValue()));
  }

  public Object evaluate(String expression) {
    return getELProcessor().eval(expression);
  }

  public HttpServletRequest getRequest() {
    return getVarReq(PebbleUtils.VARIABLE_REQUEST);
  }

  public HttpServletResponse getResponse() {
    return getVarReq(PebbleUtils.VARIABLE_RESPONSE);
  }

  public RequestContext getRequestContext() {
    return getVarReq(PebbleUtils.VARIABLE_REQUEST_CONTEXT);
  }

  public ELProcessor getELProcessor() {
    return getVarReq(PebbleUtils.VARIABLE_EL_PROCESSOR);
  }

  public ModelState getModelState() {
    return ServletUtils.getModelState(getRequest());
  }

  public Map<String, Object> getArguments() {
    return this.arguments;
  }

  public PebbleTemplate getTemplate() {
    return this.template;
  }

  public EvaluationContext getEvaluationContext() {
    return this.evaluationContext;
  }

  public <T> T getArg(String name) {
    return (T) this.arguments.get(name);
  }

  public <T> T getArg(String name, T defaultValue) {
    T value = (T) this.arguments.get(name);
    return (value != null) ? value : defaultValue;
  }

  public <T> T getArgReq(String name) {
    T value = (T) this.arguments.get(name);
    if (value == null) {
      throw new IllegalStateException(STR.fmt("The argument '{}' is required.", name));
    }
    return value;
  }

  public <T> T getVar(String name) {
    return (T) this.evaluationContext.getVariable(name);
  }

  public <T> T getVarReq(String name) {
    T value = (T) this.evaluationContext.getVariable(name);
    if (value == null) {
      throw new IllegalStateException(STR.fmt("The variable '{}' is required.", name));
    }
    return value;
  }

  // Boolean
  private boolean toBool(Object value) {
    if (value.getClass() == Boolean.class) {
      return ((Boolean) value).booleanValue();
    }
    return ParseUtils.parseBool(value.toString());
  }

  public boolean getBool(String name) {
    Object value = getArgReq(name);
    return toBool(value);
  }

  public boolean getBool(String name, boolean defaultValue) {
    Object value = getArg(name);
    return (value != null) ? toBool(value) : defaultValue;
  }

  public Boolean getBoolObj(String name) {
    Object value = getArg(name);
    return (value != null) ? toBool(value) : null;
  }

  // Integer
  private int toInt(Object value) {
    if (value instanceof Number) {
      return ((Number) value).intValue();
    }
    return ParseUtils.parseInt(value.toString());
  }

  public int getInt(String name) {
    Object value = getArgReq(name);
    return toInt(value);
  }

  public int getInt(String name, int defaultValue) {
    Object value = getArg(name);
    return (value != null) ? toInt(value) : defaultValue;
  }

  public Integer getIntObj(String name) {
    Object value = getArg(name);
    return (value != null) ? toInt(value) : null;
  }

  // Long
  private long toLong(Object value) {
    if (value instanceof Number) {
      return ((Number) value).longValue();
    }
    return ParseUtils.parseLong(value.toString());
  }

  public long getLong(String name) {
    Object value = getArgReq(name);
    return toLong(value);
  }

  public long getLong(String name, long defaultValue) {
    Object value = getArg(name);
    return (value != null) ? toLong(value) : defaultValue;
  }

  public Long getLongObj(String name) {
    Object value = getArg(name);
    return (value != null) ? toLong(value) : null;
  }

  // Double
  private double toDouble(Object value) {
    if (value instanceof Number) {
      return ((Number) value).doubleValue();
    }
    return ParseUtils.parseDouble(value.toString());
  }

  public double getDouble(String name) {
    Object value = getArgReq(name);
    return toDouble(value);
  }

  public double getDouble(String name, double defaultValue) {
    Object value = getArg(name);
    return (value != null) ? toDouble(value) : defaultValue;
  }

  public Double getDoubleObj(String name) {
    Object value = getArg(name);
    return (value != null) ? toDouble(value) : null;
  }
}
