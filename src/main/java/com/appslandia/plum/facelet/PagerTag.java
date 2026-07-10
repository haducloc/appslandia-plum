// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.PagerModel;
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
@Tag(name = "pager", bodyContent = false, attributes = {
  @Attribute(name = "controller", type = String.class),
  @Attribute(name = "action", type = String.class, required = true),
  @Attribute(name = "absUrl", type = Boolean.class),

  @Attribute(name = "model", type = PagerModel.class, required = true),
  @Attribute(name = "itemClass", type = String.class),
  @Attribute(name = "linkClass", type = String.class),
  @Attribute(name = "prevLabel", type = String.class, required = true),
  @Attribute(name = "nextLabel", type = String.class, required = true),

  @Attribute(name = "id", type = String.class),
  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(PagerTag.COMPONENT_TYPE)
public class PagerTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.PagerTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.toUnmodifiableSet(
    "controller",
    "action",
    "absUrl",

    "model",
    "itemClass",
    "linkClass",
    "prevLabel",
    "nextLabel",

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
    return "ul";
  }

  protected PagerModel model;
  protected Map<String, Object> _parameters;

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    model = (PagerModel) getValueReq(ctx, "model");

    _parameters = getDynamicParameters(ctx);
    if (_parameters == null) {
      _parameters = new LinkedHashMap<>();
    }
    _parameters.put(PagerModel.PARAM_RECORD_COUNT, model.getRecordCount());

    // Class
    var clazz = getString(ctx, "clazz");
    if (clazz == null) {
      clazz = "pagination";
    }
    getAttributes().put("clazz", clazz);
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    var id = getId();
    if (!isGeneratedId(id)) {
      HtmlUtils.escAttribute(out, "id", id);
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

  protected String toPageLinkUrl(FacesContext ctx, int index) {
    _parameters.put(PagerModel.PARAM_PAGE_INDEX, index);

    var action = getStringReq(ctx, "action");
    var controller = getString(ctx, "controller");

    if (controller == null) {
      controller = getRequestContext(ctx).getActionDesc().getController();
    }

    var absUrl = getBool(ctx, "absUrl", false);
    var url = getActionParser(ctx).toActionUrl(getRequest(ctx), controller, action, _parameters, absUrl);

    return getResponse(ctx).encodeURL(url);
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
    var provider = getHtmlSymbolProvider(ctx);
    var hellip = provider.getHtmlSymbol("hellip");

    var itemClass = getString(ctx, "itemClass", "page-item");
    var linkClass = getString(ctx, "linkClass", "page-link");

    var prevLabel = getStringReq(ctx, "prevLabel");
    var nextLabel = getStringReq(ctx, "nextLabel");

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
        HtmlUtils.escAttribute(out, "href", toPageLinkUrl(ctx, item.getIndex()));
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
  protected void writeTag(FacesContext ctx, ResponseWriter out) throws IOException {
    newLine(out);
    super.writeTag(ctx, out);
  }
}
