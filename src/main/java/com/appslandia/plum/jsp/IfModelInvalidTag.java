// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.util.Objects;

import jakarta.servlet.jsp.JspException;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "ifModelInvalid", dynamicAttributes = false, bodyContent = "scriptless")
public class IfModelInvalidTag extends TagBase {

  protected String form;

  @Override
  public void doTag() throws JspException, IOException {
    if (!rendered) {
      return;
    }

    var isValid = !Objects.equals(form, getModelState().getForm()) || getModelState().isValid();
    if (!isValid) {
      body.invoke(null);
    }
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setForm(String form) {
    this.form = form;
  }
}
