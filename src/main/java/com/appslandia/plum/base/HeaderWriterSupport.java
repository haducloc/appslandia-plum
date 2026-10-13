// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.util.function.Consumer;

import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Loc Ha
 *
 */
public interface HeaderWriterSupport {

  Consumer<HttpServletResponse> getHeaderWriter();
}
