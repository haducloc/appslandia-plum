// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Set;

import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.plum.base.PagerModel;
import com.appslandia.plum.base.SortModel;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

/**
 *
 * @author Loc Ha
 *
 */
// @formatter:off
@Tag(name = "sortTh", bodyContent = true, attributes = {
  @Attribute(name = "controller", type = String.class),
  @Attribute(name = "action", type = String.class, required = true),
  @Attribute(name = "absUrl", type = Boolean.class),

  @Attribute(name = "sortModel", type = SortModel.class, required = true),
  @Attribute(name = "pagerModel", type = PagerModel.class),
  @Attribute(name = "sortBy", type = String.class, required = true),
  @Attribute(name = "linkCss", type = String.class),

  @Attribute(name = "id", type = String.class),
  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(SortThTag.COMPONENT_TYPE)
public class SortThTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.SortThTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.toUnmodifiableSet(
    "controller",
    "action",
    "absUrl",

    "sortModel",
    "pagerModel",
    "sortBy",
    "linkCss",

    "id",
    "clazz",
    "style",
    "title",
    "hidden",
    "datatag",
    "rendered"
  );
  // @formatter:on

  @Override
  public Set<String> getTaglibAttributes() {
    return TAGLIB_ATTRS;
  }

  @Override
  protected String getTagName() {
    return "th";
  }

  protected SortModel sortModel;
  protected String sortBy;
  protected String _url;

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    sortBy = getStringReq(ctx, "sortBy");
    sortModel = (SortModel) getValueReq(ctx, "sortModel");
    var pagerModel = (PagerModel) getValue(ctx, "pagerModel");

    // URL
    var action = getStringReq(ctx, "action");
    var controller = getString(ctx, "controller");

    if (controller == null) {
      controller = getRequestContext(ctx).getActionDesc().getController();
    }
    var absUrl = getBool(ctx, "absUrl", false);

    var params = getDynamicParameters(ctx);
    if (params == null) {
      params = new LinkedHashMap<>();
    }

    params.put(SortModel.PARAM_SORT_BY, sortBy);
    params.put(SortModel.PARAM_SORT_ASC, sortModel.nextSortAsc(sortBy));

    if (pagerModel != null) {
      params.put(PagerModel.PARAM_PAGE_INDEX, pagerModel.getState().getPageIndex());
      params.put(PagerModel.PARAM_RECORD_COUNT, pagerModel.getRecordCount());
    }

    var url = getActionParser(ctx).toActionUrl(getRequest(ctx), controller, action, params, absUrl);
    _url = getResponse(ctx).encodeURL(url);

    // Class
    var clazz = getString(ctx, "clazz");
    if (clazz == null) {
      clazz = sortModel.getSortClass(sortBy);
    } else {
      clazz = clazz + " " + sortModel.getSortClass(sortBy);
    }
    getAttributes().put("clazz", clazz);
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    var id = getId();
    if (!isGeneratedId(id)) {
      HtmlUtils.escAttribute(out, "id", id);
    }

    out.write(" scope=\"col\"");

    if (sortModel.isCurrentSort(sortBy)) {
      out.write(sortModel.getState().isSortAsc() ? " aria-sort=\"ascending\"" : " aria-sort=\"descending\"");
    } else {
      out.write(" aria-sort=\"none\"");
    }

    if (getBool(ctx, "hidden", false)) {
      HtmlUtils.hidden(out);
    }

    writeAttribute(ctx, out, "datatag", "data-tag");
    writeAttribute(ctx, out, "clazz", "class");
    writeAttribute(ctx, out, "style");
    writeAttribute(ctx, out, "title");
  }

  @Override
  protected boolean hasClosing() {
    return true;
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
    out.write("<a");

    var linkCss = getString(ctx, "linkCss");
    if (linkCss != null) {
      HtmlUtils.escAttribute(out, "class", linkCss);
    }

    HtmlUtils.escAttribute(out, "href", _url);
    out.write('>');

    // Caption
    invokeBody(ctx);

    out.write("</a>");
  }

  @Override
  protected void writeTag(FacesContext ctx, ResponseWriter out) throws IOException {
    newLine(out);
    super.writeTag(ctx, out);
  }
}
