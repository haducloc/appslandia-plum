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

package com.appslandia.plum.pebble;

import java.util.Map;

import com.appslandia.common.base.BoolFormatException;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ParseUtils;
import com.appslandia.common.utils.STR;
import com.appslandia.plum.base.ModelState;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.utils.ServletUtils;

import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
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

  public HttpServletRequest getRequest() {
    return (HttpServletRequest) getVarReq(PebbleUtils.VARIABLE_REQUEST);
  }

  public HttpServletResponse getResponse() {
    return (HttpServletResponse) getVarReq(PebbleUtils.VARIABLE_RESPONSE);
  }

  public RequestContext getRequestContext() {
    return (RequestContext) getVarReq(PebbleUtils.VARIABLE_REQUEST_CONTEXT);
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

  public Object getVar(String name) {
    return this.evaluationContext.getVariable(name);
  }

  public Object getVarReq(String name) {
    var value = this.evaluationContext.getVariable(name);
    if (value == null) {
      throw new IllegalStateException(STR.fmt("The variable '{}' is required.", name));
    }
    return value;
  }

  public Object getArg(String name) {
    return this.arguments.get(name);
  }

  public Object getArgReq(String name) {
    var value = this.arguments.get(name);
    if (value == null) {
      throw new IllegalStateException(STR.fmt("The argument '{}' is required.", name));
    }
    return value;
  }

  // ===== INT =====

  public Integer getIntOpt(String name) throws NumberFormatException {
    var value = getArg(name);
    return switch (value) {
    case null -> null;
    case Integer i -> i;
    case Number num -> num.intValue();
    case String str -> ParseUtils.parseIntOpt(str);
    default -> throw new NumberFormatException(STR.fmt("Couldn't convert '{}' to java.lang.Integer.", value));
    };
  }

  public int getInt(String name, int defaultValue) {
    var val = getIntOpt(name);
    return (val != null) ? val : defaultValue;
  }

  public int getIntReq(String name) throws NumberFormatException {
    var val = getIntOpt(name);
    Asserts.notNull(val);
    return val;
  }

  // ===== BOOLEAN =====

  public Boolean getBoolOpt(String name) throws BoolFormatException {
    var value = getArg(name);
    return switch (value) {
    case null -> null;
    case Boolean b -> b;
    case String str -> ParseUtils.parseBoolOpt(str);
    default -> throw new BoolFormatException(STR.fmt("Couldn't convert '{}' to java.lang.Boolean.", value));
    };
  }

  public boolean getBool(String name, boolean defaultValue) {
    var val = getBoolOpt(name);
    return (val != null) ? val : defaultValue;
  }

  public boolean getBoolReq(String name) throws BoolFormatException {
    var val = getBoolOpt(name);
    Asserts.notNull(val);
    return val;
  }

  // ===== STRING =====

  public String getString(String name) {
    var value = getArg(name);
    if (value == null) {
      return null;
    }
    if (value instanceof String str) {
      return str;
    }
    return value.toString();
  }

  public String getString(String name, String defaultValue) {
    var val = getString(name);
    return (val != null) ? val : defaultValue;
  }

  public String getStringReq(String name) {
    var val = getString(name);
    Asserts.notNull(val);
    return val;
  }

  // ===== MAP =====

  @SuppressWarnings("unchecked")
  public Map<String, Object> getMap(String name) {
    var value = getArg(name);
    if (value == null) {
      return null;
    }
    if (value instanceof Map map) {
      return map;
    }
    throw new IllegalStateException(STR.fmt("Failed to getMap('{}')", name));
  }

  public Map<String, Object> getMapReq(String name) {
    var val = getMap(name);
    Asserts.notNull(val);
    return val;
  }
}
