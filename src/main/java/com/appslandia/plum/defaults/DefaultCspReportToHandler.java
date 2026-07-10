// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.defaults;

import java.io.BufferedReader;
import java.io.StringWriter;

import com.appslandia.plum.base.AppLogger;
import com.appslandia.plum.base.CspReportToHandler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 *
 * @author Loc Ha
 *
 */
@ApplicationScoped
public class DefaultCspReportToHandler implements CspReportToHandler {

  @Inject
  protected AppLogger appLogger;

  @Override
  public void handle(BufferedReader violation) throws Exception {
    var out = new StringWriter(512);
    violation.transferTo(out);

    appLogger.warn(out.toString());
  }
}
