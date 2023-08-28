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

package com.appslandia.plum.pebble.functions;

import java.io.IOException;
import java.util.List;

import com.appslandia.common.base.StringWriter;
import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.Message;
import com.appslandia.plum.base.Messages;
import com.appslandia.plum.jsp.MessageUtils;
import com.appslandia.plum.pebble.DynPebbleFunction;
import com.appslandia.plum.pebble.TemplateEvaluationContext;
import com.appslandia.plum.utils.HtmlUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class MessagesFunction extends DynPebbleFunction {

    @Override
    public String getDescription() {
	return "variables: type*";
    }

    @Override
    protected Object doExecute(TemplateEvaluationContext context, int lineNumber) throws IOException {
	List<Message> messages = (Messages) context.getRequest().getAttribute(Messages.REQUEST_ATTRIBUTE_ID);
	if (!CollectionUtils.hasElements(messages)) {
	    return null;
	}

	String type = context.getRequiredArgument("type");
	String boxClass = context.getArgument("boxClass");
	String msgClass = context.getArgument("msgClass");

	int typeId = MessageUtils.getMsgType(type);
	List<Message> msgs = messages.stream().filter(m -> m.getType() == typeId).toList();

	StringWriter out = new StringWriter(msgs.size() * 128);

	out.write("<ul");
	if (boxClass != null)
	    HtmlUtils.escAttribute(out, "class", boxClass);
	out.write(">");

	for (Message msg : msgs) {
	    out.write(System.lineSeparator());

	    String typeClass = MessageUtils.getMsgClass(typeId);
	    out.write("<li");

	    if (msgClass == null) {
		HtmlUtils.escAttribute(out, "class", typeClass);
	    } else {
		out.write(" class=\"");
		out.write(msgClass);
		out.write(" ");
		out.write(typeClass);
		out.write("\"");
	    }
	    out.write(">");

	    if (msg.isEscXml()) {
		XmlEscaper.escapeXml(out, msg.getText());
	    } else {
		out.write(msg.getText());
	    }
	    out.write("</li>");
	}

	out.write("</ul>");
	return out.toString();
    }
}
