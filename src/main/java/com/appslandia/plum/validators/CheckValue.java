// Licensed under the MIT License.
// See LICENSE file in the project root for details.

package com.appslandia.plum.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.appslandia.plum.base.ElProcessorPool;

import jakarta.inject.Inject;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

/**
 *
 * @author Loc Ha
 *
 */
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR,
    ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { CheckValue.ConstraintValidatorImpl.class })
@Documented
public @interface CheckValue {

  String message() default "{com.appslandia.common.validators.CheckValue.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String value();

  public static class ConstraintValidatorImpl implements ConstraintValidator<CheckValue, Object> {
    private static final String VARIABLE_VALUE = "value";

    private String expression;

    @Inject
    private ElProcessorPool elPool;

    @Override
    public void initialize(CheckValue annotation) {
      expression = annotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
      if (value == null) {
        return true;
      }

      return elPool.execute(el -> {
        el.defineBean(VARIABLE_VALUE, value);

        var result = el.eval(expression);
        if (!(result instanceof Boolean boolResult)) {
          throw new IllegalStateException("Expression must return a boolean: " + expression);
        }

        return boolResult;
      });
    }
  }
}
