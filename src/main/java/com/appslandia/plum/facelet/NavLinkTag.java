// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;
import java.util.Set;

import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.faces.FacesException;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

/**
 *
 * @author Loc Ha
 *
 */
// @formatter:off
@Tag(name = "navLink", bodyContent = true, attributes = {
  @Attribute(name = "labelKey", type = String.class),
  @Attribute(name = "target", type = String.class),
  @Attribute(name = "rel", type = String.class),
  @Attribute(name = "controller", type = String.class),
  @Attribute(name = "action", type = String.class, required = true),
  @Attribute(name = "absUrl", type = Boolean.class),
  @Attribute(name = "disabled", type = Boolean.class),
  @Attribute(name = "inTopNav", type = Boolean.class),

  @Attribute(name = "id", type = String.class),
  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(NavLinkTag.COMPONENT_TYPE)
public class NavLinkTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.NavLinkTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = Set.of(
    "labelKey",
    "target",
    "rel",
    "controller",
    "action",
    "absUrl",
    "disabled",
    "inTopNav",

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
    return "a";
  }

  // Fields
  protected boolean disabled;
  protected String _url;
  protected String _clazz;
  protected boolean _current;

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    // action/controller
    var action = getStringReq(ctx, "action");

    var controller = getString(ctx, "controller");
    if (controller == null) {
      controller = getRequestContext(ctx).getActionDesc().getController();
    }
    _current = getBool(ctx, "inTopNav", true) && getRequestContext(ctx).isRoute(controller, action);

    // URL
    var absUrl = getBool(ctx, "absUrl", false);
    var parameters = getDynamicParameters(ctx);

    var url = getActionParser(ctx).toActionUrl(getRequest(ctx), controller, action, parameters, absUrl);
    _url = getResponse(ctx).encodeURL(url);

    disabled = getBool(ctx, "disabled", false);
    _clazz = getString(ctx, "clazz");

    if (disabled) {
      _clazz = (_clazz == null) ? "disabled" : _clazz + " disabled";

    } else if (_current) {
      _clazz = (_clazz == null) ? "active" : _clazz + " active";
    }
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    var id = getId();
    if (!isGeneratedId(id)) {
      HtmlUtils.escAttribute(out, "id", id);
    }

    if (!disabled) {
      HtmlUtils.escAttribute(out, "href", _url);
    }

    writeAttribute(ctx, out, "target");
    writeAttribute(ctx, out, "rel");

    if (disabled) {
      out.write(" aria-disabled=\"true\" tabindex=\"-1\"");

    } else if (_current) {
      out.write(" aria-current=\"page\"");
    }

    if (getBool(ctx, "hidden", false)) {
      HtmlUtils.hidden(out);
    }

    writeAttribute(ctx, out, "datatag", "data-tag");
    if (_clazz != null) {
      HtmlUtils.escAttribute(out, "class", _clazz);
    }
    writeAttribute(ctx, out, "style");
    writeAttribute(ctx, out, "title");
  }

  @Override
  protected boolean hasClosing() {
    return true;
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
    var labelKey = getString(ctx, "labelKey");
    if (labelKey != null) {
      var label = getRequestContext(ctx).res(labelKey);
      XmlEscaper.escapeContent(out, label);
    } else if (invokeBody(ctx)) {
    } else {
      throw new FacesException("Couldn't determine the link label.");
    }
  }
}
