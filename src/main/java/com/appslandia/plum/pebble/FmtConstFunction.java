// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.pebble;

import java.io.IOException;
import java.util.List;

import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.ConstGroupProvider;
import com.appslandia.plum.utils.ServletUtils;

import io.pebbletemplates.pebble.extension.escaper.SafeString;

/**
 *
 * @author Loc Ha
 *
 */
public class FmtConstFunction extends PebbleFunction {

  static final List<String> ARGS = List.of("value", "fmt");

  @Override
  public List<String> getArgumentNames() {
    return ARGS;
  }

  @Override
  public String getDescription() {
    return "variables: value, fmt*";
  }

  @Override
  protected Object doExecute(TemplateEvaluationContext context, int lineNumber) throws IOException {
    var value = context.getArg("value");
    if (value == null) {
      return null;
    }

    var provider = ServletUtils.getAppScoped(context.getRequest(), ConstGroupProvider.class);
    var group = context.getStringReq("fmt");

    var descKey = provider.getDescKey(group, value);
    var content = (descKey != null) ? context.getRequestContext().res(descKey) : value.toString();
    var esc = XmlEscaper.escapeXml(content);

    return new SafeString(esc);
  }
}
