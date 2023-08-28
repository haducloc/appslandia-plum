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

import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "submitButton", bodyContent = "empty")
public class SubmitButtonTag extends UITagBase {

    protected String labelKey;

    protected String actionType;
    protected boolean disabled;
    protected boolean autofocus;

    protected boolean handleWait = true;

    protected String _label;

    @Override
    protected String getTagName() {
	return "button";
    }

    @Override
    protected void initTag() throws JspException, IOException {
	this._label = getRequestContext().escXml(this.labelKey);
    }

    @Override
    protected void writeAttributes(JspWriter out) throws JspException, IOException {
	HtmlUtils.escAttribute(out, "id", this.id);
	HtmlUtils.escAttribute(out, "type", "button");

	if (this.hidden)
	    HtmlUtils.hidden(out);

	if (this.disabled)
	    HtmlUtils.disabled(out);

	if (this.autofocus)
	    HtmlUtils.autofocus(out);

	HtmlUtils.escAttribute(out, "data-label", this._label);

	String pActionType = null;
	if (this.actionType != null) {
	    pActionType = "'" + actionType + "'";
	}
	HtmlUtils.escAttribute(out, "onclick", String.format("return __click_submit_btn(event, %s, %s);", this.handleWait, pActionType));

	if (this.datatag != null)
	    HtmlUtils.escAttribute(out, "data-tag", this.datatag);
	if (this.clazz != null)
	    HtmlUtils.escAttribute(out, "class", this.clazz);
	if (this.style != null)
	    HtmlUtils.escAttribute(out, "style", this.style);
	if (this.title != null)
	    HtmlUtils.escAttribute(out, "title", this.title);
    }

    @Override
    protected boolean hasBody() {
	return true;
    }

    @Override
    protected void writeBody(JspWriter out) throws JspException, IOException {
	out.write(this._label);
    }

    @Attribute(required = true, rtexprvalue = true)
    public void setId(String id) {
	super.setId(id);
    }

    @Attribute(required = true, rtexprvalue = false)
    public void setLabelKey(String labelKey) {
	this.labelKey = labelKey;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setActionType(String actionType) {
	this.actionType = actionType;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setDisabled(boolean disabled) {
	this.disabled = disabled;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setAutofocus(boolean autofocus) {
	this.autofocus = autofocus;
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setHandleWait(boolean handleWait) {
	this.handleWait = handleWait;
    }
}
