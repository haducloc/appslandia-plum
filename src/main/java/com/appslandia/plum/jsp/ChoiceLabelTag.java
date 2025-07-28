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

  protected String getTagId(boolean multiple) {
    var id = TagUtils.toTagId(this._path);
    if (multiple) {
      id = id + "_" + TagUtils.toIdPart(this.value);
    }
    return id;
  }

  @Override
  protected void initTag() throws JspException, IOException {
    // choiceBox
    var choiceBox = findParent(ChoiceBoxTag.class);
    Asserts.notNull(choiceBox, "No ChoiceBoxTag found.");

    this._path = choiceBox.path;
    this.value = formatInputValue(this.value, choiceBox.formatter);
    this._for = getTagId(choiceBox.multiple);
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (this.id != null) {
      HtmlUtils.escAttribute(out, "id", this.id);
    }
    HtmlUtils.escAttribute(out, "for", this._for);

    if (this.hidden) {
      HtmlUtils.hidden(out);
    }

    if (this.datatag != null) {
      HtmlUtils.escAttribute(out, "data-tag", this.datatag.toString());
    }
    if (this.clazz != null) {
      HtmlUtils.escAttribute(out, "class", this.clazz);
    }
    if (this.style != null) {
      HtmlUtils.escAttribute(out, "style", this.style);
    }
    if (this.title != null) {
      HtmlUtils.escAttribute(out, "title", this.title);
    }
  }

  @Override
  protected boolean hasClosing() {
    return true;
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
    if (this.labelKey != null) {
      var label = this.getRequestContext().res(this.labelKey);
      XmlEscaper.escapeContent(out, label);
    } else if (this.body != null) {
      this.body.invoke(out);
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
