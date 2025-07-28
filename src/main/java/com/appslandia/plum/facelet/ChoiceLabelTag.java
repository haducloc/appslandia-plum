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
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.unmodifiableSet(
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

  protected String buildTagId(FacesContext ctx, boolean multiple) {
    var id = TagUtils.toTagId(this._path);
    if (multiple) {
      id = id + "_" + TagUtils.toIdPart(this.value);
    }
    return id;
  }

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    // choiceBox
    var choiceBox = findParent(ChoiceBoxTag.class);
    Asserts.notNull(choiceBox, "No ChoiceBoxTag found.");

    this._path = choiceBox.path;
    var value = getValue(ctx, "value");
    this.value = formatInputValue(ctx, value, choiceBox.formatter);
    this._for = buildTagId(ctx, choiceBox.multiple);
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    writeIdAttribute(ctx, out);
    HtmlUtils.escAttribute(out, "for", this._for);

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
      var label = this.getRequestContext(ctx).res(labelKey);
      XmlEscaper.escapeContent(out, label);
    } else if (invokeBody(ctx)) {
    } else {
      throw new FacesException("Couldn't determine the choice label content.");
    }
  }
}
