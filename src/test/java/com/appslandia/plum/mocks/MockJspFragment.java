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
    for (SimpleTag tag : this.tags) {
      if (!(tag instanceof SimpleTag)) {
        continue;
      }
      var t = tag;
      t.setParent(parent);
      t.setJspContext(this.jspContext);
    }
  }

  @Override
  public void invoke(Writer out) throws JspException, IOException {
    if (this.tags != null) {
      for (SimpleTag tag : this.tags) {
        tag.doTag();
      }
      return;
    }
    if (this.bodyContent != null) {
      if (out != null) {
        out.write(this.bodyContent);
      } else {
        jspContext.getOut().write(this.bodyContent);
      }
      return;
    }
  }

  @Override
  public JspContext getJspContext() {
    return this.jspContext;
  }
}
