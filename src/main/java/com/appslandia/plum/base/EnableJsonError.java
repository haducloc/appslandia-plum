// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.util.AnnotationLiteral;

/**
 *
 * @author Loc Ha
 *
 */
@Target(value = { ElementType.TYPE, ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface EnableJsonError {

  public static final EnableJsonError IMPL = new EnableJsonErrorLiteral();

  @SuppressWarnings("all")
  public static class EnableJsonErrorLiteral extends AnnotationLiteral<EnableJsonError> implements EnableJsonError {
    private static final long serialVersionUID = 1L;

    public EnableJsonErrorLiteral() {
    }

    @Override
    public Class<? extends Annotation> annotationType() {
      return EnableJsonError.class;
    }
  }
}
