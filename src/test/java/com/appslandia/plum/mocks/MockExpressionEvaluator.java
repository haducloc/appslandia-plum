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

package com.appslandia.plum.mocks;

import com.appslandia.plum.base.Messages;
import com.appslandia.plum.base.ModelState;
import com.appslandia.plum.base.PagerModel;
import com.appslandia.plum.base.PrefCookie;
import com.appslandia.plum.base.RequestContext;
import com.appslandia.plum.base.SortModel;
import com.appslandia.plum.base.TempData;
import com.appslandia.plum.tags.ExpressionEvaluator;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.el.ELProcessor;
import jakarta.servlet.jsp.PageContext;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class MockExpressionEvaluator extends ExpressionEvaluator {

    @Override
    public Object getValue(PageContext pc, String property) {
	try {
	    ELProcessor processor = new ELProcessor();
	    processor.defineBean("pageContext", pc);

	    defineBean(RequestContext.REQUEST_ATTRIBUTE_ID, pc, processor);
	    defineBean(Messages.REQUEST_ATTRIBUTE_ID, pc, processor);

	    defineBean(PrefCookie.REQUEST_ATTRIBUTE_ID, pc, processor);
	    defineBean(TempData.REQUEST_ATTRIBUTE_ID, pc, processor);

	    defineBean(SortModel.REQUEST_ATTRIBUTE_ID, pc, processor);
	    defineBean(PagerModel.REQUEST_ATTRIBUTE_ID, pc, processor);

	    defineBean(ServletUtils.REQUEST_ATTRIBUTE_MODEL, pc, processor);
	    defineBean(ModelState.REQUEST_ATTRIBUTE_ID, pc, processor);

	    return processor.eval(property);
	} catch (Exception ex) {
	    throw new jakarta.el.ELException(ex);
	}
    }

    private void defineBean(String name, PageContext pc, ELProcessor processor) {
	processor.defineBean(name, pc.findAttribute(name));
    }
}
