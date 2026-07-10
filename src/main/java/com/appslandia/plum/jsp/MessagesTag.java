// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.util.List;

import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.Message;
import com.appslandia.plum.base.Message.MsgType;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "messages")
public class MessagesTag extends UITagBase {

  protected List<Message> value;
  protected String type;
  protected String listClass;
  protected String itemClass;
  protected String role;

  protected MsgType _msgType;
  protected List<Message> _typedMessages;

  @Override
  protected String getTagName() {
    return "div";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    _msgType = MsgType.parseType(type);

    if (value != null) {
      _typedMessages = value.stream().filter(m -> m.getType() == _msgType).toList();
    }

    if ((_typedMessages == null) || _typedMessages.isEmpty()) {
      var dNoneClass = TagUtils.CSS_D_NONE;
      clazz = (clazz == null) ? dNoneClass : clazz + " " + dNoneClass;
    }
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (id != null) {
      HtmlUtils.escAttribute(out, "id", id);
    }

    if (hidden) {
      HtmlUtils.hidden(out);
    }

    if (role != null) {
      HtmlUtils.escAttribute(out, "role", role);
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
  protected void writeTag(JspWriter out) throws JspException, IOException {
    out.newLine();
    super.writeTag(out);
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
    out.write("<ul");
    if (listClass != null) {
      HtmlUtils.escAttribute(out, "class", listClass);
    }
    out.write('>');

    if ((_typedMessages != null) && !_typedMessages.isEmpty()) {
      var basicHtmlSanitizer = getBasicHtmlSanitizer();
      var itemClass = this.itemClass;

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

  @Attribute(rtexprvalue = true, required = true)
  public void setValue(List<Message> value) {
    this.value = value;
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setType(String type) {
    this.type = type;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setListClass(String listClass) {
    this.listClass = listClass;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setItemClass(String itemClass) {
    this.itemClass = itemClass;
  }

  @Attribute(rtexprvalue = true, required = false, description = "alert|status")
  public void setRole(String role) {
    this.role = role;
  }
}
