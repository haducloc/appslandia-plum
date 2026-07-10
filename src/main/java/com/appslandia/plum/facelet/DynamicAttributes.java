// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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

  public static final Set<String> LIBRARY_ATTRS = CollectionUtils.toUnmodifiableSet("com.sun.faces.facelets.MARK_ID");

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
