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

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.util.List;

import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.Message;
import com.appslandia.plum.base.Messages;
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

  protected String type;
  protected String listClass;
  protected String itemClass;

  protected List<Message> _typedMessages;

  @Override
  protected String getTagName() {
    return "div";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    var messages = (Messages) this.getRequest().getAttribute(Messages.REQUEST_ATTRIBUTE_ID);
    if (messages != null) {
      var typeId = Message.parseType(this.type);
      this._typedMessages = messages.stream().filter(m -> m.getType() == typeId).toList();
    }

    if ((this._typedMessages == null) || this._typedMessages.isEmpty()) {
      var dNoneClass = TagUtils.CSS_D_NONE;
      this.clazz = (this.clazz == null) ? dNoneClass : this.clazz + " " + dNoneClass;
    }
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (this.id != null) {
      HtmlUtils.escAttribute(out, "id", this.id);
    }

    if (this.hidden) {
      HtmlUtils.hidden(out);
    }

    if (this.datatag != null) {
      HtmlUtils.escAttribute(out, "data-tag", this.datatag.toString());
    }
    if (this.clazz != null) {
      HtmlUtils.escAttribute(out, "class", this.clazz);
    }
    if (this.style != null) {
      HtmlUtils.escAttribute(out, "style", this.style);
    }
    if (this.title != null) {
      HtmlUtils.escAttribute(out, "title", this.title);
    }
  }

  @Override
  protected boolean hasClosing() {
    return true;
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
    out.write("<ul");
    if (this.listClass != null) {
      HtmlUtils.escAttribute(out, "class", this.listClass);
    }
    out.write('>');

    if (this._typedMessages != null) {
      var itemClass = this.itemClass;
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
}
