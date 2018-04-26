package nl.smith.mathematics.patterns;

import static nl.smith.mathematics.patterns.ExpressionElement.DECIMAL_NUMBER;
import static nl.smith.mathematics.patterns.ExpressionElement.FRACTION;
import static nl.smith.mathematics.patterns.ExpressionElement.INTEGER;
import static nl.smith.mathematics.patterns.ExpressionElement.POSITIVE_INTEGER;
import static nl.smith.mathematics.patterns.ExpressionElement.SCIENTIFIC_NUMBER;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class ExpressionElementTest {

	@Test
	public void positiveInteger() {
		for (int i = -100; i <= 0; i++) {
			assertFalse(String.format("Value '%d' should not be accepted an positive natural number.", i), String.valueOf(i).matches(POSITIVE_INTEGER));
		}

		for (int i = 1; i <= 100; i++) {
			assertTrue(String.format("Value '%d' should be accepted an positive natural number.", i), String.valueOf(i).matches(POSITIVE_INTEGER));
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

	/*
	 * @Test public void fraction2() { Pattern pattern = Pattern.compile(FRACTION); Arrays.asList(".|1|R", ".0|1|R", ".0234|12|R").forEach(value -> { Matcher matcher =
	 * pattern.matcher(value); assertTrue(String.format("Value '%s' should be accepted as a fraction.", value), matcher.matches()); assertThat(matcher.groupCount(), is(2));
	 * assertThat(matcher.group(1), is(".")); });
	 * 
	 * Arrays.asList(".||R", ".1|0|R", ".1|00|R").forEach(value -> { assertFalse(String.format("Value '%s' should not be accepted as a fraction.", value), value.matches(FRACTION));
	 * });
	 * 
	 * }
	 */

	@Test
	public void decimalNumber() {
		List<Map<String, Object>> numberMapElements = new ArrayList<>();
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("value", "2");
		valueMap.put("expectedGroupValues", Arrays.asList("2", "2", "", "", "", null, null, null, null, null));
		numberMapElements.add(valueMap);

		valueMap.put("value", "2.01");
		valueMap.put("expectedGroupValues", Arrays.asList("2.01", "2", ".01", ".", "01", null, null, null, null, null));
		numberMapElements.add(valueMap);

		valueMap.put("value", "-2");
		valueMap.put("expectedGroupValues", Arrays.asList("-2", "-2", "", "", "", null, null, null, null, null));
		numberMapElements.add(valueMap);

		valueMap.put("value", "-2.01");
		valueMap.put("expectedGroupValues", Arrays.asList("-2.01", "-2", ".01", ".", "01", null, null, null, null, null));
		numberMapElements.add(valueMap);

		valueMap.put("value", "0");
		valueMap.put("expectedGroupValues", Arrays.asList(null, null, null, null, null, "0", "", ""));
		numberMapElements.add(valueMap);

		valueMap.put("value", "0.33");
		valueMap.put("expectedGroupValues", Arrays.asList("0.33", "0", ".330", ".", "33", null, null, null, null, null));
		numberMapElements.add(valueMap);

		valueMap.put("value", "0.033");
		valueMap.put("expectedGroupValues", Arrays.asList("0.033", "0", ".033", ".", "033", null, null, null, null, null));
		numberMapElements.add(valueMap);

		valueMap.put("value", "-0.33");
		valueMap.put("expectedGroupValues", Arrays.asList(null, null, null, null, null, "-0.33", "-0", ".33", ".", "33"));
		numberMapElements.add(valueMap);

		valueMap.put("value", "-0.033");
		valueMap.put("expectedGroupValues", Arrays.asList(null, null, null, null, null, "-0.033", "-0", ".033", ".", "033"));
		numberMapElements.add(valueMap);

		Pattern pattern = Pattern.compile(DECIMAL_NUMBER);

		numberMapElements.forEach((map) -> {
			String value = (String) map.get("value");
			Matcher matcher = pattern.matcher(value);
			List<String> expectedGroupValues = (List<String>) map.get("expectedGroupValues");

			assertThat(matcher.matches(), is(true));
			assertThat(matcher.groupCount(), is(10));

			for (int i = 1; i <= matcher.groupCount(); i++) {
				assertThat(String.format("Value '%s' should be accepted as a decimal number.", value), matcher.group(i), is(expectedGroupValues.get(i - 1)));
			}
		});

		Arrays.asList("-0", "+0", "0.0", "+0.0", "-0.0", "2E4", "-2E4", "2.1E4", "-2.1E4").forEach(value -> {
			assertFalse(String.format("Value '%s' should not be accepted as a decimal number.", value), value.matches(DECIMAL_NUMBER));
		});

	}

	@Test
	public void scientificNumber() {
		Arrays.asList("2E3", "2.01E3", "-2E3", "-2.01E3", "2E-3", "2.01E-3", "-2E-3", "-2.01E-3").forEach(value -> {
			assertTrue(String.format("Value '%s' should be accepted as a scientific number.", value), value.matches(SCIENTIFIC_NUMBER));
		});

		Arrays.asList("-0.33E3", "0.2E3", "-0.2E3", "0.33E3", "0E3", "-0", "+0", "0.0", "+0.0", "-0.0").forEach(value -> {
			assertFalse(String.format("Value '%s' should not be accepted as a cientific number.", value), value.matches(SCIENTIFIC_NUMBER));
		});

	}

	public static void main(String[] args) {
		System.out.println(FRACTION);
		// (\.)( (\d*[1-9])|(\d*\|\d*[1-9]\d*\|R) )
		//
	}
}
