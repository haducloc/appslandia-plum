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
import java.util.Iterator;

import com.appslandia.common.utils.ObjectUtils;

import jakarta.servlet.jsp.JspException;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "iterate", dynamicAttributes = false, bodyContent = "scriptless")
public class IterateTag extends TagBase {

  protected Iterable<?> items;
  protected String var = "item";

  static final String VAR_INDEX = "index";
  static final String VAR_BEGIN = "begin";
  static final String VAR_END = "end";

  @Override
  public void doTag() throws JspException, IOException {
    if ((!this.rendered) || (this.items == null)) {
      return;
    }

    final var bakVar = this.pageContext.getAttribute(this.var);
    final var bakIndexVar = this.pageContext.getAttribute(VAR_INDEX);
    final var bakBeginVar = this.pageContext.getAttribute(VAR_BEGIN);
    final var bakEndVar = this.pageContext.getAttribute(VAR_END);

    try {
      var index = -1;
      Iterator<?> iter = this.items.iterator();

      while (iter.hasNext()) {
        Object item = iter.next();

        this.pageContext.setAttribute(this.var, item);
        index += 1;

        this.pageContext.setAttribute(VAR_INDEX, index);
        this.pageContext.setAttribute(VAR_BEGIN, index == 0);
        this.pageContext.setAttribute(VAR_END, !iter.hasNext());

        if (this.body != null) {
          this.body.invoke(null);
        }
      }
    } finally {
      this.pageContext.setAttribute(this.var, bakVar);
      this.pageContext.setAttribute(VAR_INDEX, bakIndexVar);
      this.pageContext.setAttribute(VAR_BEGIN, bakBeginVar);
      this.pageContext.setAttribute(VAR_END, bakEndVar);
    }
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setItems(Object items) {
    this.items = ObjectUtils.toIterable(items);
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setVar(String var) {
    this.var = var;
  }
}
