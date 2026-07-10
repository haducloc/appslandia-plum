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
@Tag(name = "fieldError", attributes = {
  @Attribute(name = "path", type = String.class, required = true),
  @Attribute(name = "form", type = String.class),
  @Attribute(name = "role", type = String.class, description = "alert"),

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
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.toUnmodifiableSet(
    "path",
    "form",
    "role",

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
    path = getStringReq(ctx, "path");

    var id = TagUtils.toFieldErrorId(path);
    setId(id);

    var formCtx = getFormContext(ctx);
    form = getForm(ctx, formCtx);
    var isValid = !Objects.equals(form, getModelState(ctx).getForm()) || getModelState(ctx).isValid(path);

    // Class
    var clazz = getString(ctx, "clazz", TagUtils.CSS_ERROR_MSG);
    if (isValid) {
      clazz = clazz + " " + TagUtils.CSS_D_NONE;
    }

    getAttributes().put("clazz", clazz);
    _isValid = isValid;
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    HtmlUtils.escAttribute(out, "id", getId());

    if (getBool(ctx, "hidden", false)) {
      HtmlUtils.hidden(out);
    }

    writeAttribute(ctx, out, "role");

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
    if (_isValid) {
      return;
    }

    var error = getModelState(ctx).getFieldErrors(path).get(0);
    if (error.isEscXml()) {
      XmlEscaper.escapeContent(out, error.getText());
    } else {
      out.write(error.getText());
    }
  }
}
