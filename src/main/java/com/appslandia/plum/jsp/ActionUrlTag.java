// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.tags.TagUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.DynamicAttributes;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "actionUrl")
public class ActionUrlTag extends TagBase implements DynamicAttributes {

  protected String controller;
  protected String action;
  protected boolean absUrl;
  protected boolean esc = true;

  protected Map<String, Object> _parameters;

  protected Map<String, Object> getParams() {
    if (_parameters == null) {
      return _parameters = new LinkedHashMap<>();
    }
    return _parameters;
  }

  @Override
  public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
    if (TagUtils.isDynamicParameter(name)) {
      getParams().put(TagUtils.getDynParamName(name), value);
    }
  }

  @Override
  public void doTag() throws JspException, IOException {
    Arguments.notNull(action);

    if (!rendered) {
      return;
    }
    if (controller == null) {
      controller = getRequestContext().getActionDesc().getController();
    }

    // URL
    var url = getActionParser().toActionUrl(getRequest(), controller, action, _parameters, absUrl);
    url = getResponse().encodeURL(url);

    if (esc) {
      XmlEscaper.escapeXml(pageContext.getOut(), url);
    } else {
      pageContext.getOut().write(url);
    }
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setController(String controller) {
    this.controller = controller;
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setAction(String action) {
    this.action = action;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setAbsUrl(boolean absUrl) {
    this.absUrl = absUrl;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setEsc(boolean esc) {
    this.esc = esc;
  }
}
