// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;
import java.util.Objects;

import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;

/**
 *
 * @author Loc Ha
 *
 */
// @formatter:off
  @Tag(name = "ifModelInvalid", bodyContent = true, dynamicAttributes = false, attributes = {
  @Attribute(name = "form", type = String.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(IfModelInvalidTag.COMPONENT_TYPE)
public class IfModelInvalidTag extends FlFragmentComponent {

  public static final String COMPONENT_TYPE = "appslandia.IfModelInvalidTag";

  protected boolean _isValid;

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    var formCtx = getFormContext(ctx);
    var form = getForm(ctx, formCtx);

    _isValid = !Objects.equals(form, getModelState(ctx).getForm()) || getModelState(ctx).isValid();
  }

  @Override
  public boolean invokeBody(FacesContext ctx) throws IOException {
    if (_isValid) {
      return false;
    }
    return super.invokeBody(ctx);
  }
}
