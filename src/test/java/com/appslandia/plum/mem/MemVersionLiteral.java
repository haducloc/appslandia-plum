// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.mem;

import jakarta.enterprise.util.AnnotationLiteral;

/**
 *
 * @author Loc Ha
 *
 */
@SuppressWarnings("all")
public class MemVersionLiteral extends AnnotationLiteral<MemVersion> implements MemVersion {
  private static final long serialVersionUID = 1L;

  public static final MemVersion IMPL = new MemVersionLiteral();

  private MemVersionLiteral() {
  }
}
