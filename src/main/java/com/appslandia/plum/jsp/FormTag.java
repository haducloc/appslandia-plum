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

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.Asserts;
import com.appslandia.plum.tags.FormContext;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "form", bodyContent = "scriptless")
public class FormTag extends UITagBase {

  protected String name;
  protected String method;
  protected String enctype;
  protected String autocomplete;
  protected String acceptCharset;

  protected String target;
  protected boolean novalidate;

  protected String model;
  protected String controller;
  protected String action;
  protected Map<String, Object> _parameters;

  protected String _url;

  protected Map<String, Object> getParams() {
    if (_parameters == null) {
      return _parameters = new LinkedHashMap<>();
    }
    return _parameters;
  }

  @Override
  public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
    if (TagUtils.isDynamicParameter(name)) {
      getParams().put(TagUtils.getDynParamName(name), value);
    } else {
      super.setDynamicAttribute(uri, name, value);
    }
  }

  @Override
  protected String getTagName() {
    return "form";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    Arguments.notNull(action);
    if (controller == null) {
      controller = getRequestContext().getActionDesc().getController();
    }

    // URL
    var url = getActionParser().toActionUrl(getRequest(), controller, action, _parameters, false);
    _url = getResponse().encodeURL(url);

    // FormContext
    Asserts.isNull(pageContext.getAttribute(FormContext.ATTRIBUTE_FORM_CONTEXT));
    pageContext.setAttribute(FormContext.ATTRIBUTE_FORM_CONTEXT, new FormContext().setForm(id).setModel(model));
  }

  @Override
  protected void cleanUpTag() throws JspException, IOException {
    pageContext.removeAttribute(FormContext.ATTRIBUTE_FORM_CONTEXT);
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (id != null) {
      HtmlUtils.escAttribute(out, "id", id);
    }
    if (name != null) {
      HtmlUtils.escAttribute(out, "name", name);
    }
    HtmlUtils.escAttribute(out, "action", _url);

    if (method != null) {
      HtmlUtils.escAttribute(out, "method", method);
    }
    if (enctype != null) {
      HtmlUtils.escAttribute(out, "enctype", enctype);
    }

    HtmlUtils.escAttribute(out, "autocomplete", (autocomplete != null) ? autocomplete : "off");

    if (acceptCharset != null) {
      HtmlUtils.escAttribute(out, "accept-charset", acceptCharset);
    }

    if (target != null) {
      HtmlUtils.escAttribute(out, "target", target);
    }

    if (novalidate) {
      HtmlUtils.novalidate(out);
    }

    if (hidden) {
      HtmlUtils.hidden(out);
    }

    if (datatag != null) {
      HtmlUtils.escAttribute(out, "data-tag", datatag.toString());
    }
    if (clazz != null) {
      HtmlUtils.escAttribute(out, "class", clazz);
    }
    if (style != null) {
      HtmlUtils.escAttribute(out, "style", style);
    }
    if (title != null) {
      HtmlUtils.escAttribute(out, "title", title);
    }
  }

  @Override
  protected boolean hasClosing() {
    return true;
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
    // Form ID
    out.write("<input");
    HtmlUtils.escAttribute(out, "id", ServletUtils.PARAM_FORM_FIELD);
    HtmlUtils.escAttribute(out, "type", "hidden");
    HtmlUtils.escAttribute(out, "name", ServletUtils.PARAM_FORM_FIELD);

    if (id != null) {
      HtmlUtils.escAttribute(out, "value", id);
    }
    out.write(" />");

    if (body != null) {
      body.invoke(out);
    }
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setName(String name) {
    this.name = name;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setMethod(String method) {
    this.method = method;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setEnctype(String enctype) {
    this.enctype = enctype;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAutocomplete(String autocomplete) {
    this.autocomplete = autocomplete;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAcceptCharset(String acceptCharset) {
    this.acceptCharset = acceptCharset;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setTarget(String target) {
    this.target = target;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setNovalidate(boolean novalidate) {
    this.novalidate = novalidate;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setModel(String model) {
    this.model = model;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setController(String controller) {
    this.controller = controller;
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setAction(String action) {
    this.action = action;
  }
}
