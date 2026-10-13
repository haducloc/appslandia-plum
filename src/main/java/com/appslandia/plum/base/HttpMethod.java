// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Loc Ha
 *
 */
@Target(value = { ElementType.TYPE })
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface HttpMethod {

  public static final String GET = "GET";
  public static final String POST = "POST";
  public static final String HEAD = "HEAD";
  public static final String PUT = "PUT";
  public static final String DELETE = "DELETE";
  public static final String PATCH = "PATCH";

  public static final String OPTIONS = "OPTIONS";
  public static final String TRACE = "TRACE";

  String value();
}
