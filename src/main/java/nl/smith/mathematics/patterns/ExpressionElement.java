package nl.smith.mathematics.patterns;

public class ExpressionElement {

	/**
	 * <pre>
	 *  ℕ*: 1, 2, 3, ... (natural number - not zero)
	 * <pre>
	 */
	public static final String POSITIVE_INTEGER = "[1-9]\\d*";

	/** 
	 * <pre>
	 * ℤ: ..., -3, -2, -1, 0, 1, 2, 3, ... (integer)
	 * </pre>
	 */
	public static final String INTEGER = "[\\-]?" + POSITIVE_INTEGER + "|0";

	/**
	 * <pre>
	 * A fraction begins with a dot, followed by either:
	 * 
	 *  - zero or more (decimal) digits ending with a non zero digit
	 *    Trailing zeros are <b>not</b> accepted.
	 *  - zero or more digits, a list off one or more digits enclosed within pipes (repeating fraction).
	 *    The repeating fraction should contain at least one non zero digit.
	 * 
	 * <b>First group</b>: decimal point
	 * <b>Second group</b>: integer part with zero or more leading zeros (list of decimals)
	 * </pre>
	 */
	public static final String FRACTION = "(\\.)(\\d*[1-9])";
	/**
	 * <pre>
	 * ℚ (Rational/Decimal number)
	 * 
	 * This number contains the following groups:
	 * 
	 * <b>First group</b>: decimal number {n ∈ ℚ | (–∞, -1] ∪ [0, ∞)}
	 *     <b>Second group</b>: integer part of first group {n ∈ ℤ}
	 *     Third group: fractional part of first group
	 *     Fourth group: decimal point of third group (fractional part)
	 *     <b>Fifth group</b>: integer part  of third group with zero or more leading zeros (list of decimals)
	 * <b>Sixth group</b>: decimal number {n ∈ ℚ | (-1, 0)}
	 *     Seventh group: integer part of sixth group: always -0
	 *     Eight group: fractional part of sixth group
	 *     Ninth group: decimal point eight group (of fractional part)
	 *     <b>Tenth group</b>: integer part  of eight group with zero or more leading zeros (list of decimals)
	 * 
	 * A string that matched this pattern has <b>either</b> a <b>not null</b> first group or sixth group.
	 * If the first group is null then the second, third, fourth and fifth groups are also null.
	 * If the sixth group is null then the seventh, eight, ninth and tenth groups are also null.
	 * 
	 * </pre>
	 */
	public static final String DECIMAL_NUMBER = "((" + INTEGER + ")(" + FRACTION + ")?)" + "|((\\-0)(" + FRACTION + "))";

	/**
	 * Example of numbers using scientific notation: −5.3×104 ===> -5.3E104
	 */
	public static final String SCIENTIFIC_NUMBER = "\\-?[1-9](" + FRACTION + ")?(E(" + INTEGER + "))";
}
