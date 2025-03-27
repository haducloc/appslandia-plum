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

import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.AppConfig;
import com.appslandia.plum.base.UserPrincipal;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.servlet.jsp.JspException;

/**
 *
 * @author Loc Ha
 *
 */
@Tag(name = "userDName", dynamicAttributes = false)
public class UserDNameTag extends TagBase {

  private String module;

  @Override
  public void doTag() throws JspException, IOException {
    // module
    if (this.module == null) {
      AppConfig config = ServletUtils.getAppScoped(this.pageContext.getServletContext(), AppConfig.class);
      this.module = config.getStringReq(AppConfig.CONFIG_DEFAULT_MODULE);
    }

    UserPrincipal principal = ServletUtils.getPrincipalReq(getRequest());
    if (principal.isForModule(this.module)) {

      XmlEscaper.escapeXml(this.pageContext.getOut(), principal.getDisplayName());
    }
  }

  @Attribute(required = false, rtexprvalue = false)
  public void setModule(String module) {
    this.module = module;
  }
}
