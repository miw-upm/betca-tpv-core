package es.upm.miw.betca_tpv_core.domain.model.validations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ListNotEmptyValidator.class)
public @interface ListNotEmpty {
    String message() default "Expected not empty";

    Class< ? >[] groups() default {};

    Class< ? extends Payload >[] payload() default {};
}
