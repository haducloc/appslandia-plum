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
import java.util.Iterator;

import com.appslandia.common.utils.ArrayUtils;

import jakarta.servlet.jsp.JspException;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
@Tag(name = "iterate", bodyContent = "scriptless", dynamicAttributes = false)
public class IterateTag extends TagBase {

    protected Iterable<?> items;
    protected String var = "item";

    protected boolean render = true;

    static final String INDEX_VAR = "index";
    static final String FIRST_INDEX_VAR = "firstIndex";
    static final String LAST_INDEX_VAR = "lastIndex";

    @Override
    public void doTag() throws JspException, IOException {
	if ((!this.render) || (this.items == null) || (this.jspBody == null)) {
	    return;
	}
	final Object bakVar = this.pageContext.getAttribute(this.var);

	final Object bakIndexVar = this.pageContext.getAttribute(INDEX_VAR);
	final Object bakFirstIndexVar = this.pageContext.getAttribute(FIRST_INDEX_VAR);
	final Object bakLastIndexVar = this.pageContext.getAttribute(LAST_INDEX_VAR);

	try {
	    int index = -1;
	    Iterator<?> iter = this.items.iterator();

	    while (iter.hasNext()) {
		Object item = iter.next();

		this.pageContext.setAttribute(this.var, item);
		index += 1;

		this.pageContext.setAttribute(INDEX_VAR, index);
		this.pageContext.setAttribute(FIRST_INDEX_VAR, index == 0);
		this.pageContext.setAttribute(LAST_INDEX_VAR, !iter.hasNext());

		this.jspBody.invoke(null);
	    }

	} finally {
	    this.pageContext.setAttribute(this.var, bakVar);

	    this.pageContext.setAttribute(INDEX_VAR, bakIndexVar);
	    this.pageContext.setAttribute(FIRST_INDEX_VAR, bakFirstIndexVar);
	    this.pageContext.setAttribute(LAST_INDEX_VAR, bakLastIndexVar);
	}
    }

    @Attribute(required = true, rtexprvalue = true)
    public void setItems(Object items) {
	if (items == null) {
	    return;
	}
	if (items instanceof Iterable) {
	    this.items = (Iterable<?>) items;

	} else if (items.getClass().isArray()) {

	    this.items = new Iterable<Object>() {

		@Override
		public Iterator<Object> iterator() {
		    return ArrayUtils.iterator(items);
		}
	    };

	} else {
	    throw new IllegalStateException("items must be an Iterable or an Array.");
	}
    }

    @Attribute(required = false, rtexprvalue = false)
    public void setVar(String var) {
	this.var = var;
    }

    @Attribute(required = false, rtexprvalue = true)
    public void setRender(boolean render) {
	this.render = render;
    }
}
