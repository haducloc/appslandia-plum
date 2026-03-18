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
import jakarta.servlet.jsp.PageContext;

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
    if ((!rendered) || (items == null)) {
      return;
    }

    final var bakVar = pageContext.getAttribute(var, PageContext.PAGE_SCOPE);
    final var bakIndexVar = pageContext.getAttribute(VAR_INDEX, PageContext.PAGE_SCOPE);
    final var bakBeginVar = pageContext.getAttribute(VAR_BEGIN, PageContext.PAGE_SCOPE);
    final var bakEndVar = pageContext.getAttribute(VAR_END, PageContext.PAGE_SCOPE);

    try {
      var index = -1;
      Iterator<?> iter = items.iterator();

      while (iter.hasNext()) {
        Object item = iter.next();

        pageContext.setAttribute(var, item);
        index += 1;

        pageContext.setAttribute(VAR_INDEX, index);
        pageContext.setAttribute(VAR_BEGIN, index == 0);
        pageContext.setAttribute(VAR_END, !iter.hasNext());

        if (body != null) {
          body.invoke(null);
        }
      }
    } finally {
      pageContext.setAttribute(var, bakVar, PageContext.PAGE_SCOPE);
      pageContext.setAttribute(VAR_INDEX, bakIndexVar, PageContext.PAGE_SCOPE);
      pageContext.setAttribute(VAR_BEGIN, bakBeginVar, PageContext.PAGE_SCOPE);
      pageContext.setAttribute(VAR_END, bakEndVar, PageContext.PAGE_SCOPE);
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
