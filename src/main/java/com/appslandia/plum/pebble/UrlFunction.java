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

package com.appslandia.plum.pebble;

import java.util.List;

import com.appslandia.common.utils.URLUtils;
import com.appslandia.common.utils.XmlEscaper;

import io.pebbletemplates.pebble.extension.escaper.SafeString;

/**
 *
 * @author Loc Ha
 *
 */
public class UrlFunction extends PebbleFunction {

  static final List<String> ARGS = List.of("base", "params", "esc");

  @Override
  public List<String> getArgumentNames() {
    return ARGS;
  }

  @Override
  public String getDescription() {
    return "variables: base*, params, esc";
  }

  @Override
  protected Object doExecute(TemplateEvaluationContext context, int lineNumber) {
    var base = context.getStringReq("base");
    var parameters = context.getMap("params");
    var esc = context.getBool("esc", false);

    var url = URLUtils.toUrl(base, parameters);
    return new SafeString(esc ? XmlEscaper.escapeAttribute(url) : url);
  }
}
