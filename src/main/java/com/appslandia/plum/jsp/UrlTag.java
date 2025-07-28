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

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.DynamicAttributes;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "url")
public class UrlTag extends TagBase implements DynamicAttributes {

  protected String base;
  protected boolean esc = false;
  protected Map<String, Object> _parameters;

  protected Map<String, Object> getParams() {
    if (this._parameters == null) {
      return this._parameters = new LinkedHashMap<>();
    }
    return this._parameters;
  }

  @Override
  public void setDynamicAttribute(String uri, String name, Object value) throws JspException {
    if (TagUtils.isDynamicParameter(name)) {
      getParams().put(TagUtils.getDynParamName(name), value);
    }
  }

  @Override
  public void doTag() throws JspException, IOException {
    Arguments.notNull(this.base);

    if (!this.rendered) {
      return;
    }
    var url = URLUtils.toUrl(this.base, this._parameters);

    if (this.esc) {
      XmlEscaper.escapeAttribute(this.pageContext.getOut(), url);
    } else {
      this.pageContext.getOut().write(url);
    }
  }

  @Attribute(rtexprvalue = true, required = true)
  public void setBase(String base) {
    this.base = base;
  }

  @Attribute(rtexprvalue = true, required = false)
  public void setEsc(boolean esc) {
    this.esc = esc;
  }
}
