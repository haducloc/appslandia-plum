// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;

import jakarta.faces.context.FacesContext;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class FlFragmentComponent extends FlComponent {

  protected abstract void initTag(FacesContext ctx) throws IOException;

  protected void cleanUpTag(FacesContext ctx) throws IOException {
  }

  @Override
  public void encodeAll(FacesContext ctx) throws IOException {
    if (!isRendered()) {
      return;
    }
    try {
      initTag(ctx);

      invokeBody(ctx);

    } finally {
      cleanUpTag(ctx);
    }
  }
}
