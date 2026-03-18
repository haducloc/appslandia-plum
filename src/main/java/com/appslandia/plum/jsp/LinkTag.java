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

package com.appslandia.plum.jsp;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.appslandia.common.utils.Arguments;
import com.appslandia.common.utils.URLUtils;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "link", bodyContent = "scriptless")
public class LinkTag extends UITagBase {

  protected String baseUrl;
  protected String labelKey;
  protected String target;
  protected String rel;

  protected String _url;
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
    } else {
      super.setDynamicAttribute(uri, name, value);
    }
  }

  @Override
  protected String getTagName() {
    return "a";
  }

  @Override
  protected void initTag() throws JspException, IOException {
    Arguments.notNull(baseUrl);

    _url = URLUtils.toUrl(baseUrl, _parameters);
  }

  @Override
  protected void writeAttributes(JspWriter out) throws JspException, IOException {
    if (id != null) {
      HtmlUtils.escAttribute(out, "id", id);
    }
    HtmlUtils.escAttribute(out, "href", _url);

    if (target != null) {
      HtmlUtils.escAttribute(out, "target", target);
    }

    if (rel != null) {
      HtmlUtils.escAttribute(out, "rel", rel);
    }

    if (hidden) {
      HtmlUtils.hidden(out);
    }

    if (datatag != null) {
      HtmlUtils.escAttribute(out, "data-tag", datatag.toString());
    }
    if (clazz != null) {
      HtmlUtils.escAttribute(out, "class", clazz);
    }
    if (style != null) {
      HtmlUtils.escAttribute(out, "style", style);
    }
    if (title != null) {
      HtmlUtils.escAttribute(out, "title", title);
    }
  }

  @Override
  protected boolean hasClosing() {
    return true;
  }

  @Override
  protected void writeBody(JspWriter out) throws JspException, IOException {
    if (labelKey != null) {
      var label = getRequestContext().res(labelKey);
      XmlEscaper.escapeContent(out, label);
    } else if (body != null) {
      body.invoke(out);
    } else {
      throw new JspException("Couldn't determine the link label.");
    }
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setLabelKey(String labelKey) {
    this.labelKey = labelKey;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setTarget(String target) {
    this.target = target;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setRel(String rel) {
    this.rel = rel;
  }
}
