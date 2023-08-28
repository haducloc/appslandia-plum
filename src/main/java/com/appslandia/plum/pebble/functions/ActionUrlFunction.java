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

import java.util.Map;

import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.ActionParser;
import com.appslandia.plum.pebble.DynPebbleFunction;
import com.appslandia.plum.pebble.TemplateEvaluationContext;
import com.appslandia.plum.utils.ServletUtils;

import io.pebbletemplates.pebble.extension.escaper.SafeString;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class ActionUrlFunction extends DynPebbleFunction {

    @Override
    public String getDescription() {
	return "variables: action*, controller, absUrl, escXml";
    }

    @Override
    protected Object doExecute(TemplateEvaluationContext context, int lineNumber) {
	String action = context.getRequiredArgument("action");
	String controller = context.getArgument("controller");

	boolean absUrl = context.getBool("absUrl", false);
	boolean escXml = context.getBool("escXml", true);

	if (controller == null) {
	    controller = context.getRequestContext().getActionDesc().getController();
	}
	Map<String, Object> parameters = context.parseParameters();

	ActionParser actionParser = ServletUtils.getAppScoped(context.getRequest().getServletContext(), ActionParser.class);
	String url = actionParser.toActionUrl(context.getRequest(), controller, action, parameters, absUrl);

	url = context.getResponse().encodeURL(url);
	return new SafeString(escXml ? XmlEscaper.escapeXml(url) : url);
    }
}
