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
import java.util.Objects;
import java.util.Set;

import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

/**
 *
 * @author Loc Ha
 *
 */
// @formatter:off
@Tag(name = "fieldError", bodyContent = true, attributes = {
  @Attribute(name = "path", type = String.class, required = true),
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
@FacesComponent(FieldErrorTag.COMPONENT_TYPE)
public class FieldErrorTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.FieldErrorTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.unmodifiableSet(
    "path",
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
    return "div";
  }

  // Fields
  protected String path;
  protected String form;
  protected boolean _isValid;

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    this.path = getStringReq(ctx, "path");

    // Form Context
    var formCtx = getFormContext(ctx);
    this.form = (formCtx != null) ? formCtx.getForm() : getString(ctx, "form");

    var isValid = !Objects.equals(this.form, this.getModelState(ctx).getForm())
        || this.getModelState(ctx).isValid(this.path);

    // Class
    if (isValid) {
      var dNoneClass = TagUtils.CSS_D_NONE;
      var clazz = getString(ctx, "clazz");
      clazz = (clazz == null) ? dNoneClass : clazz + " " + dNoneClass;
      getAttributes().put("clazz", clazz);
    }
    this._isValid = isValid;
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    writeIdAttribute(ctx, out);

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
    if (!this._isValid) {
      var error = getModelState(ctx).getFieldErrors(this.path).get(0);
      if (error.isEscXml()) {
        XmlEscaper.escapeContent(out, error.getText());
      } else {
        out.write(error.getText());
      }
    }
  }
}
