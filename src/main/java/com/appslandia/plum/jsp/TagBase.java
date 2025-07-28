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

import com.appslandia.common.base.StringOutput;
import com.appslandia.common.converters.Converter;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.base.ActionParser;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.ElProcessorPool;
import com.appslandia.plum.base.ModelState;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.tags.FormContext;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.el.ELProcessor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.JspFragment;
import jakarta.servlet.jsp.tagext.JspTag;
import jakarta.servlet.jsp.tagext.SimpleTag;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class TagBase implements SimpleTag {

  protected JspTag parent;
  protected PageContext pageContext;
  protected JspFragment body;

  protected boolean rendered = true;

  protected String evalBody() throws JspException, IOException {
    if (this.body != null) {
      var out = new StringOutput();
      this.body.invoke(out);
      return out.toString();
    }
    return StringUtils.EMPTY_STRING;
  }

  protected <T extends JspTag> T findParent(Class<T> parentClass) {
    var cur = this.parent;
    while (cur != null) {
      if (parentClass.isInstance(cur)) {
        return parentClass.cast(cur);
      } else if (cur instanceof SimpleTag) {
        cur = ((SimpleTag) cur).getParent();
      } else {
        break;
      }
    }
    return null;
  }

  protected Object evalPath(String modelVar, String path) {
    var request = getRequest();
    var model = request.getAttribute(modelVar);
    var pool = ServletUtils.getAppScoped(request, ElProcessorPool.class);

    ELProcessor el = null;
    try {
      el = pool.obtain();
      el.defineBean(modelVar, model);
      return el.eval(modelVar + "." + path);

    } finally {
      if (el != null) {
        el.defineBean(modelVar, null);
        pool.release(el);
      }
    }
  }

  public String formatInputValue(Object value, String formatter) {
    if (value == null) {
      return null;
    }
    if (value instanceof String str) {
      return str;
    }

    var reqCtx = getRequestContext();
    Converter<Object> converter = null;

    if (formatter != null) {
      converter = reqCtx.getConverterProvider().getConverter(formatter);
    } else if (reqCtx.getConverterProvider().hasConverter(value.getClass())) {
      converter = reqCtx.getConverterProvider().getConverter(value.getClass());
    }

    return (converter != null) ? converter.format(value, reqCtx.getFormatProvider(), false) : value.toString();
  }

  public FormContext getFormContext() {
    return (FormContext) this.pageContext.getAttribute(FormContext.ATTRIBUTE_FORM_CONTEXT);
  }

  public boolean isInFormContext() {
    return this.pageContext.getAttribute(FormContext.ATTRIBUTE_FORM_CONTEXT) != null;
  }

  public PageContext getPageContext() {
    return this.pageContext;
  }

  public HttpServletRequest getRequest() {
    return (HttpServletRequest) this.pageContext.getRequest();
  }

  public HttpServletResponse getResponse() {
    return (HttpServletResponse) this.pageContext.getResponse();
  }

  public RequestContext getRequestContext() {
    return ServletUtils.getRequestContext(this.pageContext.getRequest());
  }

  public ModelState getModelState() {
    return ServletUtils.getModelState(this.pageContext.getRequest());
  }

  public AppConfig getAppConfig() {
    return ServletUtils.getAppScoped(this.pageContext.getServletContext(), AppConfig.class);
  }

  public ActionParser getActionParser() {
    return ServletUtils.getAppScoped(this.pageContext.getServletContext(), ActionParser.class);
  }

  @Override
  public void setParent(JspTag parent) {
    this.parent = parent;
  }

  @Override
  public JspTag getParent() {
    return this.parent;
  }

  @Override
  public void setJspContext(JspContext pc) {
    this.pageContext = (PageContext) pc;
  }

  @Override
  public void setJspBody(JspFragment body) {
    this.body = body;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setRendered(boolean rendered) {
    this.rendered = rendered;
  }
}
