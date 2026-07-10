// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
@Tag(name = "textarea", attributes = {
    @Attribute(name = "path", type = String.class, required = true),
    @Attribute(name = "model", type = String.class),
    @Attribute(name = "form", type = String.class),

    @Attribute(name = "hasFieldError", type = Boolean.class, description = "Field error is defined"),
    @Attribute(name = "describedby", type = String.class, description = "aria-describedby"),

    @Attribute(name = "required", type = Boolean.class),
    @Attribute(name = "readonly", type = Boolean.class),
    @Attribute(name = "disabled", type = Boolean.class),
    @Attribute(name = "autofocus", type = Boolean.class),

    @Attribute(name = "placeholder", type = String.class),

    @Attribute(name = "fmt", type = String.class, description = "Formatter ID"),

    @Attribute(name = "rows", type = Integer.class),
    @Attribute(name = "cols", type = Integer.class),

    @Attribute(name = "minlength", type = Integer.class),
    @Attribute(name = "maxlength", type = Integer.class),

    @Attribute(name = "autocomplete", type = String.class),
    @Attribute(name = "autocapitalize", type = String.class),
    @Attribute(name = "inputmode", type = String.class),
    @Attribute(name = "wrap", type = String.class),

    @Attribute(name = "clazz", type = String.class),
    @Attribute(name = "style", type = String.class),
    @Attribute(name = "title", type = String.class),
    @Attribute(name = "hidden", type = Boolean.class),
    @Attribute(name = "datatag", type = Object.class),
    @Attribute(name = "rendered", type = Boolean.class)
  })
// @formatter:on
@FacesComponent(TextAreaTag.COMPONENT_TYPE)
public class TextAreaTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.TextAreaTag";

//@formatter:off
private static final Set<String> TAGLIB_ATTRS = CollectionUtils.toUnmodifiableSet(
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

 "rows",
 "cols",

 "minlength",
 "maxlength",

 "autocomplete",
 "autocapitalize",
 "inputmode",
 "wrap",

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
    return "textarea";
  }

  // Fields
  protected String path;
  protected String form;
  protected String model;

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

    var fmt = getString(ctx, "fmt");
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

    writeAttribute(ctx, out, "rows");
    writeAttribute(ctx, out, "cols");

    writeAttribute(ctx, out, "minlength");
    writeAttribute(ctx, out, "maxlength");

    writeAttribute(ctx, out, "placeholder");

    writeAttribute(ctx, out, "autocomplete");
    writeAttribute(ctx, out, "autocapitalize");
    writeAttribute(ctx, out, "inputmode");
    writeAttribute(ctx, out, "wrap");

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
    return true;
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
    if (_handlingValue != null) {
      XmlEscaper.escapeContent(out, _handlingValue);
    }
  }
}
