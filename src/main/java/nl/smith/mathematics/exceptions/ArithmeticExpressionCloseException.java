package nl.smith.mathematics.exceptions;

public class ArithmeticExpressionCloseException extends Exception {

	private static final long serialVersionUID = 1L;

	private final int[] positions;

	public ArithmeticExpressionCloseException(String message, int... positions) {
		super(message);
		this.positions = positions;
	}

	public int[] getPositions() {
		return positions;
	}

}
