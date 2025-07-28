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
import java.util.Set;

import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.plum.tags.FormContext;
import com.appslandia.plum.utils.HtmlUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

/**
 *
 * @author Loc Ha
 *
 */
// @formatter:off
@Tag(name = "form", bodyContent = true, attributes = {
  @Attribute(name = "name", type = String.class),
  @Attribute(name = "method", type = String.class),
  @Attribute(name = "enctype", type = String.class),
  @Attribute(name = "autocomplete", type = String.class),
  @Attribute(name = "acceptCharset", type = String.class),
  @Attribute(name = "target", type = String.class),
  @Attribute(name = "novalidate", type = Boolean.class),
  @Attribute(name = "model", type = String.class),
  @Attribute(name = "controller", type = String.class),
  @Attribute(name = "action", type = String.class, required = true),

  @Attribute(name = "id", type = String.class),
  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(FormTag.COMPONENT_TYPE)
public class FormTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.FormTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.unmodifiableSet(
    "name",
    "method",
    "enctype",
    "autocomplete",
    "acceptCharset",
    "target",
    "novalidate",
    "model",
    "controller",
    "action",

    "id",
    "clazz",
    "style",
    "title",
    "hidden",
    "datatag",
    "rendered"
  );
  // @formatter:on

  @Override
  public Set<String> getTaglibAttributes() {
    return TAGLIB_ATTRS;
  }

  @Override
  protected String getTagName() {
    return "form";
  }

  // Fields
  protected String id;
  protected String _url;

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    // action/controller
    var action = getStringReq(ctx, "action");
    var controller = getString(ctx, "controller");
    if (controller == null) {
      controller = getRequestContext(ctx).getActionDesc().getController();
    }

    // URL
    var parameters = getDynamicParameters(ctx);
    var url = getActionParser(ctx).toActionUrl(getRequest(ctx), controller, action, parameters, false);
    url = getResponse(ctx).encodeURL(url);
    this._url = url;

    // id
    this.id = getString(ctx, "id");

    // FormContext
    Asserts.isNull(ctx.getAttributes().get(FormContext.ATTRIBUTE_FORM_CONTEXT));
    ctx.getAttributes().put(FormContext.ATTRIBUTE_FORM_CONTEXT,
        new FormContext().setForm(this.id).setModel(getString(ctx, "model")));
  }

  @Override
  protected void cleanUpTag(FacesContext ctx) throws IOException {
    ctx.getAttributes().remove(FormContext.ATTRIBUTE_FORM_CONTEXT);
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    if (this.id != null) {
      HtmlUtils.escAttribute(out, "id", this.id);
    }

    writeAttribute(ctx, out, "name");
    HtmlUtils.escAttribute(out, "action", this._url);

    writeAttribute(ctx, out, "method");
    writeAttribute(ctx, out, "enctype");
    HtmlUtils.escAttribute(out, "autocomplete", getString(ctx, "autocomplete", "off"));
    writeAttribute(ctx, out, "acceptCharset", "accept-charset");
    writeAttribute(ctx, out, "target");

    if (getBool(ctx, "novalidate", false)) {
      HtmlUtils.novalidate(out);
    }

    if (getBool(ctx, "hidden", false)) {
      HtmlUtils.hidden(out);
    }

    writeAttribute(ctx, out, "datatag", "data-tag");
    writeAttribute(ctx, out, "clazz", "class");
    writeAttribute(ctx, out, "style");
    writeAttribute(ctx, out, "title");
  }

  @Override
  protected boolean hasClosing() {
    return true;
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
    // Form ID
    out.write("<input");
    HtmlUtils.escAttribute(out, "id", ServletUtils.PARAM_FORM_FIELD);
    HtmlUtils.escAttribute(out, "type", "hidden");
    HtmlUtils.escAttribute(out, "name", ServletUtils.PARAM_FORM_FIELD);

    if (this.id != null) {
      HtmlUtils.escAttribute(out, "value", this.id);
    }
    out.write(" />");

    invokeBody(ctx);
  }
}
