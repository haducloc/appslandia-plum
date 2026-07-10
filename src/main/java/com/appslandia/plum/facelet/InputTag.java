// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.CollectionUtils;
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
@Tag(name = "input", attributes = {
  @Attribute(name = "type", type = String.class),
  @Attribute(name = "path", type = String.class, required = true),
  @Attribute(name = "model", type = String.class),
  @Attribute(name = "form", type = String.class),

  @Attribute(name = "hasFieldError", type = Boolean.class, description = "Field error is defined"),
  @Attribute(name = "describedby", type = String.class, description = "aria-describedby"),

  @Attribute(name = "disabled", type = Boolean.class),
  @Attribute(name = "readonly", type = Boolean.class),
  @Attribute(name = "required", type = Boolean.class),
  @Attribute(name = "autofocus", type = Boolean.class),

  @Attribute(name = "placeholder", type = String.class),

  @Attribute(name = "fmt", type = String.class, description = "Formatter ID"),
  @Attribute(name = "min", type = Object.class),
  @Attribute(name = "max", type = Object.class),
  @Attribute(name = "step", type = Object.class),
  @Attribute(name = "pattern", type = String.class),
  @Attribute(name = "minlength", type = Integer.class),
  @Attribute(name = "maxlength", type = Integer.class),

  @Attribute(name = "autocomplete", type = String.class),
  @Attribute(name = "autocapitalize", type = String.class),
  @Attribute(name = "inputmode", type = String.class),

  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
  })
//@formatter:on
@FacesComponent(InputTag.COMPONENT_TYPE)
public class InputTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.InputTag";

  //@formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.toUnmodifiableSet(
   "type",
   "path",
   "model",
   "form",

   "hasFieldError",
   "describedby",

   "required",
   "readonly",
   "disabled",
   "autofocus",

   "placeholder",

   "fmt",
   "min",
   "max",
   "step",
   "pattern",
   "minlength",
   "maxlength",

   "autocomplete",
   "autocapitalize",
   "inputmode",

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
    return "input";
  }

  // Fields
  protected String type;
  protected String path;
  protected String form;
  protected String model;
  protected String fmt;

  protected String _id;
  protected boolean _isValid;
  protected String _handlingValue;

  protected Object getHandlingValue(FacesContext ctx, boolean isValid) {
    if (isValid) {
      return evalPath(ctx, model, path);
    }
    return getRequest(ctx).getParameter(path);
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

    // type
    type = getString(ctx, "type");
    if (type != null) {
      Arguments.isTrue(TagUtils.isValidInputType(type));
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

    if (getBool(ctx, "hidden", false)) {
      HtmlUtils.hidden(out);
    }

    if (getBool(ctx, "disabled", false)) {
      HtmlUtils.disabled(out);
    } else if (getBool(ctx, "readonly", false)) {
      HtmlUtils.readonly(out);
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

    writeAttribute(ctx, out, "placeholder");

    formatAttribute(ctx, out, "min", fmt);
    formatAttribute(ctx, out, "max", fmt);
    formatAttribute(ctx, out, "step", fmt);
    writeAttribute(ctx, out, "pattern");

    writeAttribute(ctx, out, "minlength");
    writeAttribute(ctx, out, "maxlength");

    writeAttribute(ctx, out, "autocomplete");
    writeAttribute(ctx, out, "autocapitalize");
    writeAttribute(ctx, out, "inputmode");

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

  @Override
  protected boolean hasClosing() {
    return false;
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
  }
}
