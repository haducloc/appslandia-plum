// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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

        pageContext.setAttribute(VAR_INDEX, index, PageContext.PAGE_SCOPE);
        pageContext.setAttribute(VAR_BEGIN, index == 0, PageContext.PAGE_SCOPE);
        pageContext.setAttribute(VAR_END, !iter.hasNext(), PageContext.PAGE_SCOPE);

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
