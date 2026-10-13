// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.base;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.appslandia.common.utils.StringUtils;

/**
 *
 * @author Loc Ha
 *
 */
@Target(value = { ElementType.PARAMETER, ElementType.FIELD })
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface BindField {

  String name() default StringUtils.EMPTY_STRING;

  String converter() default StringUtils.EMPTY_STRING;

  String defaultValue() default StringUtils.EMPTY_STRING;

  String resKey() default StringUtils.EMPTY_STRING;
}
