// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.io.BufferedReader;

/**
 *
 * @author Loc Ha
 *
 */
public interface CspReportToHandler {

  void handle(BufferedReader violation) throws Exception;
}
