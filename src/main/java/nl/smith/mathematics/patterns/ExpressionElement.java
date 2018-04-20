package nl.smith.mathematics.patterns;

public class ExpressionElement {

	/** 1, 2, 3, ... */
	public static final String INTEGER_LARGER_THAN_ZERO = "([1-9]\\d*)";

	/** 0, 1, 2, 3, ... */
	public static final String POSITIVE_INTEGER = "(0|" + INTEGER_LARGER_THAN_ZERO + ")";

	/** ..., -3, -2, -1, 0, 1, 2, 3, ... */
	public static final String INTEGER = "([\\-]?" + INTEGER_LARGER_THAN_ZERO + ")|0";

	public static final String FRACTION = "(\\d*[1-9])";

}
