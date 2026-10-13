// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.facelet;

import java.io.IOException;
import java.util.Set;

import com.appslandia.common.utils.Asserts;
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
  @Attribute(name = "describedby", type = String.class, description = "aria-describedby"),

  @Attribute(name = "disabled", type = Boolean.class),
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
  private static final Set<String> TAGLIB_ATTRS = Set.of(
   "value",
   "describedby",

   "disabled",
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

  protected ChoiceGroupTag _choiceGroup;
  protected String _id;
  protected String _handlingValue;

  protected Object getHandlingValue(FacesContext ctx, boolean isValid) {
    return evalPath(ctx, _choiceGroup.model, _choiceGroup.path);
  }

  @Override
  protected void initTag(FacesContext ctx) throws IOException {
    _choiceGroup = findParent(ChoiceGroupTag.class);
    Asserts.notNull(_choiceGroup, "No ChoiceGroupTag parent found.");

    var value = getValue(ctx, "value");
    this.value = formatValue(ctx, value, _choiceGroup.fmt);
    _id = _choiceGroup.toChoiceId(this.value);

    var handlingValue = getHandlingValue(ctx, _choiceGroup._isValid);
    _handlingValue = formatValue(ctx, handlingValue, _choiceGroup.fmt);
  }

  protected boolean isChecked() {
    if ("checkbox".equals(_choiceGroup.type)) {
      return TagUtils.isCheckboxChecked((String) value, _handlingValue);
    } else {
      return TagUtils.isRadioChecked((String) value, _handlingValue);
    }
  }

  @Override
  protected void writeAttributes(FacesContext ctx, ResponseWriter out) throws IOException {
    HtmlUtils.escAttribute(out, "id", _id);
    HtmlUtils.escAttribute(out, "type", _choiceGroup.type);
    HtmlUtils.escAttribute(out, "name", _choiceGroup.path);

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

    if (!_choiceGroup.multi && _choiceGroup.required) {
      HtmlUtils.required(out);
    }

    if (getBool(ctx, "autofocus", false)) {
      HtmlUtils.autofocus(out);
    }

    if (_choiceGroup.form != null) {
      HtmlUtils.escAttribute(out, "form", _choiceGroup.form);
    }

    writeAttribute(ctx, out, "autocomplete");

    if (!_choiceGroup.multi && !_choiceGroup._isValid) {
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
    if (!_choiceGroup.multi && !_choiceGroup._isValid && _choiceGroup.hasErrorFeedback) {

      var fieldErrId = TagUtils.toFieldErrorId(_choiceGroup.path);
      descBy = (descBy == null) ? fieldErrId : descBy + " " + fieldErrId;
    }
    if (descBy != null) {
      HtmlUtils.escAttribute(out, "aria-describedby", descBy);
    }
  }

  @Override
  protected void writeTag(FacesContext ctx, ResponseWriter out) throws IOException {
    newLine(out);
    super.writeTag(ctx, out);
  }

  @Override
  protected boolean hasClosing() {
    return false;
  }

  @Override
  protected void writeBody(FacesContext ctx, ResponseWriter out) throws IOException {
  }
}
