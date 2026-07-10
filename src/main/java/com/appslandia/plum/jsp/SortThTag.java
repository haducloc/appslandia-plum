// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.appslandia.common.utils.Arguments;
import com.appslandia.plum.base.PagerModel;
import com.appslandia.plum.base.SortModel;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "sortTh", bodyContent = "scriptless")
public class SortThTag extends UITagBase {

  protected String controller;
  protected String action;
  protected boolean absUrl;

  protected PagerModel pagerModel;
  protected SortModel sortModel;

  protected String sortBy;
  protected String linkCss;

  protected Map<String, Object> _parameters;
  protected String _url;

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
    return "th";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    Arguments.notNull(action);
    Arguments.notNull(sortModel);
    Arguments.notNull(sortBy);

    if (controller == null) {
      controller = getRequestContext().getActionDesc().getController();
    }

    var params = getParams();
    params.put(SortModel.PARAM_SORT_BY, sortBy);
    params.put(SortModel.PARAM_SORT_ASC, sortModel.nextSortAsc(sortBy));

    if (pagerModel != null) {
      params.put(PagerModel.PARAM_PAGE_INDEX, pagerModel.getState().getPageIndex());
      params.put(PagerModel.PARAM_RECORD_COUNT, pagerModel.getRecordCount());
    }

    var url = getActionParser().toActionUrl(getRequest(), controller, action, _parameters, absUrl);
    _url = getResponse().encodeURL(url);

    // CSS
    if (clazz == null) {
      clazz = sortModel.getSortClass(sortBy);
    } else {
      clazz = clazz + " " + sortModel.getSortClass(sortBy);
    }
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (id != null) {
      HtmlUtils.escAttribute(out, "id", id);
    }
    out.write(" scope=\"col\"");

    if (sortModel.isCurrentSort(sortBy)) {
      out.write(sortModel.getState().isSortAsc() ? " aria-sort=\"ascending\"" : " aria-sort=\"descending\"");
    } else {
      out.write(" aria-sort=\"none\"");
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

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
    out.write("<a");

    if (linkCss != null) {
      HtmlUtils.escAttribute(out, "class", linkCss);
    }

    HtmlUtils.escAttribute(out, "href", _url);
    out.write('>');

    // caption
    if (body != null) {
      body.invoke(out);
    }
    out.write("</a>");
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
  public void setSortModel(SortModel sortModel) {
    this.sortModel = sortModel;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setPagerModel(PagerModel pagerModel) {
    this.pagerModel = pagerModel;
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setLinkCss(String linkCss) {
    this.linkCss = linkCss;
  }
}
