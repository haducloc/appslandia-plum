// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.util.Objects;

import com.appslandia.common.utils.Arguments;
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
@Tag(name = "label", bodyContent = "scriptless")
public class FieldLabelTag extends UITagBase {

  protected String path;
  protected boolean asDiv;
  protected String form;
  protected String labelKey;
  protected boolean required;

  protected String _for;
  protected String _tag;

  @Override
  protected String getTagName() {
    return asDiv ? "div" : "label";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    Arguments.notNull(path);
    id = TagUtils.toFormLabelId(path);

    var formCtx = getFormContext();
    form = getForm(form, formCtx);
    var isValid = !Objects.equals(form, getModelState().getForm()) || getModelState().isValid(path);

    // Required Class
    var clazz = this.clazz;
    if (required) {
      var reqClass = TagUtils.CSS_LABEL_REQUIRED;
      clazz = (clazz == null) ? reqClass : clazz + " " + reqClass;
    }

    // Error Class
    if (!isValid) {
      var errClass = TagUtils.CSS_LABEL_ERROR;
      clazz = (clazz == null) ? errClass : clazz + " " + errClass;
    }
    this.clazz = clazz;

    if (!asDiv) {
      _for = TagUtils.toTagId(path);
    }
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    HtmlUtils.escAttribute(out, "id", id);

    if (_for != null) {
      HtmlUtils.escAttribute(out, "for", _for);
    }

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
  public void setPath(String path) {
    this.path = path;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAsDiv(boolean asDiv) {
    this.asDiv = asDiv;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setForm(String form) {
    this.form = form;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setLabelKey(String labelKey) {
    this.labelKey = labelKey;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setRequired(boolean required) {
    this.required = required;
  }
}
