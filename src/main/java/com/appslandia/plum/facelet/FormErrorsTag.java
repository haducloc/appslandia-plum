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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import com.appslandia.common.utils.CollectionUtils;
import com.appslandia.common.utils.SplitUtils;
import com.appslandia.common.utils.XmlEscaper;
import com.appslandia.plum.base.Message;
import com.appslandia.plum.base.ModelState;
import com.appslandia.plum.base.SimpleCsrfManager;
import com.appslandia.plum.tags.TagUtils;
import com.appslandia.plum.utils.HtmlUtils;

import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

/**
 *
 * @author Loc Ha
 *
 */
// @formatter:off
@Tag(name = "formErrors", attributes = {
  @Attribute(name = "modelOnly", type = Boolean.class),
  @Attribute(name = "fieldOrder", type = String.class),
  @Attribute(name = "form", type = String.class),
  @Attribute(name = "listClass", type = String.class),
  @Attribute(name = "itemClass", type = String.class),

  @Attribute(name = "id", type = String.class),
  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
})
// @formatter:on
@FacesComponent(FormErrorsTag.COMPONENT_TYPE)
public class FormErrorsTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.FormErrorsTag";

  // @formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.unmodifiableSet(
    "modelOnly",
    "fieldOrder",
    "form",
    "listClass",
    "itemClass",

    "id",
    "clazz",
    "style",
    "title",
    "hidden",
    "datatag",
    "rendered"
  );
  // @formatter:on

  @Override
  public Set<String> getTaglibAttributes() {
    return TAGLIB_ATTRS;
  }

  @Override
  protected String getTagName() {
    return "div";
  }

  // Fields
  protected boolean modelOnly;
  protected String form;

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    // Form Context
    var formCtx = getFormContext(ctx);
    this.form = (formCtx != null) ? formCtx.getForm() : getString(ctx, "form");

    this.modelOnly = getBool(ctx, "modelOnly", true);
    var hasErrors = false;

    if (this.modelOnly) {
      hasErrors = Objects.equals(this.form, this.getModelState(ctx).getForm())
          && (!this.getModelState(ctx).isValid(ModelState.FIELD_MODEL)
              || !this.getModelState(ctx).isValid(SimpleCsrfManager.PARAM_CSRF_ID));
    } else {
      hasErrors = !getModelState(ctx).isValid();
    }

    // Class
    if (!hasErrors) {
      var dNoneClass = TagUtils.CSS_D_NONE;
      var clazz = getString(ctx, "clazz");
      clazz = (clazz == null) ? dNoneClass : clazz + " " + dNoneClass;
      getAttributes().put("clazz", clazz);
    }
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    writeIdAttribute(ctx, out);

    if (getBool(ctx, "hidden", false)) {
      HtmlUtils.hidden(out);
    }

    writeAttribute(ctx, out, "datatag", "data-tag");
    writeAttribute(ctx, out, "clazz", "class");
    writeAttribute(ctx, out, "style");
    writeAttribute(ctx, out, "title");
  }

  @Override
  protected boolean hasClosing() {
    return true;
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
    out.write("<ul");
    writeAttribute(ctx, out, "listClass", "class");
    out.write('>');
    var modelState = getModelState(ctx);

    if (!this.modelOnly) {
      List<Entry<String, List<Message>>> errors = new ArrayList<>(modelState.getErrors().entrySet());
      var fieldOrder = getString(ctx, "fieldOrder");
      if (fieldOrder != null) {
        Collections.sort(errors, toFieldComparator(fieldOrder));
      }

      for (Entry<String, List<Message>> fieldError : errors) {
        if (!fieldError.getKey().equals(ModelState.FIELD_MODEL)
            && !fieldError.getKey().equals(SimpleCsrfManager.PARAM_CSRF_ID)) {
          writeMessages(ctx, out, fieldError.getValue());
        }
      }
    }

    // Model Errors
    var modelErrors = modelState.getErrors().get(ModelState.FIELD_MODEL);
    if (modelErrors != null) {
      writeMessages(ctx, out, modelErrors);
    }

    // CSRF Errors
    var csrfErrors = modelState.getErrors().get(SimpleCsrfManager.PARAM_CSRF_ID);
    if (csrfErrors != null) {
      writeMessages(ctx, out, csrfErrors);
    }
    out.write("</ul>");
  }

  private void writeMessages(FacesContext ctx, ResponseWriter out, List<Message> messages) throws IOException {
    var itemClass = getString(ctx, "itemClass");
    for (Message message : messages) {
      out.write("<li");
      if (itemClass != null) {
        HtmlUtils.escAttribute(out, "class", itemClass);
      }
      out.write('>');

      if (message.isEscXml()) {
        XmlEscaper.escapeContent(out, message.getText());
      } else {
        out.write(message.getText());
      }
      out.write("</li>");
    }
  }

  private Comparator<Entry<String, List<Message>>> toFieldComparator(String fieldOrder) {
    final Map<String, Integer> posMap = new HashMap<>();
    var pos = 0;
    for (String field : SplitUtils.splitByComma(fieldOrder)) {
      posMap.put(field, ++pos);
    }
    return Comparator.comparing(entry -> posMap.getOrDefault(entry.getKey(), Integer.MAX_VALUE));
  }
}
