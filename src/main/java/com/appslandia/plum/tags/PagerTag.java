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

package com.appslandia.plum.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.JspFragment;

import com.appslandia.common.utils.AssertUtils;
import com.appslandia.plum.base.PagerItem;
import com.appslandia.plum.base.PagerModel;
import com.appslandia.plum.utils.HtmlUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "pager", bodyContent = "scriptless", description = "pager|template=page,dots")
public class PagerTag extends UITagBase implements TemplateSupport {

	static final String VAR_ITEM = "item";
	static final String VAR_LABEL = "label";
	static final String VAR_SELECTED = "selected";

	protected String tag;
	protected String prevLabel = "&lt;";
	protected String nextLabel = "&gt;";

	protected PagerModel model;

	protected JspFragment pageTpl;
	protected JspFragment dotsTpl;

	@Override
	protected String getTagName() {
		return this.tag;
	}

	@Override
	public void setTemplate(String type, JspFragment tpl) {
		if ("page".equalsIgnoreCase(type)) {
			this.pageTpl = tpl;

		} else if ("dots".equalsIgnoreCase(type)) {
			this.dotsTpl = tpl;
		} else {
			throw new IllegalArgumentException("type is invalid (value=" + type + ")");
		}
	}

	@Override
	protected void initTag() throws JspException, IOException {
		AssertUtils.assertNotNull(this.tag);
		AssertUtils.assertNotNull(this.model);

		this.jspBody.invoke(null);

		AssertUtils.assertNotNull(this.pageTpl);
		AssertUtils.assertNotNull(this.dotsTpl);
	}

	@Override
	protected void writeAttributes(JspWriter out) throws JspException, IOException {
		if (this.id != null)
			HtmlUtils.attribute(out, "id", this.id);
		if (this.hidden)
			HtmlUtils.hidden(out);

		if (this.datatag != null)
			HtmlUtils.escAttribute(out, "data-tag", this.datatag);
		if (this.clazz != null)
			HtmlUtils.attribute(out, "class", this.clazz);
		if (this.style != null)
			HtmlUtils.attribute(out, "style", this.style);
		if (this.title != null)
			HtmlUtils.escAttribute(out, "title", this.title);
	}

	@Override
	protected boolean hasBody() {
		return true;
	}

	@Override
	protected void writeBody(JspWriter out) throws JspException, IOException {
		final Object bakItem = this.pageContext.getAttribute(VAR_ITEM);
		final Object bakSelected = this.pageContext.getAttribute(VAR_SELECTED);
		final Object bakLabel = this.pageContext.getAttribute(VAR_LABEL);
		try {
			// int index = -1;
			for (PagerItem page : this.model.getItems()) {
				this.pageContext.setAttribute(VAR_ITEM, page);

				// page.prevType
				if (page.isPrevType()) {
					this.pageContext.setAttribute(VAR_LABEL, this.prevLabel);
					this.pageTpl.invoke(out);
				}

				// page.indexType
				if (page.isIndexType()) {
					this.pageContext.setAttribute(VAR_LABEL, Integer.toString(page.getIndex()));
					this.pageContext.setAttribute(VAR_SELECTED, page.getIndex() == this.model.getPageIndex());

					this.pageTpl.invoke(out);
				}

				// page.dotType
				if (page.isDotType()) {
					this.dotsTpl.invoke(out);
				}

				// page.nextType
				if (page.isNextType()) {
					this.pageContext.setAttribute(VAR_LABEL, this.nextLabel);
					this.pageTpl.invoke(out);
				}
			}
		} finally {
			this.pageContext.setAttribute(VAR_ITEM, bakItem);
			this.pageContext.setAttribute(VAR_SELECTED, bakSelected);
			this.pageContext.setAttribute(VAR_LABEL, bakLabel);
		}

	}

	@Attribute(required = true, rtexprvalue = false)
	public void setTag(String tag) {
		this.tag = tag;
	}

	@Attribute(required = true, rtexprvalue = true)
	public void setModel(PagerModel model) {
		this.model = model;
	}

	@Attribute(required = false, rtexprvalue = true)
	public void setPrevLabel(String prevLabel) {
		this.prevLabel = prevLabel;
	}

	@Attribute(required = false, rtexprvalue = true)
	public void setNextLabel(String nextLabel) {
		this.nextLabel = nextLabel;
	}
}
