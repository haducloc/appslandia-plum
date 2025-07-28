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
import com.appslandia.plum.base.SelectItem;
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
@Tag(name = "select", attributes = {
  @Attribute(name = "path", type = String.class, required = true),
  @Attribute(name = "required", type = Boolean.class),
  @Attribute(name = "disabled", type = Boolean.class),
  @Attribute(name = "model", type = String.class),
  @Attribute(name = "form", type = String.class),
  @Attribute(name = "formatter", type = String.class),
  @Attribute(name = "size", type = Integer.class),
  @Attribute(name = "autocomplete", type = String.class),
  @Attribute(name = "autofocus", type = Boolean.class),
  @Attribute(name = "items", type = Iterable.class, required = true),
  @Attribute(name = "optional", type = Boolean.class),
  @Attribute(name = "optLabel", type = String.class),

  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(SelectTag.COMPONENT_TYPE)
@SuppressWarnings({ "unchecked" })
public class SelectTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.SelectTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.unmodifiableSet(
    "path",
    "required",
    "disabled",
    "model",
    "form",
    "formatter",
    "size",
    "autocomplete",
    "autofocus",
    "items",
    "optional",
    "optLabel",

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
    return "select";
  }

  // Fields
  protected String path;
  protected String model;
  protected String form;
  protected String formatter;

  protected String _id;
  protected String _handlingValue;

  protected Object getHandlingValue(FacesContext ctx, boolean isValid) {
    return this.evalPath(ctx, this.model, this.path);
  }

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    // Form Context
    var formCtx = getFormContext(ctx);
    this.model = getString(ctx, "model");
    if (this.model == null) {
      this.model = (formCtx != null) && (formCtx.getModel() != null) ? formCtx.getModel()
          : ServletUtils.REQUEST_ATTRIBUTE_MODEL;
    }
    this.form = (formCtx != null) ? formCtx.getForm() : getString(ctx, "form");

    this.path = getStringReq(ctx, "path");
    this._id = TagUtils.toTagId(this.path);

    var isValid = !Objects.equals(this.form, this.getModelState(ctx).getForm())
        || this.getModelState(ctx).isValid(this.path);

    // Handling value
    var handlingValue = getHandlingValue(ctx, isValid);
    this._handlingValue = formatInputValue(ctx, handlingValue, this.formatter);

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
    HtmlUtils.escAttribute(out, "id", this._id);
    HtmlUtils.escAttribute(out, "name", this.path);

    if (getBool(ctx, "required", false)) {
      HtmlUtils.required(out);
    }

    if (getBool(ctx, "disabled", false)) {
      HtmlUtils.disabled(out);
    }

    if ((this.form != null) && !isInFormContext(ctx)) {
      HtmlUtils.escAttribute(out, "form", this.form);
    }

    writeAttribute(ctx, out, "size");
    writeAttribute(ctx, out, "autocomplete");

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

  protected void writeOptItem(FacesContext ctx, ResponseWriter out, String optLabel, boolean selected)
      throws IOException {
    out.write("<option");
    if (selected) {
      HtmlUtils.selected(out);
    }

    out.write('>');
    if (optLabel != null) {
      XmlEscaper.escapeContent(out, optLabel);
    }
    out.write("</option>");
  }

  protected void writeOption(FacesContext ctx, ResponseWriter out, SelectItem item, boolean selected,
      boolean optDisabled) throws IOException {
    out.write("<option");
    if (item.getValue() != null) {
      HtmlUtils.escAttribute(out, "value", formatInputValue(ctx, item.getValue(), this.formatter));
    }

    if (selected) {
      HtmlUtils.selected(out);
    }

    if (optDisabled) {
      HtmlUtils.disabled(out);
    }
    out.write('>');

    if (item.getDisplayName() != null) {
      XmlEscaper.escapeContent(out, item.getDisplayName());
    }
    out.write("</option>");
  }

  @Override
  protected boolean hasClosing() {
    return true;
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
    // optional
    var optional = getBool(ctx, "optional", false);
    var optLabel = getString(ctx, "optLabel");

    if (optional) {
      var selected = (this._handlingValue == null);
      writeOptItem(ctx, out, optLabel, selected);
    }

    // items
    var items = (Iterable<SelectItem>) getIterable(ctx, "items");
    if (items != null) {
      for (SelectItem item : items) {

        var itemVal = formatInputValue(ctx, item.getValue(), this.formatter);
        var selected = Objects.equals(itemVal, this._handlingValue);
        writeOption(ctx, out, item, selected, item.isDisabled());
      }
    }
  }
}
