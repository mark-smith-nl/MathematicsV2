package nl.smith.mathematics.patterns;

public class ExpressionElement {

	/** 
	 * <pre>
	 * ℕ*: 1, 2, 3, ... (positive integer)
	 * </pre>
	 */
	public static final String POSITIVE_NATURAL_NUMBER = "[1-9]\\d*";

	/** 
	 * <pre>
	 * ℕ0: 0, 1, 2, 3, ... (non negative integer)
	 * </pre>
	 */
	public static final String NATURAL_NUMBER = "0|" + POSITIVE_NATURAL_NUMBER;

	/** 
	 * <pre>
	 *  ℤ: ..., -3, -2, -1, 0, 1, 2, 3, ... (integer)
	 * </pre>
	 */
	public static final String INTEGER = "[\\-]?" + POSITIVE_NATURAL_NUMBER + "|0";

	/**
	 * <pre>
	 * A fraction begins with a dot, followed by zero or more (decimal) digits and ends with a non zero digit.
	 * Trailing zeros are <b>not</b> accepted.The pattern contains two groups:
	 * 
	 * <b>First group</b>: decimal point
	 * <b>Second group</b>: integer part with zero or more leading zeros (list of decimals)
	 * </pre>
	 */
	public static final String FRACTION = "(\\.)(\\d*[1-9])";

	/**
	 * <pre>
	 * Decimal number.
	 * This number contains the following groups:
	 * 
	 * <b>First group</b>: decimal number (–∞, 1] ∪ [1, ∞)
	 *     <b>Second group</b>: integer part of first group {n ∈ ℤ | n ∉ [0]}
	 *     Third group: fractional part of first group
	 *     Fourth group: decimal point
	 *     <b>Fifth group</b>: integer part  of third group with zero or more leading zeros (list of decimals)
	 * <b>Sixth group</b>: decimal number (-1, 1)
	 *     Seventh group: decimal point
	 *     <b>Eight group</b>: integer part of third group with zero or more leading zeros (list of decimals)
	 * 
	 * A string that matched this pattern has <b>either</b> a <b>not null</b> first group or sixth group.
	 * If the first group is null then the second, third, fourth and fifth groups are also null.
	 * If the sixth group is null then the seventh and eight groups are also null.
	 * 
	 * </pre>
	 */
	public static final String DECIMAL_NUMBER = "((" + INTEGER + ")(" + FRACTION + ")?)" + "|(\\-0" + FRACTION + ")";

	/**
	 * <pre>
	 * Example of numbers using scientific notation: −5.3×10<sup>4</sup> ⇒ -5.3E104
	 * Groups
	 * </pre>
	 */
	public static final String SCIENTIFIC_NUMBER = "\\-?[1-9](" + FRACTION + ")?(E(" + INTEGER + "))";
}
