// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class UITagBase extends FlComponent implements DynamicAttributes {

  protected abstract void initTag(FacesContext ctx) throws IOException;

  protected void cleanUpTag(FacesContext ctx) throws IOException {
  }

  protected abstract String getTagName();

  protected abstract void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException;

  protected abstract boolean hasClosing();

  protected abstract void writeBody(FacesContext ctx, ResponseWriter out) throws IOException;

  protected void writeTag(FacesContext ctx, ResponseWriter out) throws IOException {
    out.write('<');
    out.write(getTagName());
    writeAttributes(ctx, out);

    var taglibAttrs = getTaglibAttributes();
    if (taglibAttrs != null) {
      writeDynAttributes(ctx, out, taglibAttrs);
    }

    if (hasClosing()) {
      out.write('>');
      writeBody(ctx, out);

      out.write("</");
      out.write(getTagName());
      out.write('>');

    } else {
      out.write(" />");
    }
  }

  @Override
  public void encodeAll(FacesContext ctx) throws IOException {
    if (!isRendered()) {
      return;
    }
    try {
      initTag(ctx);
      writeTag(ctx, ctx.getResponseWriter());

    } finally {
      cleanUpTag(ctx);
    }
  }
}
