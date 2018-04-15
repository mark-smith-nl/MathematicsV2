package nl.smith.mathematics.exceptions;

public class ArithmeticExpressionMissingCloseTokenException extends ArithmeticExpressionCloseException {

	private static final long serialVersionUID = 1L;

	public ArithmeticExpressionMissingCloseTokenException(char expectedCloseToken) {
		super(String.format("Expression requires the closetoken '%c'.", expectedCloseToken));
	}
}
