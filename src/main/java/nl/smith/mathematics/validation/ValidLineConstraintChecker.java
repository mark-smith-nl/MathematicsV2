package nl.smith.mathematics.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.smith.mathematics.annotations.ValidLine;

public class ValidLineConstraintChecker implements ConstraintValidator<nl.smith.mathematics.annotations.ValidLine, String> {

	@Override
	public void initialize(ValidLine constraintAnnotation) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isValid(String line, ConstraintValidatorContext context) {
		boolean valid = true;

		if (line != null && line.indexOf('\n') > -1) {
			valid = false;
		}

		return valid;
	}

}
