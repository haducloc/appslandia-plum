// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import com.appslandia.common.utils.Asserts;
import com.appslandia.common.utils.CollectionUtils;
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
//@formatter:off
  @Tag(name = "choiceInput", attributes = {
  @Attribute(name = "value", type = Object.class, required = true),
  @Attribute(name = "hasFieldError", type = Boolean.class, description = "Field error is defined"),
  @Attribute(name = "describedby", type = String.class, description = "aria-describedby"),

  @Attribute(name = "disabled", type = Boolean.class),
  @Attribute(name = "required", type = Boolean.class),
  @Attribute(name = "autofocus", type = Boolean.class),

  @Attribute(name = "autocomplete", type = String.class),

  @Attribute(name = "clazz", type = String.class),
  @Attribute(name = "style", type = String.class),
  @Attribute(name = "title", type = String.class),
  @Attribute(name = "hidden", type = Boolean.class),
  @Attribute(name = "datatag", type = Object.class),
  @Attribute(name = "rendered", type = Boolean.class)
  })
//@formatter:on
@FacesComponent(ChoiceInputTag.COMPONENT_TYPE)
public class ChoiceInputTag extends UITagBase {

  public static final String COMPONENT_TYPE = "appslandia.ChoiceInputTag";

  //@formatter:off
  private static final Set<String> TAGLIB_ATTRS = CollectionUtils.toUnmodifiableSet(
   "value",
   "hasFieldError",
   "describedby",

   "disabled",
   "required",
   "autofocus",

   "autocomplete",

   "clazz",
   "style",
   "title",
   "hidden",
   "datatag",
   "rendered"
  );
  //@formatter:on

  @Override
  public Set<String> getTaglibAttributes() {
    return TAGLIB_ATTRS;
  }

  @Override
  protected String getTagName() {
    return "input";
  }

  // Fields
  protected Object value;

  protected String _path;
  protected String _type;
  protected String _model;
  protected String _form;
  protected boolean _multi;

  protected String _id;
  protected boolean _isValid;
  protected String _handlingValue;

  protected String getTagId(boolean multi) {
    var id = TagUtils.toTagId(_path);
    if (multi) {
      id = id + "_" + TagUtils.toIdPart(value);
    }
    return id;
  }

  protected Object getHandlingValue(FacesContext ctx, boolean isValid) {
    return evalPath(ctx, _model, _path);
  }

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    var choiceBox = findParent(ChoiceGroupTag.class);
    Asserts.notNull(choiceBox, "No ChoiceGroupTag parent found.");

    _path = choiceBox.path;
    _type = choiceBox.type;
    _model = choiceBox.model;
    _form = choiceBox.form;
    _multi = choiceBox.multi;

    var value = getValue(ctx, "value");
    this.value = formatValue(ctx, value, choiceBox.fmt);
    _id = getTagId(_multi);

    _isValid = !Objects.equals(_form, getModelState(ctx).getForm()) || getModelState(ctx).isValid(_path);
    var handlingValue = getHandlingValue(ctx, _isValid);
    _handlingValue = formatValue(ctx, handlingValue, choiceBox.fmt);
  }

  protected boolean isChecked() {
    if ("checkbox".equals(_type)) {
      return TagUtils.isCheckboxChecked((String) value, _handlingValue);
    } else {
      return TagUtils.isRadioChecked((String) value, _handlingValue);
    }
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    HtmlUtils.escAttribute(out, "id", _id);
    HtmlUtils.escAttribute(out, "type", _type);
    HtmlUtils.escAttribute(out, "name", _path);

    if (value != null) {
      HtmlUtils.escAttribute(out, "value", (String) value);
    }

    if (getBool(ctx, "hidden", false)) {
      HtmlUtils.hidden(out);
    }

    if (isChecked()) {
      HtmlUtils.checked(out);
    }

    if (getBool(ctx, "disabled", false)) {
      HtmlUtils.disabled(out);
    }

    if (getBool(ctx, "required", false)) {
      HtmlUtils.required(out);
    }

    if (getBool(ctx, "autofocus", false)) {
      HtmlUtils.autofocus(out);
    }

    if (_form != null) {
      HtmlUtils.escAttribute(out, "form", _form);
    }

    writeAttribute(ctx, out, "autocomplete");

    if (!_multi && !_isValid) {
      out.write(" aria-invalid=\"true\"");
    }

    // aria-describedby
    writeDescribedby(ctx, out);

    writeAttribute(ctx, out, "datatag", "data-tag");
    writeAttribute(ctx, out, "clazz", "class");
    writeAttribute(ctx, out, "style");
    writeAttribute(ctx, out, "title");
  }

  protected void writeDescribedby(FacesContext ctx, ResponseWriter out) throws IOException {
    var descBy = getString(ctx, "describedby");
    var hasFieldError = getBool(ctx, "hasFieldError", false);
    if (!_isValid && hasFieldError) {

      var fieldErrId = TagUtils.toFieldErrorId(_path);
      descBy = (descBy == null) ? fieldErrId : descBy + " " + fieldErrId;
    }
    if (descBy != null) {
      HtmlUtils.escAttribute(out, "aria-describedby", descBy);
    }
  }

  @Override
  protected boolean hasClosing() {
    return false;
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
  }
}
