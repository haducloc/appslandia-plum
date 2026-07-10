// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.util.Objects;

import com.appslandia.common.utils.Arguments;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "choiceGroup", bodyContent = "scriptless")
public class ChoiceGroupTag extends UITagBase {

  protected String path;
  protected String type = "checkbox";

  protected String model;
  protected String form;
  protected boolean multi = false;
  protected String fmt;

  protected boolean hasFieldError;
  protected String describedby;
  protected String role;
  protected String labelledby;

  protected boolean _isValid;

  @Override
  protected String getTagName() {
    return "div";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    Arguments.notNull(path);
    Arguments.isTrue("checkbox".equals(type) || "radio".equals(type), "type must be either checkbox or radio.");

    var formCtx = getFormContext();
    model = getModel(model, formCtx);
    form = getForm(form, formCtx);
    _isValid = !Objects.equals(form, getModelState().getForm()) || getModelState().isValid(path);

    // Class
    if (!_isValid) {
      var errClass = TagUtils.CSS_FIELD_ERROR;
      clazz = (clazz == null) ? errClass : clazz + " " + errClass;
    }
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (id != null) {
      HtmlUtils.escAttribute(out, "id", id);
    }

    if (hidden) {
      HtmlUtils.hidden(out);
    }

    // aria-labelledby
    if (role != null) {
      HtmlUtils.escAttribute(out, "role", role);
    }
    if (labelledby != null) {
      HtmlUtils.escAttribute(out, "aria-labelledby", labelledby);
    }

    if (multi && !_isValid) {
      out.write(" aria-invalid=\"true\"");
    }

    // aria-describedby
    writeDescribedby(out);

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

  protected void writeDescribedby(JspWriter out) throws IOException {
    var descBy = this.describedby;

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
  protected void writeTag(JspWriter out) throws JspException, IOException {
    out.newLine();
    super.writeTag(out);
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
    if (body != null) {
      body.invoke(out);
    }
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setPath(String path) {
    this.path = path;
  }

  @Attribute(rtexprvalue = true, required = false, description = "checkbox|radio")
  public void setType(String type) {
    this.type = type;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setModel(String model) {
    this.model = model;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setForm(String form) {
    this.form = form;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setMulti(boolean multi) {
    this.multi = multi;
  }

  @Attribute(rtexprvalue = true, required = false, description = "Formatter ID")
  public void setFmt(String fmt) {
    this.fmt = fmt;
  }

  @Attribute(rtexprvalue = true, required = false, description = "Field error is defined")
  public void setHasFieldError(boolean hasFieldError) {
    this.hasFieldError = hasFieldError;
  }

  @Attribute(rtexprvalue = true, required = false, description = "aria-describedby")
  public void setDescribedby(String describedby) {
    this.describedby = describedby;
  }

  @Attribute(rtexprvalue = true, required = false, description = "group|radiogroup")
  public void setRole(String role) {
    this.role = role;
  }

  @Attribute(rtexprvalue = true, required = false, description = "aria-labelledby")
  public void setLabelledby(String labelledby) {
    this.labelledby = labelledby;
  }
}
