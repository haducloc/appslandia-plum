// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.appslandia.common.utils.ContentTypes;

import jakarta.enterprise.util.AnnotationLiteral;

/**
 *
 * @author Loc Ha
 *
 */
@Target(value = { ElementType.TYPE, ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface ConsumeType {

  String value();

  public static final ConsumeType APP_JSON = new ConsumeTypeLiteral(ContentTypes.APP_JSON);

  @SuppressWarnings("all")
  public static class ConsumeTypeLiteral extends AnnotationLiteral<ConsumeType> implements ConsumeType {
    private static final long serialVersionUID = 1L;

    final String contentType;

    public ConsumeTypeLiteral(String contentType) {
      this.contentType = contentType;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
      return ConsumeType.class;
    }

    @Override
    public String value() {
      return contentType;
    }
  }
}
