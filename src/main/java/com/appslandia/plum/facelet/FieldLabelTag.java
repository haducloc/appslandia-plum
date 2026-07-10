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

import jakarta.faces.FacesException;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

/**
 *
 * @author Loc Ha
 *
 */
// @formatter:off
@Tag(name = "label", bodyContent = true, attributes = {
  @Attribute(name = "path", type = String.class, required = true),
  @Attribute(name = "asDiv", type = Boolean.class),
  @Attribute(name = "form", type = String.class),
  @Attribute(name = "labelKey", type = String.class),
  @Attribute(name = "required", type = Boolean.class),

  @Attribute(name = "id", type = String.class),
  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(FieldLabelTag.COMPONENT_TYPE)
public class FieldLabelTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.FieldLabelTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.toUnmodifiableSet(
    "path",
    "asDiv",
    "form",
    "labelKey",
    "required",

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
    return asDiv ? "div" : "label";
  }

  // Fields
  protected boolean asDiv;
  protected String form;
  protected String _for;

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    var path = getStringReq(ctx, "path");

    var id = TagUtils.toFormLabelId(path);
    setId(id);

    var formCtx = getFormContext(ctx);
    form = getForm(ctx, formCtx);
    var isValid = !Objects.equals(form, getModelState(ctx).getForm()) || getModelState(ctx).isValid(path);

    // Required Class
    var clazz = getString(ctx, "clazz");
    if (getBool(ctx, "required", false)) {

      var reqClass = TagUtils.CSS_LABEL_REQUIRED;
      clazz = (clazz == null) ? reqClass : clazz + " " + reqClass;
    }

    // Error Class
    if (!isValid) {
      var errClass = TagUtils.CSS_LABEL_ERROR;
      clazz = (clazz == null) ? errClass : clazz + " " + errClass;
    }
    getAttributes().put("clazz", clazz);

    asDiv = getBool(ctx, "asDiv", false);
    if (!asDiv) {
      _for = TagUtils.toTagId(path);
    }
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    HtmlUtils.escAttribute(out, "id", getId());

    if (_for != null) {
      HtmlUtils.escAttribute(out, "for", _for);
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
    return true;
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
    var labelKey = getString(ctx, "labelKey");
    if (labelKey != null) {
      var label = getRequestContext(ctx).res(labelKey);
      XmlEscaper.escapeContent(out, label);
    } else if (invokeBody(ctx)) {
    } else {
      throw new FacesException("Couldn't determine the label content.");
    }
  }
}
