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

  final String model;
  final String form;

  public FormContext(String form, String modelVariable) {
    this.form = form;
    this.model = modelVariable;
  }

  public String getForm() {
    return form;
  }

  public String getModel() {
    return model;
  }
}
