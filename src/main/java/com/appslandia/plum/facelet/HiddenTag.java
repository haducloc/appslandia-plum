// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

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
// @formatter:off
@Tag(name = "hidden",  attributes = {
  @Attribute(name = "path", type = String.class, required = true),
  @Attribute(name = "form", type = String.class),
  @Attribute(name = "model", type = String.class),
  @Attribute(name = "fmt", type = String.class, description = "Formatter ID"),
  @Attribute(name = "disabled", type = Boolean.class),

  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(HiddenTag.COMPONENT_TYPE)
public class HiddenTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.HiddenTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.toUnmodifiableSet(
    "path",
    "form",
    "model",
    "fmt",
    "disabled",

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
  protected String path;
  protected String form;
  protected String model;

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
    var formCtx = getFormContext(ctx);
    model = getModel(ctx, formCtx);
    form = getForm(ctx, formCtx);

    path = getStringReq(ctx, "path");
    _id = TagUtils.toTagId(path);

    var isValid = !Objects.equals(form, getModelState(ctx).getForm()) || getModelState(ctx).isValid(path);
    var handlingValue = getHandlingValue(ctx, isValid);

    var fmt = getString(ctx, "fmt");
    _handlingValue = formatValue(ctx, handlingValue, fmt);
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    HtmlUtils.escAttribute(out, "id", _id);
    HtmlUtils.escAttribute(out, "type", "hidden");
    HtmlUtils.escAttribute(out, "name", path);

    if (_handlingValue != null) {
      HtmlUtils.escAttribute(out, "value", _handlingValue);
    }

    if (getBool(ctx, "disabled", false)) {
      HtmlUtils.disabled(out);
    }

    if (form != null) {
      HtmlUtils.escAttribute(out, "form", form);
    }

    writeAttribute(ctx, out, "datatag", "data-tag");
  }

  @Override
  protected boolean hasClosing() {
    return false;
  }

  @Override
  protected void writeTag(FacesContext ctx, ResponseWriter out) throws IOException {
    newLine(out);
    super.writeTag(ctx, out);
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
  }
}
