package nl.smith.mathematics.exceptions;

public class ArithmeticExpressionWongCloseTokenException extends ArithmeticExpressionCloseException {

	private static final long serialVersionUID = 1L;

	public ArithmeticExpressionWongCloseTokenException(char closeToken, char expectedCloseToken) {
		super(String.format("Expression can not be close with closetoken '%c'. Expected closetoken '%c'.", closeToken, expectedCloseToken));
	}
}
