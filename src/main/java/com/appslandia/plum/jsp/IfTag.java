// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;

import jakarta.servlet.jsp.JspException;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "if", dynamicAttributes = false, bodyContent = "scriptless")
public class IfTag extends TagBase {

  protected boolean test;

  @Override
  public void doTag() throws JspException, IOException {
    if (!rendered || !test) {
      return;
    }
    if (body != null) {
      body.invoke(null);
    }
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setTest(boolean test) {
    this.test = test;
  }
}
