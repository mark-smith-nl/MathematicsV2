package nl.smith.mathematics.exceptions;

public class ArithmeticExpressionUnexpectedCloseException extends ArithmeticExpressionCloseException {

	private static final long serialVersionUID = 1L;

	public ArithmeticExpressionUnexpectedCloseException(char closeToken) {
		super(String.format("Expression does not require a closetoken. Remove closetoken '%c'.", closeToken));
	}
}
