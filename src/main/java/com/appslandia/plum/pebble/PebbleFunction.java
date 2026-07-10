// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.pebble;

import java.io.IOException;
import java.util.Map;

import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.Function;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

/**
 *
 * @author Loc Ha
 *
 */
public abstract class PebbleFunction implements Function {

  @Override
  public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber)
      throws PebbleException {
    try {
      return doExecute(new TemplateEvaluationContext(args, self, context), lineNumber);

    } catch (IOException ex) {
      throw new PebbleException(ex, ex.getMessage());
    }
  }

  protected abstract Object doExecute(TemplateEvaluationContext context, int lineNumber)
      throws PebbleException, IOException;

  public String getDescription() {
    return null;
  }
}
