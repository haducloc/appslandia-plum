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

import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.faces.FacesException;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

/**
 *
 * @author Loc Ha
 *
 */
// @formatter:off
@Tag(name = "button", bodyContent = true, attributes = {
  @Attribute(name = "labelKey", type = String.class),
  @Attribute(name = "type", type = String.class),
  @Attribute(name = "name", type = String.class),
  @Attribute(name = "value", type = String.class),
  @Attribute(name = "disabled", type = Boolean.class),
  @Attribute(name = "autofocus", type = Boolean.class),
  @Attribute(name = "form", type = String.class),

  @Attribute(name = "id", type = String.class),
  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(ButtonTag.COMPONENT_TYPE)
public class ButtonTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.ButtonTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.unmodifiableSet(
    "labelKey",
    "type",
    "name",
    "value",
    "disabled",
    "autofocus",
    "form",

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
    return "button";
  }

  // Fields
  protected String _label;
  protected String form;

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    // Form Context
    var formCtx = getFormContext(ctx);
    this.form = (formCtx != null) ? formCtx.getForm() : getString(ctx, "form");

    // label
    var labelKey = getString(ctx, "labelKey");
    if (labelKey != null) {
      this._label = getRequestContext(ctx).res(labelKey);
    } else if (this.getChildCount() > 0) {
      this._label = evalBody(ctx);
    } else {
      throw new FacesException("Couldn't determine the button label.");
    }
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    writeIdAttribute(ctx, out);
    HtmlUtils.escAttribute(out, "type", getString(ctx, "type", "button"));
    writeAttribute(ctx, out, "name");
    writeAttribute(ctx, out, "value");
    HtmlUtils.escAttribute(out, "data-label", this._label);

    if (getBool(ctx, "disabled", false)) {
      HtmlUtils.disabled(out);
    }

    if ((this.form != null) && !isInFormContext(ctx)) {
      HtmlUtils.escAttribute(out, "form", this.form);
    }

    if (getBool(ctx, "autofocus", false)) {
      HtmlUtils.autofocus(out);
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
    XmlEscaper.escapeContent(out, this._label);
  }
}
