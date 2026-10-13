// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.pebble;

import java.util.Map;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.Test;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class PebbleTest implements Test {

  @Override
  public boolean apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context,
      int lineNumber) throws PebbleException {
    return doExecute(input, new TemplateEvaluationContext(args, self, context), lineNumber);
  }

  protected abstract boolean doExecute(Object input, TemplateEvaluationContext context, int lineNumber)
      throws PebbleException;

  public String getDescription() {
    return null;
  }
}
