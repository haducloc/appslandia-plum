// The MIT License (MIT)
// Copyright © 2015 AppsLandia. All rights reserved.

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software withhtml restriction, including withhtml limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHhtml WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// out OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.appslandia.plum.pebble.functions;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import com.appslandia.common.models.SelectItem;
import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.StringUtils;
import com.appslandia.plum.pebble.DynPebbleFunction;
import com.appslandia.plum.pebble.TemplateEvaluationContext;
import com.appslandia.plum.utils.HtmlUtils;

import io.pebbletemplates.pebble.extension.escaper.SafeString;

/**
 *
 * @author <a href="mailto:haducloc13@gmail.com">Loc Ha</a>
 *
 */
public class HiddenSelectFunction extends DynPebbleFunction {

  @Override
  public String getDescription() {
    return "variables: path*, items*, readonly*, converter";
  }

  @Override
  protected Object doExecute(TemplateEvaluationContext context, int lineNumber) throws IOException {
    String path = context.getRequiredArgument("path");
    String converter = context.getArgument("converter");
    boolean readonly = context.getBool("readonly");

    List<SelectItem> items = context.getRequiredArgument("items");

    int nameIdx = path.indexOf('.');
    Asserts.isTrue(nameIdx > 0 && nameIdx < path.length() - 1, "path is invalid.");
    String name = path.substring(nameIdx + 1);

    Object value = context.evaluate(path);
    String fmtValue = context.getRequestContext().format(value, converter, false);

    // Selected Item
    SelectItem selectedItem = items.stream().filter(item -> {

      String codeValue = context.getRequestContext().format(item.getValue(), converter, false);
      return StringUtils.iequals(codeValue, fmtValue);

    }).findFirst().orElse(null);

    // If readonly & selectedItem
    if (readonly && selectedItem != null) {

      StringWriter out = new StringWriter(128);
      out.append("<input type=\"hidden\"");

      HtmlUtils.escAttribute(out, "name", name);
      HtmlUtils.escAttribute(out, "value", fmtValue);
      out.append(" />");

      return new SafeString(out.toString());
    }
    return null;
  }
}
