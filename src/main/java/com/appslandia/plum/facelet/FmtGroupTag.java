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

package com.appslandia.plum.facelet;

import java.io.IOException;

import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.GroupFormatProvider;
import com.appslandia.plum.utils.ServletUtils;

import jakarta.faces.component.UIComponent;
import jakarta.faces.view.facelets.FaceletContext;
import jakarta.faces.view.facelets.TagConfig;

/**
 *
 * @author Loc Ha
 *
 */
// @formatter:off
@Tag(name = "fmtGroup", dynamicAttributes = false, attributes = {
  @Attribute(name = "name", type = String.class, required = true),
  @Attribute(name = "value", type = String.class, required = true),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
public class FmtGroupTag extends FlTagHandler {

  public FmtGroupTag(TagConfig config) {
    super(config);
  }

  @Override
  public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
    if (!isRendered(ctx)) {
      return;
    }
    var value = getString(ctx, "value");
    if (value == null) {
      return;
    }

    // Group
    var name = getStringReq(ctx, "name");
    var groupFormatProvider = ServletUtils.getAppScoped(getRequest(ctx), GroupFormatProvider.class);
    var groupFormat = groupFormatProvider.getGroupFormat(name);

    var formattedValue = groupFormat.format(value);
    var rawValue = XmlEscaper.escapeAttribute(formattedValue);
    parent.getChildren().add(toHtmlOuputText(rawValue));
  }
}
