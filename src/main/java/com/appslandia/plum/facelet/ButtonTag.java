// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;
import java.util.Set;

import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.XmlEscaper;
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
@Tag(name = "button", bodyContent = true, attributes = {
  @Attribute(name = "labelKey", type = String.class),
  @Attribute(name = "type", type = String.class),
  @Attribute(name = "name", type = String.class),
  @Attribute(name = "value", type = String.class),
  @Attribute(name = "form", type = String.class),

  @Attribute(name = "disabled", type = Boolean.class),
  @Attribute(name = "autofocus", type = Boolean.class),

  @Attribute(name = "id", type = String.class),
  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(ButtonTag.COMPONENT_TYPE)
public class ButtonTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.ButtonTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.toUnmodifiableSet(
    "labelKey",
    "type",
    "name",
    "value",
    "form",

    "disabled",
    "autofocus",

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
    return "button";
  }

  // Fields
  protected String form;

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    var formCtx = getFormContext(ctx);
    form = getForm(ctx, formCtx);
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    var id = getId();
    if (!isGeneratedId(id)) {
      HtmlUtils.escAttribute(out, "id", id);
    }

    HtmlUtils.escAttribute(out, "type", getString(ctx, "type", "button"));
    writeAttribute(ctx, out, "name");
    writeAttribute(ctx, out, "value");

    if (getBool(ctx, "disabled", false)) {
      HtmlUtils.disabled(out);
    }

    if (form != null) {
      HtmlUtils.escAttribute(out, "form", form);
    }

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
    return true;
  }

  @Override
  protected void writeTag(FacesContext ctx, ResponseWriter out) throws IOException {
    newLine(out);
    super.writeTag(ctx, out);
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
    var labelKey = getString(ctx, "labelKey");
    if (labelKey != null) {
      var label = getRequestContext(ctx).res(labelKey);
      XmlEscaper.escapeContent(out, label);

    } else if (invokeBody(ctx)) {
    } else {
      throw new FacesException("Couldn't determine the link label.");
    }
  }
}
