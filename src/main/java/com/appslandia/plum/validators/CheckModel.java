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
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { CheckModel.ConstraintValidatorImpl.class })
@Documented
public @interface CheckModel {

  String message() default "{com.appslandia.common.validators.CheckModel.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String expr();

  String reportProperty();

  public static class ConstraintValidatorImpl implements ConstraintValidator<CheckModel, Object> {
    private static final String VARIABLE_MODEL = "model";

    private CheckModel rule;

    @Inject
    private ElProcessorPool elPool;

    @Override
    public void initialize(CheckModel annotation) {
      rule = annotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
      if (value == null) {
        return true;
      }

      return elPool.execute(el -> {
        el.defineBean(VARIABLE_MODEL, value);

        var result = el.eval(rule.expr());
        if (!(result instanceof Boolean boolResult)) {
          throw new IllegalStateException("Expression must return a boolean: " + rule.expr());
        }

        if (!boolResult) {
          context.disableDefaultConstraintViolation();
          context.buildConstraintViolationWithTemplate(rule.message()).addPropertyNode(rule.reportProperty())
              .addConstraintViolation();
          return false;
        }
        return true;
      });
    }
  }
}
