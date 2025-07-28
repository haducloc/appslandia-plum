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
    if (this._parameters == null) {
      return this._parameters = new LinkedHashMap<>();
    }
    return this._parameters;
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
    Arguments.notNull(this.action);
    if (this.controller == null) {
      this.controller = getRequestContext().getActionDesc().getController();
    }

    // URL
    var url = getActionParser().toActionUrl(this.getRequest(), this.controller, this.action, this._parameters, false);
    this._url = this.getResponse().encodeURL(url);

    // FormContext
    Asserts.isNull(this.pageContext.getAttribute(FormContext.ATTRIBUTE_FORM_CONTEXT));
    this.pageContext.setAttribute(FormContext.ATTRIBUTE_FORM_CONTEXT,
        new FormContext().setForm(this.id).setModel(this.model));
  }

  @Override
  protected void cleanUpTag() throws JspException, IOException {
    this.pageContext.removeAttribute(FormContext.ATTRIBUTE_FORM_CONTEXT);
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (this.id != null) {
      HtmlUtils.escAttribute(out, "id", this.id);
    }
    if (this.name != null) {
      HtmlUtils.escAttribute(out, "name", this.name);
    }
    HtmlUtils.escAttribute(out, "action", this._url);

    if (this.method != null) {
      HtmlUtils.escAttribute(out, "method", this.method);
    }
    if (this.enctype != null) {
      HtmlUtils.escAttribute(out, "enctype", this.enctype);
    }

    HtmlUtils.escAttribute(out, "autocomplete", (this.autocomplete != null) ? this.autocomplete : "off");

    if (this.acceptCharset != null) {
      HtmlUtils.escAttribute(out, "accept-charset", this.acceptCharset);
    }

    if (this.target != null) {
      HtmlUtils.escAttribute(out, "target", this.target);
    }

    if (this.novalidate) {
      HtmlUtils.novalidate(out);
    }

    if (this.hidden) {
      HtmlUtils.hidden(out);
    }

    if (this.datatag != null) {
      HtmlUtils.escAttribute(out, "data-tag", this.datatag.toString());
    }
    if (this.clazz != null) {
      HtmlUtils.escAttribute(out, "class", this.clazz);
    }
    if (this.style != null) {
      HtmlUtils.escAttribute(out, "style", this.style);
    }
    if (this.title != null) {
      HtmlUtils.escAttribute(out, "title", this.title);
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

    if (this.id != null) {
      HtmlUtils.escAttribute(out, "value", this.id);
    }
    out.write(" />");

    if (this.body != null) {
      this.body.invoke(out);
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
