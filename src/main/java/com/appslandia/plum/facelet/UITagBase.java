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
    out.write(this.getTagName());
    this.writeAttributes(ctx, out);

    var taglibAttrs = getTaglibAttributes();
    if (taglibAttrs != null) {
      this.writeDynAttributes(ctx, out, taglibAttrs);
    }

    if (this.hasClosing()) {
      out.write('>');
      this.writeBody(ctx, out);

      out.write("</");
      out.write(this.getTagName());
      out.write('>');

    } else {
      out.write(" />");
    }
  }

  @Override
  public void encodeAll(FacesContext ctx) throws IOException {
    if (!this.isRendered()) {
      return;
    }
    try {
      this.initTag(ctx);
      this.writeTag(ctx, ctx.getResponseWriter());

    } finally {
      this.cleanUpTag(ctx);
    }
  }
}
