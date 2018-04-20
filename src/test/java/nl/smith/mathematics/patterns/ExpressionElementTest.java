package nl.smith.mathematics.patterns;

import static nl.smith.mathematics.patterns.ExpressionElement.INTEGER;
import static nl.smith.mathematics.patterns.ExpressionElement.INTEGER_LARGER_THAN_ZERO;
import static nl.smith.mathematics.patterns.ExpressionElement.POSITIVE_INTEGER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class ExpressionElementTest {

	@Test
	public void integerLargerThanZero() {
		for (int i = -100; i <= 0; i++) {
			assertFalse(String.format("Value '%d' should not be accepted an integerLargerThanZero.", i), String.valueOf(i).matches(INTEGER_LARGER_THAN_ZERO));
		}

		for (int i = 1; i <= 100; i++) {
			assertTrue(String.format("Value '%d' should be accepted an integerLargerThanZero.", i), String.valueOf(i).matches(INTEGER_LARGER_THAN_ZERO));
		}
	}

	@Test
	public void positiveInteger() {
		Arrays.asList("a", " 1", "+1", "-1", "-11", "+11", "2.5", "-2.5", "2.5E2").forEach(value -> {
			assertFalse(String.format("Value '%s' should not be accepted as positiveInteger.", value), value.matches(POSITIVE_INTEGER));
		});

		for (int i = 0; i <= 100; i++) {
			assertTrue(String.format("Value '%d' should be accepted as a positiveInteger.", i), String.valueOf(i).matches(POSITIVE_INTEGER));
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

}
