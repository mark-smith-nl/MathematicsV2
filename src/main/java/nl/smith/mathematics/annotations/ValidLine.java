package nl.smith.mathematics.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import nl.smith.mathematics.validation.ValidLineConstraintChecker;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidLineConstraintChecker.class)
public @interface ValidLine {
	String message() default "String contains new line{s)";

	Class<?>[] groups() default {};

	// Required by validation runtime
	Class<? extends Payload>[] payload() default {};
}
