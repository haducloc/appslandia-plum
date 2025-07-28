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

package com.appslandia.plum.facelet;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.appslandia.common.base.BoolFormatException;
import com.appslandia.common.base.StringOutput;
import com.appslandia.common.base.TextBuilder;
import com.appslandia.common.converters.Converter;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.ParseUtils;
import com.appslandia.common.utils.STR;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.base.ActionParser;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.ElProcessorPool;
import com.appslandia.plum.base.ModelState;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.tags.FormContext;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.el.ELProcessor;
import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponentBase;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class FlComponent extends UIComponentBase {

  public static final String COMPONENT_FAMILY = "appslandia";

  @Override
  public String getFamily() {
    return COMPONENT_FAMILY;
  }

  public String evalBody(FacesContext ctx) throws IOException {
    if (getChildCount() > 0) {
      var respOut = ctx.getResponseWriter();
      var tempOut = new StringOutput();
      ctx.setResponseWriter(respOut.cloneWithWriter(tempOut));

      super.encodeChildren(ctx);
      ctx.setResponseWriter(respOut);
      return tempOut.toString();
    }
    return StringUtils.EMPTY_STRING;
  }

  public boolean invokeBody(FacesContext ctx) throws IOException {
    if (getChildCount() > 0) {
      super.encodeChildren(ctx);
      return true;
    }
    return false;
  }

  public Object evalPath(FacesContext ctx, String modelVar, String path) {
    var request = getRequest(ctx);
    var model = request.getAttribute(modelVar);
    var pool = ServletUtils.getAppScoped(request, ElProcessorPool.class);

    ELProcessor el = null;
    try {
      el = pool.obtain();
      el.defineBean(modelVar, model);
      return el.eval(modelVar + "." + path);

    } finally {
      if (el != null) {
        el.defineBean(modelVar, null);
        pool.release(el);
      }
    }
  }

  @Override
  public void encodeBegin(FacesContext context) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void encodeEnd(FacesContext context) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void encodeChildren(FacesContext context) throws IOException {
    throw new UnsupportedOperationException();
  }

  public <T extends FlComponent> T findParent(Class<T> parentClass) {
    var cur = this.getParent();
    while (cur != null) {
      if (parentClass.isInstance(cur)) {
        return parentClass.cast(cur);
      } else if (cur instanceof FlComponent) {
        cur = cur.getParent();
      } else {
        break;
      }
    }
    return null;
  }

  public String formatInputValue(FacesContext ctx, Object value, String formatter) {
    if (value == null) {
      return null;
    }
    if (value instanceof String str) {
      return str;
    }

    var reqCtx = getRequestContext(ctx);
    Converter<Object> converter = null;

    if (formatter != null) {
      converter = reqCtx.getConverterProvider().getConverter(formatter);
    } else if (reqCtx.getConverterProvider().hasConverter(value.getClass())) {
      converter = reqCtx.getConverterProvider().getConverter(value.getClass());
    }

    return (converter != null) ? converter.format(value, reqCtx.getFormatProvider(), false) : value.toString();
  }

  public void newLine(ResponseWriter out) throws IOException {
    out.write(System.lineSeparator());
  }

  public FormContext getFormContext(FacesContext ctx) {
    return (FormContext) ctx.getAttributes().get(FormContext.ATTRIBUTE_FORM_CONTEXT);
  }

  public boolean isInFormContext(FacesContext ctx) {
    return ctx.getAttributes().containsKey(FormContext.ATTRIBUTE_FORM_CONTEXT);
  }

  public HttpServletRequest getRequest(FacesContext ctx) {
    return (HttpServletRequest) ctx.getExternalContext().getRequest();
  }

  public HttpServletResponse getResponse(FacesContext ctx) {
    return (HttpServletResponse) ctx.getExternalContext().getResponse();
  }

  public RequestContext getRequestContext(FacesContext ctx) {
    return ServletUtils.getRequestContext(getRequest(ctx));
  }

  public ModelState getModelState(FacesContext ctx) {
    return ServletUtils.getModelState(getRequest(ctx));
  }

  public AppConfig getAppConfig(FacesContext ctx) {
    return ServletUtils.getAppScoped(getRequest(ctx), AppConfig.class);
  }

  public ActionParser getActionParser(FacesContext ctx) {
    return ServletUtils.getAppScoped(getRequest(ctx), ActionParser.class);
  }

  public Map<String, Object> getDynamicParameters(FacesContext ctx) {
    Map<String, Object> map = new LinkedHashMap<>();
    consumeAttributes(ctx, name -> TagUtils.isDynamicParameter(name),
        (name, value) -> map.put(TagUtils.getDynParamName(name), value));
    return map;
  }

  public void consumeAttributes(FacesContext ctx, Function<String, Boolean> matcher,
      BiConsumer<String, Object> consumer) {
    for (Map.Entry<String, Object> entry : getAttributes().entrySet()) {
      var name = entry.getKey();
      if (matcher.apply(name)) {

        var value = getValue(ctx, name);
        consumer.accept(name, value);
      }
    }
  }

  public void assertIdSet(FacesContext ctx) throws IOException {
    var value = getStringReq(ctx, "id");
    Arguments.isTrue(!value.startsWith(UIViewRoot.UNIQUE_ID_PREFIX), "The id attribute must be set explicitly.");
  }

  public void writeIdAttribute(FacesContext ctx, Writer out) throws IOException {
    var value = getString(ctx, "id");
    if ((value != null) && !value.startsWith(UIViewRoot.UNIQUE_ID_PREFIX)) {
      HtmlUtils.escAttribute(out, "id", value);
    }
  }

  public void writeAttribute(FacesContext ctx, Writer out, String variableName, String attributeName)
      throws IOException {
    var value = getString(ctx, variableName);
    if (value != null) {
      HtmlUtils.escAttribute(out, attributeName, value);
    }
  }

  public void writeAttribute(FacesContext ctx, Writer out, String variableName) throws IOException {
    var value = getString(ctx, variableName);
    if (value != null) {
      HtmlUtils.escAttribute(out, variableName, value);
    }
  }

  public void formatAttribute(FacesContext ctx, Writer out, String variableName, String formatter) throws IOException {
    var value = getValue(ctx, variableName);
    if (value != null) {
      HtmlUtils.escAttribute(out, variableName, formatInputValue(ctx, value, formatter));
    }
  }

  public Object getValue(FacesContext ctx, String name) {
    var valueExpression = getValueExpression(name);
    if (valueExpression != null) {
      return valueExpression.getValue(ctx.getELContext());
    }

    var value = getAttributes().get(name);
    if (value instanceof ValueExpression) {
      return ((ValueExpression) value).getValue(ctx.getELContext());
    }
    return value;
  }

  public Object getValueReq(FacesContext ctx, String name) {
    var val = getValue(ctx, name);
    Arguments.notNull(val);
    return val;
  }

  public Iterable<?> getIterable(FacesContext ctx, String name) {
    var value = getValue(ctx, name);
    if (value == null) {
      return null;
    }
    return ObjectUtils.toIterable(value);
  }

  // ===== INT =====

  public Integer getIntOpt(FacesContext ctx, String name) throws NumberFormatException {
    var value = getValue(ctx, name);
    return switch (value) {
    case null -> null;
    case Integer i -> i;
    case Number num -> num.intValue();
    case String str -> ParseUtils.parseIntOpt(str);
    default -> throw new NumberFormatException(STR.fmt("Couldn't convert '{}' to java.lang.Integer.", value));
    };
  }

  public int getInt(FacesContext ctx, String name, int defaultValue) {
    var val = getIntOpt(ctx, name);
    return (val != null) ? val : defaultValue;
  }

  public int getIntReq(FacesContext ctx, String name) throws NumberFormatException {
    var val = getIntOpt(ctx, name);
    Arguments.notNull(val);
    return val;
  }

  // ===== BOOLEAN =====

  public Boolean getBoolOpt(FacesContext ctx, String name) throws BoolFormatException {
    var value = getValue(ctx, name);
    return switch (value) {
    case null -> null;
    case Boolean b -> b;
    case String str -> ParseUtils.parseBoolOpt(str);
    default -> throw new BoolFormatException(STR.fmt("Couldn't convert '{}' to java.lang.Boolean.", value));
    };
  }

  public boolean getBool(FacesContext ctx, String name, boolean defaultValue) {
    var val = getBoolOpt(ctx, name);
    return (val != null) ? val : defaultValue;
  }

  public boolean getBoolReq(FacesContext ctx, String name) throws BoolFormatException {
    var val = getBoolOpt(ctx, name);
    Arguments.notNull(val);
    return val;
  }

  // ===== STRING =====

  public String getString(FacesContext ctx, String name) {
    var value = getValue(ctx, name);
    if (value == null) {
      return null;
    }
    if (value instanceof String str) {
      return str;
    }
    return value.toString();
  }

  public String getString(FacesContext ctx, String name, String defaultValue) {
    var val = getString(ctx, name);
    return (val != null) ? val : defaultValue;
  }

  public String getStringReq(FacesContext ctx, String name) {
    var val = getString(ctx, name);
    Arguments.notNull(val);
    return val;
  }

  public static String toTaglibAttributes(Class<?> tagClass) {
    var tag = tagClass.getDeclaredAnnotation(Tag.class);
    Asserts.notNull(tag, "@com.appslandia.plum.facelet.Tag is required.");

    var sb = new TextBuilder();
    sb.appendln("  // @formatter:off");
    sb.appendln("  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.unmodifiableSet(");

    var attributes = tag.attributes();

    for (var i = 0; i < attributes.length; i++) {
      var attribute = attributes[i];

      if (i < attributes.length - 1) {
        sb.appendln("    \"" + attribute.name() + "\",");
      } else {
        sb.appendln("    \"" + attribute.name() + "\"");
      }
    }

    sb.appendln("  );");
    sb.appendln("  // @formatter:on");
    sb.appendln();
    sb.appendln("  @Override");
    sb.appendln("  public Set<String> getTaglibAttributes() {");
    sb.appendln("    return TAGLIB_ATTRS;");
    sb.appendln("  }");
    sb.appendln();

    return sb.toString();
  }
}
