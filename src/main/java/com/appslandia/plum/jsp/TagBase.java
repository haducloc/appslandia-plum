// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;

import com.appslandia.common.base.StringOutput;
import com.appslandia.common.converters.Converter;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.base.ActionParser;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.BasicHtmlSanitizer;
import com.appslandia.plum.base.ElProcessorPool;
import com.appslandia.plum.base.HtmlSymbolProvider;
import com.appslandia.plum.base.ModelState;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.tags.FormContext;
import com.appslandia.plum.utils.ServletUtils;

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
    if (body != null) {
      var out = new StringOutput();
      body.invoke(out);
      return out.toString();
    }
    return StringUtils.EMPTY_STRING;
  }

  protected <T extends JspTag> T findParent(Class<T> parentClass) {
    var cur = parent;
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
    var elPool = ServletUtils.getAppScoped(request, ElProcessorPool.class);

    return elPool.execute(el -> {
      el.defineBean(modelVar, model);
      return el.eval(modelVar + "." + path);
    });
  }

  public String formatValue(Object value, String formatter) {
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

  public String getModel(String model, FormContext formCtx) {
    if (model != null) {
      return model;
    }
    return (formCtx != null && formCtx.getModel() != null) ? formCtx.getModel() : ServletUtils.REQUEST_ATTRIBUTE_MODEL;
  }

  public String getForm(String form, FormContext formCtx) {
    if (form != null) {
      return form;
    }
    return (formCtx != null) ? formCtx.getForm() : null;
  }

  public FormContext getFormContext() {
    return (FormContext) pageContext.getAttribute(FormContext.ATTRIBUTE_FORM_CONTEXT);
  }

  public PageContext getPageContext() {
    return pageContext;
  }

  public HttpServletRequest getRequest() {
    return (HttpServletRequest) pageContext.getRequest();
  }

  public HttpServletResponse getResponse() {
    return (HttpServletResponse) pageContext.getResponse();
  }

  public RequestContext getRequestContext() {
    return ServletUtils.getRequestContext(pageContext.getRequest());
  }

  public ModelState getModelState() {
    return ServletUtils.getModelState(pageContext.getRequest());
  }

  public AppConfig getAppConfig() {
    return ServletUtils.getAppScoped(pageContext.getServletContext(), AppConfig.class);
  }

  public ActionParser getActionParser() {
    return ServletUtils.getAppScoped(pageContext.getServletContext(), ActionParser.class);
  }

  public BasicHtmlSanitizer getBasicHtmlSanitizer() {
    return ServletUtils.getAppScoped(pageContext.getServletContext(), BasicHtmlSanitizer.class);
  }

  public HtmlSymbolProvider getHtmlSymbolProvider() {
    return ServletUtils.getAppScoped(pageContext.getServletContext(), HtmlSymbolProvider.class);
  }

  @Override
  public void setParent(JspTag parent) {
    this.parent = parent;
  }

  @Override
  public JspTag getParent() {
    return parent;
  }

  @Override
  public void setJspContext(JspContext pc) {
    pageContext = (PageContext) pc;
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
