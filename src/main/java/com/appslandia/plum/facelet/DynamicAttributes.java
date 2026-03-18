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
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.faces.context.FacesContext;

/**
 *
 * @author Loc Ha
 *
 */
public interface DynamicAttributes {

  public static final Set<String> LIBRARY_ATTRS = CollectionUtils.unmodifiableSet("com.sun.faces.facelets.MARK_ID");

  default Set<String> getTaglibAttributes() {
    return null;
  }

  Map<String, Object> getAttributes();

  Object getValue(FacesContext ctx, String name);

  default void writeDynAttributes(FacesContext ctx, Writer out, Set<String> taglibAttrs) throws IOException {
    for (Map.Entry<String, Object> entry : getAttributes().entrySet()) {
      var name = entry.getKey();
      if (!(LIBRARY_ATTRS.contains(name) || taglibAttrs.contains(name) || TagUtils.isDynamicParameter(name))) {

        var value = getValue(ctx, name);
        if (value != null) {
          HtmlUtils.escAttribute(out, name, value.toString());
        }
      }
    }
  }
}
