// Licensed under the MIT License.
// See LICENSE file in the project root for details.

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
  @Attribute(name = "role", type = String.class, description = "alert"),

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
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.toUnmodifiableSet(
    "modelOnly",
    "fieldOrder",
    "form",
    "listClass",
    "itemClass",
    "role",

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
    var formCtx = getFormContext(ctx);
    form = getForm(ctx, formCtx);

    modelOnly = getBool(ctx, "modelOnly", true);
    var hasErrors = false;

    if (modelOnly) {
      hasErrors = Objects.equals(form, getModelState(ctx).getForm())
          && (!getModelState(ctx).isValid(ModelState.FIELD_MODEL)
              || !getModelState(ctx).isValid(SimpleCsrfManager.PARAM_CSRF_ID));
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
    var id = getId();
    if (!isGeneratedId(id)) {
      HtmlUtils.escAttribute(out, "id", id);
    }

    if (getBool(ctx, "hidden", false)) {
      HtmlUtils.hidden(out);
    }

    writeAttribute(ctx, out, "role");

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
  protected void writeTag(FacesContext ctx, ResponseWriter out) throws IOException {
    newLine(out);
    super.writeTag(ctx, out);
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
    out.write("<ul");
    writeAttribute(ctx, out, "listClass", "class");
    out.write('>');
    var modelState = getModelState(ctx);

    if (!modelOnly) {
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
