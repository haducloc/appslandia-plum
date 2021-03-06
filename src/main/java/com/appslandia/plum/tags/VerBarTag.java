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

import com.appslandia.plum.utils.HtmlUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "verBar", dynamicAttributes = false)
public class VerBarTag extends UITagBase {

	@Override
	protected String getTagName() {
		return "span";
	}

	@Override
	protected void initTag() throws JspException, IOException {
		if (this.clazz == null)
			this.clazz = "ver-bar";
	}

	@Override
	protected void writeAttributes(JspWriter out) throws JspException, IOException {
		HtmlUtils.attribute(out, "class", this.clazz);
	}

	@Override
	protected boolean hasBody() {
		return true;
	}

	@Override
	protected void writeBody(JspWriter out) throws JspException, IOException {
		out.write("&#124;");
	}

	@Override
	public void setId(String id) {
	}

	@Override
	public void setDatatag(Object datatag) {
	}

	@Override
	public void setHidden(boolean hidden) {
	}

	@Override
	public void setStyle(String style) {
	}

	@Override
	public void setTitle(String title) {
	}

	@Override
	public void setRender(boolean render) {
	}
}
