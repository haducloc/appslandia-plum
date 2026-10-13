// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.view.facelets.FaceletContext;
import jakarta.faces.view.facelets.TagConfig;

/**
 *
 * @author Loc Ha
 *
 */
// @formatter:off
@Tag(name = "if", bodyContent = true, dynamicAttributes = false, attributes = {
  @Attribute(name="test", type=Boolean.class, required=true),
  @Attribute(name="rendered", type=Boolean.class)
})
// @formatter:on
public class IfTag extends FlTagHandler {

  public IfTag(TagConfig config) {
    super(config);
  }

  @Override
  public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
    if (!isRendered(ctx)) {
      return;
    }
    var test = getBoolReq(ctx, "test");
    if (test) {
      nextHandler.apply(ctx, parent);
    }
  }
}
