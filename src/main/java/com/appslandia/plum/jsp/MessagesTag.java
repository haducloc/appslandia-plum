// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

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
import java.util.stream.Collectors;

import com.appslandia.plum.base.Message;
import com.appslandia.plum.base.Messages;
import com.appslandia.plum.utils.XmlEscaper;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "messages", dynamicAttributes = false)
public class MessagesTag extends TagBase {

    protected Messages _messages;
    protected String clazz;

    @Override
    public void doTag() throws JspException, IOException {
	this._messages = (Messages) getRequest().getAttribute(Messages.REQUEST_ATTRIBUTE_ID);
	if (this._messages == null) {
	    return;
	}
	if (this.clazz == null) {
	    this.clazz = "messages";
	}
	writeTypedMessages(Message.TYPE_ERROR);
	writeTypedMessages(Message.TYPE_WARN);
	writeTypedMessages(Message.TYPE_NOTICE);
	writeTypedMessages(Message.TYPE_INFO);
    }

    protected void writeTypedMessages(int typeId) throws JspException, IOException {
	List<Message> messages = this._messages.stream().filter(r -> r.getType() == typeId).collect(Collectors.toList());
	if (messages.isEmpty()) {
	    return;
	}
	String typedClass = getClassName(typeId);
	JspWriter out = this.pageContext.getOut();

	out.println();
	out.write("<ul class=\"");
	out.write(this.clazz);
	out.write(" ");
	out.write(typedClass);
	out.write("\">");

	for (Message msg : messages) {
	    out.write("<li>");

	    if (msg.isEscXml()) {
		XmlEscaper.escapeXmlContent(out, msg.getText());
	    } else {
		out.write(msg.getText());
	    }
	    out.write("</li>");
	}
	out.write("</ul>");
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setClazz(String clazz) {
	this.clazz = clazz;
    }

    public static String getClassName(int typeId) {
	switch (typeId) {
	case Message.TYPE_INFO:
	    return "messages-info";

	case Message.TYPE_NOTICE:
	    return "messages-notice";

	case Message.TYPE_WARN:
	    return "messages-warn";

	case Message.TYPE_ERROR:
	    return "messages-error";
	default:
	    throw new IllegalArgumentException("typeId is invalid.");
	}
    }
}
