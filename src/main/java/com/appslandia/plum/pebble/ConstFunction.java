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

package com.appslandia.plum.pebble;

import java.io.IOException;
import java.util.List;

import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.ConstDescProvider;
import com.appslandia.plum.utils.ServletUtils;

import io.pebbletemplates.pebble.extension.escaper.SafeString;

/**
 *
 * @author Loc Ha
 *
 */
public class ConstFunction extends PebbleFunction {

  static final List<String> ARGS = List.of("value", "group", "esc");

  @Override
  public List<String> getArgumentNames() {
    return ARGS;
  }

  @Override
  public String getDescription() {
    return "variables: value, group*, esc";
  }

  @Override
  protected Object doExecute(TemplateEvaluationContext context, int lineNumber) throws IOException {
    var value = context.getArg("value");
    if (value == null) {
      return null;
    }

    var group = context.getStringReq("group");
    var provider = ServletUtils.getAppScoped(context.getRequest(), ConstDescProvider.class);

    var descKey = provider.getDescKey(group, value);
    var content = (descKey != null) ? context.getRequestContext().res(descKey) : value.toString();

    var esc = context.getBool("esc", false);
    return new SafeString(esc ? XmlEscaper.escapeAttribute(content) : content);
  }
}
