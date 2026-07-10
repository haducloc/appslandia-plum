// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.Message;
import com.appslandia.plum.base.Message.MsgType;
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
  @Attribute(name = "value", type = List.class, required = true),
  @Attribute(name = "type", type = String.class, required = true),
  @Attribute(name = "listClass", type = String.class),
  @Attribute(name = "itemClass", type = String.class),
  @Attribute(name = "role", type = String.class, description = "alert|status"),

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
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.toUnmodifiableSet(
    "value",
    "type",
    "listClass",
    "itemClass",
    "role",

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

  protected MsgType _msgType;
  protected List<Message> _typedMessages;

  @SuppressWarnings("unchecked")
  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    var type = getStringReq(ctx, "type");
    _msgType = MsgType.parseType(type);

    var value = (List<Message>) getValue(ctx, "value");
    if (value != null) {
      _typedMessages = value.stream().filter(m -> m.getType() == _msgType).toList();
    }

    if ((_typedMessages == null) || _typedMessages.isEmpty()) {
      var dNoneClass = TagUtils.CSS_D_NONE;
      var clazz = getString(ctx, "clazz");
      clazz = (clazz == null) ? dNoneClass : clazz + " " + dNoneClass;
      getAttributes().put("clazz", clazz);
    }
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

    writeAttribute(ctx, out, "role");

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
  protected void writeTag(FacesContext ctx, ResponseWriter out) throws IOException {
    newLine(out);
    super.writeTag(ctx, out);
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
    out.write("<ul");
    writeAttribute(ctx, out, "listClass", "class");
    out.write('>');

    if ((_typedMessages != null) && !_typedMessages.isEmpty()) {
      var basicHtmlSanitizer = getBasicHtmlSanitizer(ctx);
      var itemClass = getString(ctx, "itemClass");

      for (Message msg : _typedMessages) {
        out.write("<li");
        if (itemClass != null) {
          HtmlUtils.escAttribute(out, "class", itemClass);
        }
        out.write('>');

        if (msg.isEscXml()) {
          XmlEscaper.escapeContent(out, msg.getText());
        } else {
          var sm = basicHtmlSanitizer.sanitize(msg.getText());
          out.write(sm);
        }
        out.write("</li>");
      }
    }
    out.write("</ul>");
  }
}
