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

package com.appslandia.plum.jsptags;

import java.io.IOException;

import com.appslandia.common.utils.StringUtils;
import com.appslandia.common.utils.URLEncoding;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "mailto", bodyContent = "scriptless")
public class MailtoTag extends UITagBase {

    protected String to;
    protected String cc;
    protected String bcc;

    protected String subject;
    protected String body;

    protected String _href;

    @Override
    protected String getTagName() {
	return "a";
    }

    @Override
    protected void initTag() throws JspException, IOException {
	StringBuilder sb = new StringBuilder(255);
	sb.append("mailto:");

	if (!StringUtils.isNullOrEmpty(this.to)) {
	    sb.append(this.to);
	}
	sb.append("?t=").append(System.currentTimeMillis());

	if (!StringUtils.isNullOrEmpty(this.cc)) {
	    sb.append("&cc=").append(this.cc);
	}
	if (!StringUtils.isNullOrEmpty(this.bcc)) {
	    sb.append("&bcc=").append(this.bcc);
	}
	if (!StringUtils.isNullOrEmpty(this.subject)) {
	    sb.append("&subject=").append(URLEncoding.encodeParam(this.subject, false));
	}
	if (!StringUtils.isNullOrEmpty(this.body)) {
	    sb.append("&body=").append(URLEncoding.encodeParam(this.body, false));
	}
	this._href = sb.toString();
    }

    @Override
    protected void writeAttributes(JspWriter out) throws JspException, IOException {
	HtmlUtils.escAttribute(out, "href", this._href);
    }

    @Override
    protected boolean hasBody() {
	return true;
    }

    @Override
    protected void writeBody(JspWriter out) throws JspException, IOException {
	if (this.jspBody != null) {
	    this.jspBody.invoke(out);
	}
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setTo(String to) {
	this.to = to;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setCc(String cc) {
	this.cc = cc;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setBcc(String bcc) {
	this.bcc = bcc;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setSubject(String subject) {
	this.subject = subject;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setBody(String body) {
	this.body = body;
    }
}
