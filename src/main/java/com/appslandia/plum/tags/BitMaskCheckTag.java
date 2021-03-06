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

import com.appslandia.common.formatters.FormatterException;
import com.appslandia.common.utils.AssertUtils;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "bitMaskCheck")
public class BitMaskCheckTag extends CheckInputTag {

	protected boolean cssFieldError;

	@Override
	protected String getType() {
		return "checkbox";
	}

	@Override
	protected void initTag() throws JspException, IOException {
		super.initTag();
		AssertUtils.assertTrue(this.submitValue instanceof Number, "submitValue must be a number.");
	}

	@Override
	protected boolean isChecked() {
		if (this.value == null) {
			return false;
		}
		try {
			int flags = (Integer) getRequestContext().getFormatterProvider().getFormatter(Integer.class).parse((String) this.value,
					getRequestContext().getFormatProvider());
			int mask = ((Number) this.submitValue).intValue();
			return (flags & mask) == mask;

		} catch (FormatterException ex) {
			return false;
		}
	}

	@Override
	protected boolean cssFieldError() {
		return this.cssFieldError;
	}

	@Attribute(required = false, rtexprvalue = false)
	public void setCssFieldError(boolean cssFieldError) {
		this.cssFieldError = cssFieldError;
	}
}
