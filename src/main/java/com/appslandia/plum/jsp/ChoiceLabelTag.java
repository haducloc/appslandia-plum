// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;

import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "choiceLabel", bodyContent = "scriptless")
public class ChoiceLabelTag extends UITagBase {

  protected Object value;
  protected String labelKey;

  protected String _path;
  protected String _for;

  @Override
  protected String getTagName() {
    return "label";
  }

  protected String getForChoiceId(boolean multi) {
    var id = TagUtils.toTagId(_path);
    if (multi) {
      id = id + "_" + TagUtils.toIdPart(value);
    }
    return id;
  }

  @Override
  protected void initTag() throws JspException, IOException {
    var choiceBox = findParent(ChoiceGroupTag.class);
    Asserts.notNull(choiceBox, "No ChoiceGroupTag parent found.");

    _path = choiceBox.path;
    value = formatValue(value, choiceBox.fmt);
    _for = getForChoiceId(choiceBox.multi);
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (id != null) {
      HtmlUtils.escAttribute(out, "id", id);
    }

    HtmlUtils.escAttribute(out, "for", _for);

    if (hidden) {
      HtmlUtils.hidden(out);
    }

    if (datatag != null) {
      HtmlUtils.escAttribute(out, "data-tag", datatag.toString());
    }
    if (clazz != null) {
      HtmlUtils.escAttribute(out, "class", clazz);
    }
    if (style != null) {
      HtmlUtils.escAttribute(out, "style", style);
    }
    if (title != null) {
      HtmlUtils.escAttribute(out, "title", title);
    }
  }

  @Override
  protected boolean hasClosing() {
    return true;
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
    if (labelKey != null) {
      var label = getRequestContext().res(labelKey);
      XmlEscaper.escapeContent(out, label);
    } else if (body != null) {
      body.invoke(out);
    } else {
      throw new JspException("Couldn't determine the label content.");
    }
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setValue(Object value) {
    this.value = value;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setLabelKey(String labelKey) {
    this.labelKey = labelKey;
  }
}
