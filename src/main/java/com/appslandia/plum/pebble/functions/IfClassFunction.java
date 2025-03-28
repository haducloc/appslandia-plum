// The MIT License (MIT)
// Copyright © 2015 Loc Ha

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

import com.appslandia.plum.jsp.TagUtils;
import com.appslandia.plum.pebble.DynPebbleFunction;
import com.appslandia.plum.pebble.TemplateEvaluationContext;

import io.pebbletemplates.pebble.extension.escaper.SafeString;

/**
 *
 * @author Loc Ha
 *
 */
public class IfClassFunction extends DynPebbleFunction {

  @Override
  public String getDescription() {
    return "variables: test*, value*";
  }

  @Override
  protected Object doExecute(TemplateEvaluationContext context, int lineNumber) throws IOException {
    boolean test = context.getBool("test");
    String clazz = context.getArgReq("value");

    if (test) {
      return new SafeString(clazz);
    }
    return new SafeString(TagUtils.CSS_NOOP);
  }
}
