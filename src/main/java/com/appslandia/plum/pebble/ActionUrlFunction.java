// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.pebble;

import java.util.List;

import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.ActionParser;
import com.appslandia.plum.utils.ServletUtils;

import io.pebbletemplates.pebble.extension.escaper.SafeString;

/**
 *
 * @author Loc Ha
 *
 */
public class ActionUrlFunction extends PebbleFunction {

  @Override
  public List<String> getArgumentNames() {
    return null;
  }

  @Override
  public String getDescription() {
    return "variables: action*, controller*, __parameters, absUrl, esc";
  }

  @Override
  protected Object doExecute(TemplateEvaluationContext context, int lineNumber) {
    var action = context.getStringReq("action");
    var controller = context.getStringReq("controller");
    var parameters = context.getDynParams();

    var absUrl = context.getBool("absUrl", false);
    var esc = context.getBool("esc", true);

    var actionParser = ServletUtils.getAppScoped(context.getRequest(), ActionParser.class);
    var url = actionParser.toActionUrl(context.getRequest(), controller, action, parameters, absUrl);

    url = context.getResponse().encodeURL(url);
    return new SafeString(esc ? XmlEscaper.escapeXml(url) : url);
  }
}
