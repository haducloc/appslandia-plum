// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;
import java.util.Set;

import com.appslandia.common.utils.Asserts;
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
@Tag(name = "choiceLabel", bodyContent = true, attributes = {
  @Attribute(name = "value", type = Object.class, required = true),
  @Attribute(name = "labelKey", type = String.class),

  @Attribute(name = "id", type = String.class),
  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(ChoiceLabelTag.COMPONENT_TYPE)
public class ChoiceLabelTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.ChoiceLabelTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.toUnmodifiableSet(
    "value",
    "labelKey",

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
    return "label";
  }

  // Fields
  protected Object value;
  protected String _path;
  protected String _for;

  protected String getForChoiceId(FacesContext ctx, boolean multi) {
    var id = TagUtils.toTagId(_path);
    if (multi) {
      id = id + "_" + TagUtils.toIdPart(value);
    }
    return id;
  }

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    var choiceBox = findParent(ChoiceGroupTag.class);
    Asserts.notNull(choiceBox, "No ChoiceGroupTag parent found.");

    _path = choiceBox.path;
    var value = getValue(ctx, "value");
    this.value = formatValue(ctx, value, choiceBox.fmt);
    _for = getForChoiceId(ctx, choiceBox.multi);
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    var id = getId();
    if (!isGeneratedId(id)) {
      HtmlUtils.escAttribute(out, "id", id);
    }

    HtmlUtils.escAttribute(out, "for", _for);

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
      throw new FacesException("Couldn't determine the choice label content.");
    }
  }
}
