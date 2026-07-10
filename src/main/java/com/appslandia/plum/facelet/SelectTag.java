// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.SelectItem;
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
//@formatter:off
@Tag(name = "select", attributes = {
  @Attribute(name = "path", type = String.class, required = true),
  @Attribute(name = "model", type = String.class),
  @Attribute(name = "form", type = String.class),

  @Attribute(name = "hasFieldError", type = Boolean.class, description = "Field error is defined"),
  @Attribute(name = "describedby", type = String.class, description = "aria-describedby"),

  @Attribute(name = "required", type = Boolean.class),
  @Attribute(name = "disabled", type = Boolean.class),
  @Attribute(name = "autofocus", type = Boolean.class),

  @Attribute(name = "fmt", type = String.class, description = "Formatter ID"),
  @Attribute(name = "size", type = Integer.class),
  @Attribute(name = "autocomplete", type = String.class),

  @Attribute(name = "items", type = Iterable.class, required = true),
  @Attribute(name = "optBlank", type = String.class),

  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
//@formatter:on
@FacesComponent(SelectTag.COMPONENT_TYPE)
@SuppressWarnings({ "unchecked" })
public class SelectTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.SelectTag";

//@formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.toUnmodifiableSet(
   "path",
   "model",
   "form",

   "hasFieldError",
   "describedby",

   "required",
   "disabled",
   "autofocus",

   "fmt",
   "size",
   "autocomplete",

   "items",
   "optBlank",

   "clazz",
   "style",
   "title",
   "hidden",
   "datatag",
   "rendered"
  );
//@formatter:on

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
  protected String fmt;

  protected String _id;
  protected boolean _isValid;
  protected String _handlingValue;

  protected Object getHandlingValue(FacesContext ctx, boolean isValid) {
    return evalPath(ctx, model, path);
  }

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    var formCtx = getFormContext(ctx);
    model = getModel(ctx, formCtx);
    form = getForm(ctx, formCtx);

    path = getStringReq(ctx, "path");
    _id = TagUtils.toTagId(path);

    _isValid = !Objects.equals(form, getModelState(ctx).getForm()) || getModelState(ctx).isValid(path);
    var handlingValue = getHandlingValue(ctx, _isValid);

    fmt = getString(ctx, "fmt");
    _handlingValue = formatValue(ctx, handlingValue, fmt);

    // Class
    if (!_isValid) {
      var errClass = TagUtils.CSS_FIELD_ERROR;
      var clazz = getString(ctx, "clazz");
      clazz = (clazz == null) ? errClass : clazz + " " + errClass;
      getAttributes().put("clazz", clazz);
    }
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    HtmlUtils.escAttribute(out, "id", _id);
    HtmlUtils.escAttribute(out, "name", path);

    if (getBool(ctx, "hidden", false)) {
      HtmlUtils.hidden(out);
    }

    if (getBool(ctx, "disabled", false)) {
      HtmlUtils.disabled(out);
    }

    if (getBool(ctx, "required", false)) {
      HtmlUtils.required(out);
    }

    if (getBool(ctx, "autofocus", false)) {
      HtmlUtils.autofocus(out);
    }

    if (form != null) {
      HtmlUtils.escAttribute(out, "form", form);
    }

    writeAttribute(ctx, out, "size");
    writeAttribute(ctx, out, "autocomplete");

    if (!_isValid) {
      out.write(" aria-invalid=\"true\"");
    }

    // aria-describedby
    writeDescribedby(ctx, out);

    writeAttribute(ctx, out, "datatag", "data-tag");
    writeAttribute(ctx, out, "clazz", "class");
    writeAttribute(ctx, out, "style");
    writeAttribute(ctx, out, "title");
  }

  protected void writeDescribedby(FacesContext ctx, ResponseWriter out) throws IOException {
    var descBy = getString(ctx, "describedby");
    var hasFieldError = getBool(ctx, "hasFieldError", false);
    if (!_isValid && hasFieldError) {

      var fieldErrId = TagUtils.toFieldErrorId(path);
      descBy = (descBy == null) ? fieldErrId : descBy + " " + fieldErrId;
    }
    if (descBy != null) {
      HtmlUtils.escAttribute(out, "aria-describedby", descBy);
    }
  }

  protected void writeOptBlank(FacesContext ctx, ResponseWriter out, String optLabel, boolean selected)
      throws IOException {
    out.write("<option value=\"\"");
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
      HtmlUtils.escAttribute(out, "value", formatValue(ctx, item.getValue(), fmt));
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
  protected void writeTag(FacesContext ctx, ResponseWriter out) throws IOException {
    newLine(out);
    super.writeTag(ctx, out);
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
    // optBlank
    var optBlank = getString(ctx, "optBlank");

    if (optBlank != null) {
      var selected = (_handlingValue == null);
      writeOptBlank(ctx, out, optBlank.strip(), selected);
    }

    // items
    var items = (Iterable<SelectItem>) getIterable(ctx, "items");
    if (items != null) {
      for (SelectItem item : items) {

        var itemVal = formatValue(ctx, item.getValue(), fmt);
        var selected = Objects.equals(itemVal, _handlingValue);
        writeOption(ctx, out, item, selected, item.isDisabled());
      }
    }
  }
}
