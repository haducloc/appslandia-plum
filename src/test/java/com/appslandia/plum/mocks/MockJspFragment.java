// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mocks;

import java.io.IOException;
import java.io.Writer;

import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.JspFragment;
import jakarta.servlet.jsp.tagext.SimpleTag;

/**
 *
 * @author Loc Ha
 *
 */
public class MockJspFragment extends JspFragment {

  private JspContext jspContext;

  private String bodyContent;
  private SimpleTag[] tags;

  public MockJspFragment(JspContext jspContext) {
    this.jspContext = jspContext;
  }

  public MockJspFragment(JspContext jspContext, String bodyContent) {
    this.jspContext = jspContext;
    this.bodyContent = bodyContent;
  }

  public MockJspFragment(JspContext jspContext, SimpleTag parent, SimpleTag... tags) {
    this.jspContext = jspContext;
    this.tags = tags;

    initTags(parent);
  }

  protected void initTags(SimpleTag parent) {
    for (SimpleTag tag : tags) {
      if (!(tag instanceof SimpleTag)) {
        continue;
      }
      var t = tag;
      t.setParent(parent);
      t.setJspContext(jspContext);
    }
  }

  @Override
  public void invoke(Writer out) throws JspException, IOException {
    if (tags != null) {
      for (SimpleTag tag : tags) {
        tag.doTag();
      }
      return;
    }
    if (bodyContent != null) {
      if (out != null) {
        out.write(bodyContent);
      } else {
        jspContext.getOut().write(bodyContent);
      }
      return;
    }
  }

  @Override
  public JspContext getJspContext() {
    return jspContext;
  }
}
