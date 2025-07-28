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

import com.appslandia.common.utils.Asserts;
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
@Tag(name = "choiceInput", attributes = {
  @Attribute(name = "value", type = Object.class, required = true),
  @Attribute(name = "checkbox", type = Boolean.class),
  @Attribute(name = "required", type = Boolean.class),
  @Attribute(name = "disabled", type = Boolean.class),
  @Attribute(name = "model", type = String.class),
  @Attribute(name = "form", type = String.class),
  @Attribute(name = "autocomplete", type = String.class),
  @Attribute(name = "autofocus", type = Boolean.class),

  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(ChoiceInputTag.COMPONENT_TYPE)
public class ChoiceInputTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.ChoiceInputTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.unmodifiableSet(
    "value",
    "checkbox",
    "required",
    "disabled",
    "model",
    "form",
    "autocomplete",
    "autofocus",

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
  protected Object value;
  protected boolean checkbox = true;

  protected String model;
  protected String form;

  protected String _path;
  protected String _id;
  protected String _handlingValue;

  protected String getTagId(boolean multiple) {
    var id = TagUtils.toTagId(this._path);
    if (multiple) {
      id = id + "_" + TagUtils.toIdPart(this.value);
    }
    return id;
  }

  protected Object getHandlingValue(FacesContext ctx, boolean isValid) {
    return this.evalPath(ctx, this.model, this._path);
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

    // choiceBox
    var choiceBox = findParent(ChoiceBoxTag.class);
    Asserts.notNull(choiceBox, "No ChoiceBoxTag found.");

    this._path = choiceBox.path;
    var value = getValue(ctx, "value");
    this.value = formatInputValue(ctx, value, choiceBox.formatter);
    this._id = getTagId(choiceBox.multiple);

    var isValid = !Objects.equals(this.form, this.getModelState(ctx).getForm())
        || this.getModelState(ctx).isValid(this._path);

    // Handling value
    var handlingValue = getHandlingValue(ctx, isValid);
    this._handlingValue = formatInputValue(ctx, handlingValue, choiceBox.formatter);

    this.checkbox = getBool(ctx, "checkbox", true);
  }

  protected boolean isChecked() {
    if (this.checkbox) {
      return TagUtils.isCheckboxChecked((String) this.value, this._handlingValue);
    } else {
      return TagUtils.isRadioChecked((String) this.value, this._handlingValue);
    }
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    HtmlUtils.escAttribute(out, "id", this._id);
    HtmlUtils.escAttribute(out, "type", this.checkbox ? "checkbox" : "radio");
    HtmlUtils.escAttribute(out, "name", this._path);

    if (this.value != null) {
      HtmlUtils.escAttribute(out, "value", (String) this.value);
    }

    if (this.isChecked()) {
      HtmlUtils.checked(out);
    }

    if (getBool(ctx, "required", false)) {
      HtmlUtils.required(out);
    }

    if (getBool(ctx, "disabled", false)) {
      HtmlUtils.disabled(out);
    }

    if ((this.form != null) && !isInFormContext(ctx)) {
      HtmlUtils.escAttribute(out, "form", this.form);
    }

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

  @Override
  protected boolean hasClosing() {
    return false;
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
  }
}
