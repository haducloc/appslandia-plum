// The MIT License (MIT)
// Copyright © 2015 Loc Ha

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.appslandia.plum.base;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.appslandia.common.utils.StringUtils;

import jakarta.enterprise.util.AnnotationLiteral;

/**
 *
 * @author Loc Ha
 *
 */
@Target(value = { ElementType.TYPE, ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface CacheControl {

  public static final String NO_CACHE_POLICY = "noCache";

  String value() default StringUtils.EMPTY_STRING;

  boolean nocache() default false;

  public static final CacheControl NO_CACHE = new CacheControlLiteral(NO_CACHE_POLICY);

  @SuppressWarnings("all")
  public static class CacheControlLiteral extends AnnotationLiteral<CacheControl> implements CacheControl {
    private static final long serialVersionUID = 1L;

    final String policy;

    public CacheControlLiteral(String policy) {
      this.policy = policy;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
      return CacheControl.class;
    }

    @Override
    public String value() {
      return this.policy;
    }

    @Override
    public boolean nocache() {
      return CacheControl.NO_CACHE_POLICY.equals(policy);
    }
  }
}
