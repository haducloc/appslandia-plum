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

import com.appslandia.common.utils.Asserts;

import jakarta.el.ExpressionFactory;
import jakarta.servlet.jsp.JspFactory;
import jakarta.servlet.jsp.PageContext;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class ExpressionEvaluator {

  private static volatile ExpressionEvaluator __default;
  private static final Object MUTEX = new Object();

  public static ExpressionEvaluator getDefault() {
    ExpressionEvaluator obj = __default;
    if (obj == null) {
      synchronized (MUTEX) {
        if ((obj = __default) == null) {
          __default = obj = new ExpressionEvaluatorImpl();
        }
      }
    }
    return obj;
  }

  public static void setDefault(ExpressionEvaluator impl) {
    Asserts.isNull(__default, "ExpressionEvaluator.__default must be null.");

    if (__default == null) {
      synchronized (MUTEX) {
        if (__default == null) {
          __default = impl;
          return;
        }
      }
    }
  }

  public abstract Object getValue(PageContext pc, String property);

  private static class ExpressionEvaluatorImpl extends ExpressionEvaluator {

    private static final JspFactory JSP_FACTORY = JspFactory.getDefaultFactory();

    @SuppressWarnings("el-syntax")
    @Override
    public Object getValue(PageContext pc, String property) {
      String expr = "${" + property + "}";
      return getExpressionFactory(pc).createValueExpression(pc.getELContext(), expr, Object.class)
          .getValue(pc.getELContext());
    }

    protected ExpressionFactory getExpressionFactory(PageContext pc) {
      return JSP_FACTORY.getJspApplicationContext(pc.getServletContext()).getExpressionFactory();
    }
  }
}
