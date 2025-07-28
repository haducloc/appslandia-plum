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
import java.util.Iterator;

import jakarta.faces.component.UIComponent;
import jakarta.faces.view.facelets.FaceletContext;
import jakarta.faces.view.facelets.TagConfig;

/**
 *
 * @author Loc Ha
 *
 */
// @formatter:off
@Tag(name = "iterate", bodyContent = true, dynamicAttributes = false, attributes = {
  @Attribute(name = "items", type = Object.class, required = true),
  @Attribute(name = "var", type = String.class),
  @Attribute(name = "rendered", type = Boolean.class)
 })
// @formatter:on
public class IterateTag extends FlTagHandler {

  static final String VAR_INDEX = "index";
  static final String VAR_BEGIN = "begin";
  static final String VAR_END = "end";

  public IterateTag(TagConfig config) {
    super(config);
  }

  @Override
  public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
    if (!isRendered(ctx)) {
      return;
    }
    Iterable<?> items = getIterable(ctx, "items");
    if (items == null) {
      return;
    }
    var var = getString(ctx, "var", "item");

    final var bakItemVar = ctx.getAttribute(var);
    final var bakIndexVar = ctx.getAttribute(VAR_INDEX);
    final var bakBeginVar = ctx.getAttribute(VAR_BEGIN);
    final var bakEndVar = ctx.getAttribute(VAR_END);

    try {
      var index = -1;
      Iterator<?> iter = items.iterator();

      while (iter.hasNext()) {
        Object item = iter.next();

        ctx.setAttribute(var, item);
        index += 1;

        ctx.setAttribute(VAR_INDEX, index);
        ctx.setAttribute(VAR_BEGIN, index == 0);
        ctx.setAttribute(VAR_END, !iter.hasNext());

        this.nextHandler.apply(ctx, parent);
      }
    } finally {
      ctx.setAttribute(var, bakItemVar);
      ctx.setAttribute(VAR_INDEX, bakIndexVar);
      ctx.setAttribute(VAR_BEGIN, bakBeginVar);
      ctx.setAttribute(VAR_END, bakEndVar);
    }
  }
}
