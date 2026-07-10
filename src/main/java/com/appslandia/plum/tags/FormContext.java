// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.tags;

/**
 *
 * @author Loc Ha
 *
 */
public class FormContext {
  public static final String ATTRIBUTE_FORM_CONTEXT = "__form_context";

  protected String model;
  protected String form;

  public String getModel() {
    return model;
  }

  public FormContext setModel(String model) {
    this.model = model;
    return this;
  }

  public String getForm() {
    return form;
  }

  public FormContext setForm(String form) {
    this.form = form;
    return this;
  }
}
