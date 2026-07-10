// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.pebble;

import java.util.LinkedHashMap;
import java.util.Map;

import com.appslandia.common.base.BoolFormatException;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.ParseUtils;
import com.appslandia.common.utils.STR;
import com.appslandia.plum.base.ModelState;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.tags.TagUtils;
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

  public Map<String, Object> getDynParams() {
    Map<String, Object> dynParams = new LinkedHashMap<>();
    for (Map.Entry<String, Object> param : arguments.entrySet()) {
      if (TagUtils.isDynamicParameter(param.getKey())) {

        dynParams.put(TagUtils.getDynParamName(param.getKey()), param.getValue());
      }
    }
    return dynParams;
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
    return arguments;
  }

  public PebbleTemplate getTemplate() {
    return template;
  }

  public EvaluationContext getEvaluationContext() {
    return evaluationContext;
  }

  public Object getVar(String name) {
    return evaluationContext.getVariable(name);
  }

  public Object getVarReq(String name) {
    var value = evaluationContext.getVariable(name);
    if (value == null) {
      throw new IllegalStateException(STR.fmt("The variable '{}' is required.", name));
    }
    return value;
  }

  public Object getArg(String name) {
    return arguments.get(name);
  }

  public Object getArgReq(String name) {
    var value = arguments.get(name);
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

  public Map<String, Object> getMap(String name) {
    var value = getArg(name);
    if (value == null) {
      return null;
    }
    if (value instanceof Map map) {
      return ObjectUtils.cast(map);
    }
    throw new IllegalStateException(STR.fmt("Failed to getMap('{}')", name));
  }

  public Map<String, Object> getMapReq(String name) {
    var val = getMap(name);
    Asserts.notNull(val);
    return val;
  }
}
