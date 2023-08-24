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
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.Message;
import com.appslandia.plum.base.Messages;
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
	int type = context.getRequiredArgument("type");

	List<Message> messages = (Messages) context.getRequest().getAttribute(Messages.REQUEST_ATTRIBUTE_ID);
	List<Message> msgs = messages.stream().filter(m -> m.getType() == type).toList();

	StringWriter out = new StringWriter(msgs.size() * 128);
	out.write("<ul class=\"l-messages\">");

	for (Message msg : msgs) {
	    String itemClass = getItemClass(type);

	    out.write(System.lineSeparator());
	    out.write("<li");
	    HtmlUtils.escAttribute(out, "class", itemClass);
	    out.write(">");

	    if (msg.isEscXml()) {
		XmlEscaper.escapeXmlContent(out, msg.getText());
	    } else {
		out.write(msg.getText());
	    }
	    out.write("</li>");
	}

	out.write("</ul>");
	return out.toString();
    }

    public static String getItemClass(int type) {
	switch (type) {
	case Message.TYPE_INFO:
	    return "l-msg l-msg-info";

	case Message.TYPE_NOTICE:
	    return "l-msg l-msg-notice";

	case Message.TYPE_WARN:
	    return "l-msg l-msg-warn";

	case Message.TYPE_ERROR:
	    return "l-msg l-msg-error";
	default:
	    throw new IllegalArgumentException("type is invalid.");
	}
    }
}
