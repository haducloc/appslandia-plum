// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.jsp;

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
@Target(value = { ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface Function {

  String name() default "";

  String description() default "";
}
