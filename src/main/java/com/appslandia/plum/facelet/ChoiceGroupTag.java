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
// @formatter:off
@Tag(name = "choiceGroup", bodyContent = true, attributes = {
  @Attribute(name = "path", type = String.class, required = true),
  @Attribute(name = "type", type = String.class, description = "checkbox|radio"),

  @Attribute(name = "model", type = String.class),
  @Attribute(name = "form", type = String.class),
  @Attribute(name = "multi", type = Boolean.class),
  @Attribute(name = "fmt", type = String.class, description = "Formatter ID"),

  @Attribute(name = "hasFieldError", type = Boolean.class, description = "Field error is defined"),
  @Attribute(name = "describedby", type = String.class, description = "aria-describedby"),
  @Attribute(name = "role", type = String.class, description = "group|radiogroup"),
  @Attribute(name = "labelledby", type = String.class, description = "aria-labelledby"),

  @Attribute(name = "id", type = String.class),
  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(ChoiceGroupTag.COMPONENT_TYPE)
public class ChoiceGroupTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.ChoiceGroupTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.toUnmodifiableSet(
    "path",
    "type",

    "model",
    "form",
    "multi",
    "fmt",

    "hasFieldError",
    "describedby",
    "role",
    "labelledby",

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
  protected String type;

  protected String model;
  protected String form;
  protected boolean multi;
  protected String fmt;

  protected boolean _isValid;

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    path = getStringReq(ctx, "path");
    type = getString(ctx, "type", "checkbox");
    Arguments.isTrue("checkbox".equals(type) || "radio".equals(type), "type must be either checkbox or radio.");

    var formCtx = getFormContext(ctx);
    model = getModel(ctx, formCtx);
    form = getForm(ctx, formCtx);
    _isValid = !Objects.equals(form, getModelState(ctx).getForm()) || getModelState(ctx).isValid(path);

    // Class
    if (!_isValid) {
      var errClass = TagUtils.CSS_FIELD_ERROR;
      var clazz = getString(ctx, "clazz");
      clazz = (clazz == null) ? errClass : clazz + " " + errClass;
      getAttributes().put("clazz", clazz);
    }

    fmt = getString(ctx, "fmt");
    multi = getBool(ctx, "multi", false);
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    var id = getId();
    if (!isGeneratedId(id)) {
      HtmlUtils.escAttribute(out, "id", id);
    }

    if (getBool(ctx, "hidden", false)) {
      HtmlUtils.hidden(out);
    }

    writeAttribute(ctx, out, "role");
    writeAttribute(ctx, out, "labelledby", "aria-labelledby");

    if (multi && !_isValid) {
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
  protected void writeTag(FacesContext ctx, ResponseWriter out) throws IOException {
    newLine(out);
    super.writeTag(ctx, out);
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
    invokeBody(ctx);
  }
}
