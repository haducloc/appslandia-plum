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
import java.util.List;
import java.util.Set;

import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.Message;
import com.appslandia.plum.base.Messages;
import com.appslandia.plum.tags.TagUtils;
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
@Tag(name = "messages", attributes = {
  @Attribute(name = "type", type = String.class, required = true),
  @Attribute(name = "listClass", type = String.class),
  @Attribute(name = "itemClass", type = String.class),

  @Attribute(name = "id", type = String.class),
  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(MessagesTag.COMPONENT_TYPE)
public class MessagesTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.MessagesTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.unmodifiableSet(
    "type",
    "listClass",
    "itemClass",

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
    return "div";
  }

  protected List<Message> _typedMessages;

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    var messages = (Messages) this.getRequest(ctx).getAttribute(Messages.REQUEST_ATTRIBUTE_ID);
    if (messages != null) {
      var type = getStringReq(ctx, "type");
      var typeId = Message.parseType(type);
      this._typedMessages = messages.stream().filter(m -> m.getType() == typeId).toList();
    }

    if ((this._typedMessages == null) || this._typedMessages.isEmpty()) {
      var dNoneClass = TagUtils.CSS_D_NONE;
      var clazz = getString(ctx, "clazz");
      clazz = (clazz == null) ? dNoneClass : clazz + " " + dNoneClass;
      getAttributes().put("clazz", clazz);
    }
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    writeIdAttribute(ctx, out);

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
    out.write("<ul");
    writeAttribute(ctx, out, "listClass", "class");
    out.write('>');

    if (this._typedMessages != null) {
      var itemClass = getString(ctx, "itemClass");
      for (Message msg : this._typedMessages) {

        out.write("<li");
        if (itemClass != null) {
          HtmlUtils.escAttribute(out, "class", itemClass);
        }
        out.write('>');

        if (msg.isEscXml()) {
          XmlEscaper.escapeContent(out, msg.getText());
        } else {
          out.write(msg.getText());
        }
        out.write("</li>");
      }
    }
    out.write("</ul>");
  }
}
