package nl.smith.mathematics.patterns;

import static nl.smith.mathematics.patterns.ExpressionElement.DECIMAL_NUMBER;
import static nl.smith.mathematics.patterns.ExpressionElement.FRACTION;
import static nl.smith.mathematics.patterns.ExpressionElement.INTEGER;
import static nl.smith.mathematics.patterns.ExpressionElement.NATURAL_NUMBER;
import static nl.smith.mathematics.patterns.ExpressionElement.POSITIVE_NATURAL_NUMBER;
import static nl.smith.mathematics.patterns.ExpressionElement.SCIENTIFIC_NUMBER;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class ExpressionElementTest {

	@Test
	public void positiveNaturalNumber() {
		for (int i = -100; i <= 0; i++) {
			assertFalse(String.format("Value '%d' should not be accepted an positiveNaturalNumber.", i), String.valueOf(i).matches(POSITIVE_NATURAL_NUMBER));
		}

		for (int i = 1; i <= 100; i++) {
			assertTrue(String.format("Value '%d' should be accepted an positiveNaturalNumber.", i), String.valueOf(i).matches(POSITIVE_NATURAL_NUMBER));
		}
	}

	@Test
	public void naturalNumber() {
		Arrays.asList("a", " 1", "+1", "-1", "-11", "+11", "2.5", "-2.5", "2.5E2").forEach(value -> {
			assertFalse(String.format("Value '%s' should not be accepted as naturalNumber.", value), value.matches(NATURAL_NUMBER));
		});

		for (int i = 0; i <= 100; i++) {
			assertTrue(String.format("Value '%d' should be accepted as a naturalNumber.", i), String.valueOf(i).matches(NATURAL_NUMBER));
		}
	}

	@Test
	public void integer() {
		for (int i = -100; i <= 100; i++) {
			assertTrue(String.format("Value '%d' should be accepted as an integer.", i), String.valueOf(i).matches(INTEGER));
		}

		Arrays.asList("a", " 1", "+1", "+11", "2.5", "-2.5", "2.5E2", "+0", "-0").forEach(value -> {
			assertFalse(String.format("Value '%s' should not be accepted as an integer.", value), value.matches(INTEGER));
		});
	}

	@Test
	public void fraction() {
		Pattern pattern = Pattern.compile(FRACTION);
		Arrays.asList(".1", ".01", ".0234").forEach(value -> {
			Matcher matcher = pattern.matcher(value);
			assertTrue(String.format("Value '%s' should be accepted as a fraction.", value), matcher.matches());
			assertThat(matcher.groupCount(), is(2));
			assertThat(matcher.group(1), is("."));
		});

		Arrays.asList("0.1", "0.10", "-0.1", "+0.1").forEach(value -> {
			assertFalse(String.format("Value '%s' should not be accepted as a fraction.", value), value.matches(FRACTION));
		});
	}

	@Test
	public void decimalNumber() {
		Arrays.asList("2", "2.01", "-2", "-2.01", "0", "0.33", "-0.33", "0.2", "-0.2").forEach(value -> {
			assertTrue(String.format("Value '%s' should be accepted as a decimal.", value), value.matches(DECIMAL_NUMBER));
		});

		Arrays.asList("-0", "+0", "0.0", "+0.0", "-0.0", "2E4", "-2E4", "2.1E4", "-2.1E4").forEach(value -> {
			assertFalse(String.format("Value '%s' should not be accepted as a decimal.", value), value.matches(DECIMAL_NUMBER));
		});

	}

	@Test
	public void scientificNumber() {
		Arrays.asList("2E3", "2.01E3", "-2E3", "-2.01E3", "0E3", "0.33E3", "-0.33E3", "0.2E3", "-0.2E3").forEach(value -> {
			assertTrue(String.format("Value '%s' should be accepted as a decimal.", value), value.matches(SCIENTIFIC_NUMBER));
		});

		/*
		 * Arrays.asList("-0", "+0", "0.0", "+0.0", "-0.0", "2E4", "-2E4", "2.1E4", "-2.1E4").forEach(value -> {
		 * assertFalse(String.format("Value '%s' should not be accepted as a decimal.", value), value.matches(DECIMAL_NUMBER)); });
		 */

	}

	public static void main(String[] args) {
		System.out.println(SCIENTIFIC_NUMBER);
		System.out.println(DECIMAL_NUMBER);
		// (([\-]?[1-9]\d*|0)((\.)(\d*[1-9]))?)|(\-0(\.)(\d*[1-9]))
		// ab Bcd De ECB A f g Gh HF
		Pattern pattern = Pattern.compile(DECIMAL_NUMBER);

		Map<String, List<String>> valueMap = new LinkedHashMap<>();
		valueMap.put("-2.05", Arrays.asList("", "", "", "", "", "", "", ""));
		valueMap.forEach((key, value) -> {
			Matcher matcher = pattern.matcher(key);
			System.out.println(matcher.matches());
			System.out.println(matcher.groupCount());
			for (int i = 1; i <= matcher.groupCount(); i++) {
				System.out.println(matcher.group(i));
			}
		});

		/*
		 * BigInteger numerator = new BigInteger("05"); BigInteger denominator = BigInteger.TEN.pow("05".length());
		 * 
		 * System.out.println(numerator); System.out.println(denominator);
		 */
	}

}
