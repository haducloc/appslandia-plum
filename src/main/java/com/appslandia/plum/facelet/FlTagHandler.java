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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.appslandia.common.base.BoolFormatException;
import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.ObjectUtils;
import com.appslandia.common.utils.ParseUtils;
import com.appslandia.common.utils.STR;
import com.appslandia.plum.base.ActionParser;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.faces.component.html.HtmlOutputText;
import jakarta.faces.view.facelets.FaceletContext;
import jakarta.faces.view.facelets.TagAttribute;
import jakarta.faces.view.facelets.TagConfig;
import jakarta.faces.view.facelets.TagHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class FlTagHandler extends TagHandler {

  public FlTagHandler(TagConfig config) {
    super(config);
  }

  public HttpServletRequest getRequest(FaceletContext ctx) {
    return (HttpServletRequest) ctx.getFacesContext().getExternalContext().getRequest();
  }

  public HttpServletResponse getResponse(FaceletContext ctx) {
    return (HttpServletResponse) ctx.getFacesContext().getExternalContext().getResponse();
  }

  public RequestContext getRequestContext(FaceletContext ctx) {
    return ServletUtils.getRequestContext(getRequest(ctx));
  }

  public AppConfig getAppConfig(FaceletContext ctx) {
    return ServletUtils.getAppScoped(getRequest(ctx), AppConfig.class);
  }

  public ActionParser getActionParser(FaceletContext ctx) {
    return ServletUtils.getAppScoped(getRequest(ctx), ActionParser.class);
  }

  public HtmlOutputText toHtmlOuputText(String safeHtml) {
    var text = new HtmlOutputText();
    text.setValue(safeHtml);
    text.setEscape(false);
    text.setTransient(true);
    return text;
  }

  public Map<String, Object> getDynamicParameters(FaceletContext ctx) {
    Map<String, Object> map = new LinkedHashMap<>();
    consumeAttributes(ctx, name -> TagUtils.isDynamicParameter(name),
        (name, value) -> map.put(TagUtils.getDynParamName(name), value));
    return map;
  }

  public void consumeAttributes(FaceletContext ctx, Function<String, Boolean> matcher,
      BiConsumer<String, Object> consumer) {
    for (TagAttribute attr : this.tag.getAttributes().getAll()) {
      var name = attr.getLocalName();
      if (matcher.apply(name)) {

        var value = attr.getObject(ctx);
        consumer.accept(name, value);
      }
    }
  }

  public boolean isRendered(FaceletContext ctx) {
    return getBool(ctx, "rendered", true);
  }

  public Iterable<?> getIterable(FaceletContext ctx, String name) {
    var value = getValue(ctx, name);
    if (value == null) {
      return null;
    }
    return ObjectUtils.toIterable(value);
  }

  public Object getValue(FaceletContext ctx, String name) {
    var attr = getAttribute(name);
    if (attr != null) {
      return attr.getObject(ctx);
    }
    return null;
  }

  public Object getValueReq(FaceletContext ctx, String name) {
    var val = getValue(ctx, name);
    Arguments.notNull(val);
    return val;
  }

  // ===== INT =====

  public Integer getIntOpt(FaceletContext ctx, String name) throws NumberFormatException {
    var value = getValue(ctx, name);
    return switch (value) {
    case null -> null;
    case Integer i -> i;
    case Number num -> num.intValue();
    case String str -> ParseUtils.parseIntOpt(str);
    default -> throw new NumberFormatException(STR.fmt("Couldn't convert '{}' to java.lang.Integer.", value));
    };
  }

  public int getInt(FaceletContext ctx, String name, int defaultValue) {
    var val = getIntOpt(ctx, name);
    return (val != null) ? val : defaultValue;
  }

  public int getIntReq(FaceletContext ctx, String name) throws NumberFormatException {
    var val = getIntOpt(ctx, name);
    Arguments.notNull(val);
    return val;
  }

  // ===== BOOLEAN =====

  public Boolean getBoolOpt(FaceletContext ctx, String name) throws BoolFormatException {
    var value = getValue(ctx, name);
    return switch (value) {
    case null -> null;
    case Boolean b -> b;
    case String str -> ParseUtils.parseBoolOpt(str);
    default -> throw new BoolFormatException(STR.fmt("Couldn't convert '{}' to java.lang.Boolean.", value));
    };
  }

  public boolean getBool(FaceletContext ctx, String name, boolean defaultValue) {
    var val = getBoolOpt(ctx, name);
    return (val != null) ? val : defaultValue;
  }

  public boolean getBoolReq(FaceletContext ctx, String name) throws BoolFormatException {
    var val = getBoolOpt(ctx, name);
    Arguments.notNull(val);
    return val;
  }

  // ===== STRING =====

  public String getString(FaceletContext ctx, String name) {
    var value = getValue(ctx, name);
    if (value == null) {
      return null;
    }
    if (value instanceof String str) {
      return str;
    }
    return value.toString();
  }

  public String getString(FaceletContext ctx, String name, String defaultValue) {
    var val = getString(ctx, name);
    return (val != null) ? val : defaultValue;
  }

  public String getStringReq(FaceletContext ctx, String name) {
    var val = getString(ctx, name);
    Arguments.notNull(val);
    return val;
  }
}
