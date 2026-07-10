// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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

  @Override
  public List<String> getArgumentNames() {
    return null;
  }

  @Override
  public String getDescription() {
    return "variables: base*, __parameters, esc";
  }

  @Override
  protected Object doExecute(TemplateEvaluationContext context, int lineNumber) {
    var base = context.getStringReq("base");
    var parameters = context.getDynParams();
    var esc = context.getBool("esc", true);

    var url = URLUtils.toUrl(base, parameters);
    return new SafeString(esc ? XmlEscaper.escapeXml(url) : url);
  }
}
