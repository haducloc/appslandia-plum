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

import com.appslandia.common.json.JsonProcessor;
import com.appslandia.plum.utils.HtmlUtils;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.jsp.JspException;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "scriptJson", bodyContent = "empty", dynamicAttributes = false)
public class ScriptJsonTag extends TagBase {

  protected String id;
  protected String type = "application/json";
  protected Object value;
  protected boolean render = true;

  @Override
  public void doTag() throws JspException, IOException {
    if (!this.render) {
      return;
    }
    var out = this.pageContext.getOut();

    out.write("<script");
    HtmlUtils.escAttribute(out, "id", this.id);
    HtmlUtils.escAttribute(out, "type", this.type);
    out.println(">");

    if (this.value != null) {
      JsonProcessor jsonProcessor = ServletUtils.getAppScoped(getRequest(), JsonProcessor.class);
      jsonProcessor.write(out, this.value);
    }
    out.println();
    out.println("</script>");
  }

  @Attribute(required = true, rtexprvalue = false)
  public void setId(String id) {
    this.id = id;
  }

  @Attribute(required = false, rtexprvalue = false, defaultValue = "application/json")
  public void setType(String type) {
    this.type = type;
  }

  @Attribute(required = true, rtexprvalue = true)
  public void setValue(Object value) {
    this.value = value;
  }

  @Attribute(required = false, rtexprvalue = true)
  public void setRender(boolean render) {
    this.render = render;
  }
}
