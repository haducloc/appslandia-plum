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

package com.appslandia.plum.facelet;

import java.io.IOException;
import java.util.Set;

import com.appslandia.common.utils.CollectionUtils;
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
@Tag(name = "actionImage", attributes = {
  @Attribute(name = "width", type = String.class),
  @Attribute(name = "height", type = String.class),
  @Attribute(name = "alt", type = String.class),
  @Attribute(name = "loading", type = String.class),
  @Attribute(name = "decoding", type = String.class),
  @Attribute(name = "fetchpriority", type = String.class),
  @Attribute(name = "controller", type = String.class),
  @Attribute(name = "action", type = String.class, required = true),
  @Attribute(name = "absUrl", type = Boolean.class),

  @Attribute(name = "id", type = String.class),
  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(ActionImageTag.COMPONENT_TYPE)
public class ActionImageTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.ActionImageTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.unmodifiableSet(
    "width",
    "height",
    "alt",
    "loading",
    "decoding",
    "fetchpriority",
    "controller",
    "action",
    "absUrl",

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
    return "img";
  }

  // Fields
  protected String _url;

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    // action/controller
    var action = getStringReq(ctx, "action");
    var controller = getString(ctx, "controller");
    if (controller == null) {
      controller = getRequestContext(ctx).getActionDesc().getController();
    }

    // URL
    var absUrl = getBool(ctx, "absUrl", false);
    var parameters = getDynamicParameters(ctx);
    var url = getActionParser(ctx).toActionUrl(getRequest(ctx), controller, action, parameters, absUrl);
    this._url = getResponse(ctx).encodeURL(url);
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    writeIdAttribute(ctx, out);
    HtmlUtils.escAttribute(out, "src", this._url);

    writeAttribute(ctx, out, "width");
    writeAttribute(ctx, out, "height");
    HtmlUtils.escAttribute(out, "alt", getString(ctx, "alt", ""));

    writeAttribute(ctx, out, "loading");
    writeAttribute(ctx, out, "decoding");
    writeAttribute(ctx, out, "fetchpriority");

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
    return false;
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
  }
}
