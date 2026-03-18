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

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.plum.tags.TagUtils;
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
@Tag(name = "input",  attributes = {
  @Attribute(name = "type", type = String.class),
  @Attribute(name = "path", type = String.class, required = true),
  @Attribute(name = "required", type = Boolean.class),
  @Attribute(name = "readonly", type = Boolean.class),
  @Attribute(name = "disabled", type = Boolean.class),
  @Attribute(name = "model", type = String.class),
  @Attribute(name = "form", type = String.class),
  @Attribute(name = "placeholder", type = String.class),
  @Attribute(name = "formatter", type = String.class),
  @Attribute(name = "min", type = Object.class),
  @Attribute(name = "max", type = Object.class),
  @Attribute(name = "step", type = Object.class),
  @Attribute(name = "pattern", type = String.class),
  @Attribute(name = "minlength", type = Integer.class),
  @Attribute(name = "maxlength", type = Integer.class),
  @Attribute(name = "autocomplete", type = String.class),
  @Attribute(name = "autocapitalize", type = String.class),
  @Attribute(name = "autofocus", type = Boolean.class),
  @Attribute(name = "inputmode", type = String.class),

  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(InputTag.COMPONENT_TYPE)
public class InputTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.InputTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.unmodifiableSet(
    "type",
    "path",
    "required",
    "readonly",
    "disabled",
    "model",
    "form",
    "placeholder",
    "formatter",
    "min",
    "max",
    "step",
    "pattern",
    "minlength",
    "maxlength",
    "autocomplete",
    "autocapitalize",
    "autofocus",
    "inputmode",

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
    return "input";
  }

  // Fields
  protected String type;
  protected String path;
  protected String model;
  protected String form;
  protected String formatter;

  protected String _id;
  protected String _handlingValue;

  protected Object getHandlingValue(FacesContext ctx, boolean isValid) {
    if (isValid) {
      return evalPath(ctx, model, path);
    }
    return getRequest(ctx).getParameter(path);
  }

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    // type
    type = getString(ctx, "type");
    if (type != null) {
      Arguments.isTrue(TagUtils.isValidInputType(type));
    }

    // Form Context
    var formCtx = getFormContext(ctx);
    model = getString(ctx, "model");
    if (model == null) {
      model = (formCtx != null) && (formCtx.getModel() != null) ? formCtx.getModel()
          : ServletUtils.REQUEST_ATTRIBUTE_MODEL;
    }
    form = (formCtx != null) ? formCtx.getForm() : getString(ctx, "form");

    path = getStringReq(ctx, "path");
    _id = TagUtils.toTagId(path);

    var isValid = !Objects.equals(form, getModelState(ctx).getForm()) || getModelState(ctx).isValid(path);

    // Handling value
    var handlingValue = getHandlingValue(ctx, isValid);
    _handlingValue = formatInputValue(ctx, handlingValue, formatter);

    // Class
    if (!isValid) {
      var errClass = TagUtils.CSS_FIELD_ERROR;
      var clazz = getString(ctx, "clazz");
      clazz = (clazz == null) ? errClass : clazz + " " + errClass;
      getAttributes().put("clazz", clazz);
    }
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    HtmlUtils.escAttribute(out, "id", _id);
    HtmlUtils.escAttribute(out, "type", (type != null) ? type : "text");
    HtmlUtils.escAttribute(out, "name", path);

    if (_handlingValue != null) {
      HtmlUtils.escAttribute(out, "value", _handlingValue);
    }

    if (getBool(ctx, "required", false)) {
      HtmlUtils.required(out);
    }

    if (getBool(ctx, "disabled", false)) {
      HtmlUtils.disabled(out);
    } else if (getBool(ctx, "readonly", false)) {
      HtmlUtils.readonly(out);
    }

    if ((form != null) && !isInFormContext(ctx)) {
      HtmlUtils.escAttribute(out, "form", form);
    }

    writeAttribute(ctx, out, "placeholder");

    formatAttribute(ctx, out, "min", formatter);
    formatAttribute(ctx, out, "max", formatter);
    formatAttribute(ctx, out, "step", formatter);
    writeAttribute(ctx, out, "pattern");

    writeAttribute(ctx, out, "minlength");
    writeAttribute(ctx, out, "maxlength");

    writeAttribute(ctx, out, "autocomplete");
    writeAttribute(ctx, out, "autocapitalize");
    if (getBool(ctx, "autofocus", false)) {
      HtmlUtils.autofocus(out);
    }

    writeAttribute(ctx, out, "inputmode");

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
    return false;
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
  }
}
