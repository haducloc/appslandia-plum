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

    impls.put("const", new ConstFunction());
    impls.put("fmtGroup", new FmtGroupFunction());
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

    impls.put("nowms", new PebbleFunction() {

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

    impls.put("ifcls", new PebbleFunction() {

      static final List<String> ARGS = List.of("test", "cssClass");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var test = context.getBoolReq("test");
        var cssClass = context.getString("cssClass");

        return com.appslandia.plum.facelet.Functions.ifCls(test, cssClass);
      }
    });

    impls.put("tostr", new PebbleFunction() {

      static final List<String> ARGS = List.of("value", "tsDepthLevel");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var value = context.getArg("value");
        var tsDepthLevel = context.getIntReq("tsDepthLevel");

        return com.appslandia.plum.facelet.Functions.toStr(value, tsDepthLevel);
      }
    });

    impls.put("tostrattr", new PebbleFunction() {

      static final List<String> ARGS = List.of("value", "tsDepthLevel");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var value = context.getArg("value");
        var tsDepthLevel = context.getIntReq("tsDepthLevel");

        return com.appslandia.plum.facelet.Functions.toStrAttr(value, tsDepthLevel);
      }
    });

    impls.put("fmtdt", new PebbleFunction() {

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

    impls.put("fmtdtattr", new PebbleFunction() {

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

        return com.appslandia.plum.facelet.Functions.fmtDtAttr(ctx, value);
      }
    });

    impls.put("fmtint", new PebbleFunction() {

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

    impls.put("fmtintattr", new PebbleFunction() {

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

        return com.appslandia.plum.facelet.Functions.fmtIntAttr(ctx, value);
      }
    });

    impls.put("fmtnum", new PebbleFunction() {

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

    impls.put("fmtnumattr", new PebbleFunction() {

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

        return com.appslandia.plum.facelet.Functions.fmtNumAttr(ctx, value, fractionDigits);
      }
    });

    impls.put("fmtper", new PebbleFunction() {

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

    impls.put("fmtperattr", new PebbleFunction() {

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

        return com.appslandia.plum.facelet.Functions.fmtPerAttr(ctx, value, fractionDigits);
      }
    });

    impls.put("fmtcur", new PebbleFunction() {

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

    impls.put("fmtcurattr", new PebbleFunction() {

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

        return com.appslandia.plum.facelet.Functions.fmtCurAttr(ctx, value, fractionDigits);
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

    impls.put("resattr", new PebbleFunction() {

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

        return com.appslandia.plum.facelet.Functions.resAttr(ctx, key);
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

    impls.put("escattr", new PebbleFunction() {

      static final List<String> ARGS = List.of("value");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var value = context.getArg("value");

        return com.appslandia.plum.facelet.Functions.escAttr(value);
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

    impls.put("ifattr", new PebbleFunction() {

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

        return com.appslandia.plum.facelet.Functions.ifAttr(test, value);
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

    impls.put("iifattr", new PebbleFunction() {

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

        return com.appslandia.plum.facelet.Functions.iifAttr(test, trueValue, falseValue);
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

    impls.put("truncattr", new PebbleFunction() {

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

        return com.appslandia.plum.facelet.Functions.truncAttr(value, len);
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

    impls.put("upperattr", new PebbleFunction() {

      static final List<String> ARGS = List.of("value");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var value = context.getArg("value");

        return com.appslandia.plum.facelet.Functions.upperAttr(value);
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

    impls.put("lowerattr", new PebbleFunction() {

      static final List<String> ARGS = List.of("value");

      @Override
      public List<String> getArgumentNames() {
        return ARGS;
      }

      @Override
      protected Object doExecute(TemplateEvaluationContext context, int lineNumber)
          throws PebbleException, IOException {
        var value = context.getArg("value");

        return com.appslandia.plum.facelet.Functions.lowerAttr(value);
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

    impls.put("maskattr", new PebbleFunction() {

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

        return com.appslandia.plum.facelet.Functions.maskAttr(value, len);
      }
    });

    impls.put("maske", new PebbleFunction() {

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

        return com.appslandia.plum.facelet.Functions.maske(value, len);
      }
    });

    impls.put("maskeattr", new PebbleFunction() {

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

        return com.appslandia.plum.facelet.Functions.maskeAttr(value, len);
      }
    });

    impls.put("fmtdays", new PebbleFunction() {

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

    impls.put("fmtdaysattr", new PebbleFunction() {

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

        return com.appslandia.plum.facelet.Functions.fmtDaysAttr(ctx, value, zoneId);
      }
    });
  }
}
