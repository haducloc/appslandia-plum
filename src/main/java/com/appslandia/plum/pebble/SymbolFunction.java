// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.pebble;

import java.io.IOException;
import java.util.List;

import com.appslandia.plum.base.HtmlSymbolProvider;
import com.appslandia.plum.utils.ServletUtils;

import io.pebbletemplates.pebble.extension.escaper.SafeString;

/**
 *
 * @author Loc Ha
 *
 */
public class SymbolFunction extends PebbleFunction {

  static final List<String> ARGS = List.of("name", "times");

  @Override
  public List<String> getArgumentNames() {
    return ARGS;
  }

  @Override
  public String getDescription() {
    return "variables: name*, times";
  }

  @Override
  protected Object doExecute(TemplateEvaluationContext context, int lineNumber) throws IOException {
    var name = context.getStringReq("name");
    var times = context.getInt("times", 1);

    var provider = ServletUtils.getAppScoped(context.getRequest(), HtmlSymbolProvider.class);
    var symbol = provider.getHtmlSymbol(name);

    if (times <= 0) {
      return null;
    }

    var codes = symbol.getCode().repeat(times);
    return new SafeString(codes);
  }
}
