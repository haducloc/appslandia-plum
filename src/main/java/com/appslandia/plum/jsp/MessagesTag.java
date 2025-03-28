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

import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.Message;
import com.appslandia.plum.base.Messages;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "messages", dynamicAttributes = false)
public class MessagesTag extends TagBase {

  protected String type;
  protected String divClass;
  protected String listClass;
  protected String itemClass;

  @Override
  public void doTag() throws JspException, IOException {
    List<Message> messages = (Messages) this.getRequest().getAttribute(Messages.REQUEST_ATTRIBUTE_ID);
    if (!CollectionUtils.hasElements(messages)) {
      return;
    }

    int typeId = Message.parseType(this.type);
    List<Message> msgs = messages.stream().filter(m -> m.getType() == typeId).toList();
    if (msgs.isEmpty()) {
      return;
    }
    JspWriter out = this.pageContext.getOut();

    out.write("<div");
    HtmlUtils.escAttribute(out, "class", this.divClass);
    out.write(">");

    out.write("<ul");
    if (this.listClass != null) {
      HtmlUtils.escAttribute(out, "class", this.listClass);
    }
    out.write(">");
    out.newLine();

    for (Message msg : msgs) {
      out.write("<li");
      if (this.itemClass != null) {
        HtmlUtils.escAttribute(out, "class", this.itemClass);
      }
      out.write(">");

      if (msg.isEscXml()) {
        XmlEscaper.escapeXml(out, msg.getText());
      } else {
        out.write(msg.getText());
      }
      out.write("</li>");
      out.newLine();
    }

    out.write("</ul>");
    out.write("</div>");
    out.newLine();
  }

  @Attribute(required = true, rtexprvalue = false, description = "success|fatal|error|warn|notice|info")
  public void setType(String type) {
    this.type = type;
  }

  @Attribute(required = true, rtexprvalue = false)
  public void setDivClass(String divClass) {
    this.divClass = divClass;
  }

  @Attribute(required = false, rtexprvalue = false)
  public void setListClass(String listClass) {
    this.listClass = listClass;
  }

  @Attribute(required = false, rtexprvalue = false)
  public void setItemClass(String itemClass) {
    this.itemClass = itemClass;
  }
}
