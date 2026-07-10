// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.PagerModel;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "pager")
public class PagerTag extends UITagBase {

  protected String controller;
  protected String action;
  protected boolean absUrl;

  protected PagerModel model;
  protected String itemClass;
  protected String linkClass;

  protected String prevLabel;
  protected String nextLabel;

  protected Map<String, Object> _parameters;

  protected Map<String, Object> getParams() {
    if (_parameters == null) {
      return _parameters = new LinkedHashMap<>();
    }
    return _parameters;
  }

  @Override
  public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
    if (TagUtils.isDynamicParameter(name)) {
      getParams().put(TagUtils.getDynParamName(name), value);
    } else {
      super.setDynamicAttribute(uri, name, value);
    }
  }

  @Override
  protected String getTagName() {
    return "ul";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    Arguments.notNull(action);
    Arguments.notNull(model);
    Arguments.notNull(prevLabel);
    Arguments.notNull(nextLabel);

    if (controller == null) {
      controller = getRequestContext().getActionDesc().getController();
    }

    // Classes
    if (clazz == null) {
      clazz = "pagination";
    }
    if (itemClass == null) {
      itemClass = "page-item";
    }
    if (linkClass == null) {
      linkClass = "page-link";
    }

    // URL
    var params = getParams();
    params.put(PagerModel.PARAM_RECORD_COUNT, model.getRecordCount());
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (id != null) {
      HtmlUtils.escAttribute(out, "id", id);
    }

    if (hidden) {
      HtmlUtils.hidden(out);
    }

    if (datatag != null) {
      HtmlUtils.escAttribute(out, "data-tag", datatag.toString());
    }
    if (clazz != null) {
      HtmlUtils.escAttribute(out, "class", clazz);
    }
    if (style != null) {
      HtmlUtils.escAttribute(out, "style", style);
    }
    if (title != null) {
      HtmlUtils.escAttribute(out, "title", title);
    }
  }

  @Override
  protected boolean hasClosing() {
    return true;
  }

  protected String toPageLinkUrl(int index) {
    var params = getParams();
    params.put(PagerModel.PARAM_PAGE_INDEX, index);

    var url = getActionParser().toActionUrl(getRequest(), controller, action, params, absUrl);
    return getResponse().encodeURL(url);
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
    var provider = getHtmlSymbolProvider();
    var hellip = provider.getHtmlSymbol("hellip");

    for (var item : model.getItems()) {
      // li
      out.write("<li");

      if (item.isPageType()) {
        if (model.getState().getPageIndex() != item.getIndex()) {
          HtmlUtils.escAttribute(out, "class", itemClass);
        } else {
          var activeItemClass = itemClass + " active";

          HtmlUtils.escAttribute(out, "class", activeItemClass);
          out.write(" aria-current=\"page\"");
        }
      }
      out.write('>');

      // a
      out.write("<a");
      HtmlUtils.escAttribute(out, "class", linkClass);

      // URL
      if (item.isDotType()) {
        out.write(" href=\"#\"");
        out.write(" tabindex=\"-1\"");
        out.write(" aria-disabled=\"true\"");

      } else {
        HtmlUtils.escAttribute(out, "href", toPageLinkUrl(item.getIndex()));
      }
      out.write('>');

      if (item.isDotType()) {
        out.write(hellip.getCode());

      } else if (item.isIndexType()) {
        out.write(Integer.toString(item.getIndex()));

      } else if (item.isPrevType()) {
        XmlEscaper.escapeContent(out, prevLabel);

      } else if (item.isNextType()) {
        XmlEscaper.escapeContent(out, nextLabel);
      }

      out.write("</a>");
      out.write("</li>");
    }
  }

  @Override
  protected void writeTag(JspWriter out) throws JspException, IOException {
    out.newLine();
    super.writeTag(out);
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setController(String controller) {
    this.controller = controller;
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setAction(String action) {
    this.action = action;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAbsUrl(boolean absUrl) {
    this.absUrl = absUrl;
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setModel(PagerModel model) {
    this.model = model;
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setNextLabel(String nextLabel) {
    this.nextLabel = nextLabel;
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setPrevLabel(String prevLabel) {
    this.prevLabel = prevLabel;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setItemClass(String itemClass) {
    this.itemClass = itemClass;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setLinkClass(String linkClass) {
    this.linkClass = linkClass;
  }
}
