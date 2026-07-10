// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.pebble;

import java.io.IOException;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appslandia.plum.base.RequestContext;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.extension.Test;

/**
 *
 * @author Loc Ha
 *
 */
public class DefaultPebbleExtensionProvider extends PebbleExtensionProvider {

  @Override
  public Map<String, Function> getFunctions() {
    Map<String, Function> impls = new HashMap<>();
    impls.put("actionUrl", new ActionUrlFunction());
    impls.put("url", new UrlFunction());

    impls.put("fmtConst", new FmtConstFunction());
    impls.put("fmtString", new FmtStringFunction());
    impls.put("sym", new SymbolFunction());

    registerStaticFunctions(impls);
    return impls;
  }

  @Override
  public Map<String, Test> getTests() {
    Map<String, Test> impls = new HashMap<>();
    return impls;
  }

  static void registerStaticFunctions(Map<String, Function> impls) {

    impls.put("nowMs", new PebbleFunction() {

      @Override
      public List<String> getArgumentNames() {
        return null;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {

        return com.appslandia.plum.facelet.Functions.nowMs();
      }
    });

    impls.put("id", new PebbleFunction() {

      static final List<String> ARGS = List.of("pathExpression");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {

        var path = (String) context.getArgReq("pathExpression");
        return com.appslandia.plum.facelet.Functions.id(path);
      }
    });

    impls.put("toStr", new PebbleFunction() {

      static final List<String> ARGS = List.of("value", "tsLevel");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var value = context.getArg("value");
        var tsLevel = context.getIntReq("tsLevel");

        return com.appslandia.plum.facelet.Functions.toStr(value, tsLevel);
      }
    });

    impls.put("fmtDt", new PebbleFunction() {

      static final List<String> ARGS = List.of("ctx", "value");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var ctx = (RequestContext) context.getArgReq("ctx");
        var value = (Temporal) context.getArg("value");

        return com.appslandia.plum.facelet.Functions.fmtDt(ctx, value);
      }
    });

    impls.put("fmtInt", new PebbleFunction() {

      static final List<String> ARGS = List.of("ctx", "value");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var ctx = (RequestContext) context.getArgReq("ctx");
        var value = (Number) context.getArg("value");

        return com.appslandia.plum.facelet.Functions.fmtInt(ctx, value);
      }
    });

    impls.put("fmtNum", new PebbleFunction() {

      static final List<String> ARGS = List.of("ctx", "value", "fractionDigits");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var ctx = (RequestContext) context.getArgReq("ctx");
        var value = (Number) context.getArg("value");
        var fractionDigits = context.getIntReq("fractionDigits");

        return com.appslandia.plum.facelet.Functions.fmtNum(ctx, value, fractionDigits);
      }
    });

    impls.put("fmtPer", new PebbleFunction() {

      static final List<String> ARGS = List.of("ctx", "value", "fractionDigits");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var ctx = (RequestContext) context.getArgReq("ctx");
        var value = (Number) context.getArg("value");
        var fractionDigits = context.getIntReq("fractionDigits");

        return com.appslandia.plum.facelet.Functions.fmtPer(ctx, value, fractionDigits);
      }
    });

    impls.put("fmtCur", new PebbleFunction() {

      static final List<String> ARGS = List.of("ctx", "value", "fractionDigits");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var ctx = (RequestContext) context.getArgReq("ctx");
        var value = (Number) context.getArg("value");
        var fractionDigits = context.getIntReq("fractionDigits");

        return com.appslandia.plum.facelet.Functions.fmtCur(ctx, value, fractionDigits);
      }
    });

    impls.put("res", new PebbleFunction() {

      static final List<String> ARGS = List.of("ctx", "key");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var ctx = (RequestContext) context.getArgReq("ctx");
        var key = context.getStringReq("key");

        return com.appslandia.plum.facelet.Functions.res(ctx, key);
      }
    });

    impls.put("esc", new PebbleFunction() {

      static final List<String> ARGS = List.of("value");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var value = context.getArg("value");

        return com.appslandia.plum.facelet.Functions.esc(value);
      }
    });

    impls.put("if", new PebbleFunction() {

      static final List<String> ARGS = List.of("test", "value");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var test = context.getBoolReq("test");
        var value = context.getArg("value");

        return com.appslandia.plum.facelet.Functions.if_(test, value);
      }
    });

    impls.put("iif", new PebbleFunction() {

      static final List<String> ARGS = List.of("test", "trueValue", "falseValue");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var test = context.getBoolReq("test");
        var trueValue = context.getArg("trueValue");
        var falseValue = context.getArg("falseValue");

        return com.appslandia.plum.facelet.Functions.iif(test, trueValue, falseValue);
      }
    });

    impls.put("trunc", new PebbleFunction() {

      static final List<String> ARGS = List.of("value", "len");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var value = context.getArg("value");
        var len = context.getIntReq("len");

        return com.appslandia.plum.facelet.Functions.trunc(value, len);
      }
    });

    impls.put("upper", new PebbleFunction() {

      static final List<String> ARGS = List.of("value");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var value = context.getArg("value");

        return com.appslandia.plum.facelet.Functions.upper(value);
      }
    });

    impls.put("lower", new PebbleFunction() {

      static final List<String> ARGS = List.of("value");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var value = context.getArg("value");

        return com.appslandia.plum.facelet.Functions.lower(value);
      }
    });

    impls.put("mask", new PebbleFunction() {

      static final List<String> ARGS = List.of("value", "len");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var value = context.getArg("value");
        var len = context.getIntReq("len");

        return com.appslandia.plum.facelet.Functions.mask(value, len);
      }
    });

    impls.put("maskEnd", new PebbleFunction() {

      static final List<String> ARGS = List.of("value", "len");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var value = context.getArg("value");
        var len = context.getIntReq("len");

        return com.appslandia.plum.facelet.Functions.maskEnd(value, len);
      }
    });

    impls.put("fmtDays", new PebbleFunction() {

      static final List<String> ARGS = List.of("ctx", "value", "zoneId");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var ctx = (RequestContext) context.getArgReq("ctx");
        var value = context.getArg("value");
        var zoneId = context.getArg("zoneId");

        return com.appslandia.plum.facelet.Functions.fmtDays(ctx, value, zoneId);
      }
    });
  }
}
