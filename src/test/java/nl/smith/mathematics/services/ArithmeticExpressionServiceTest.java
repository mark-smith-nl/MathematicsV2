package nl.smith.mathematics.services;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.assertj.core.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import nl.smith.mathematics.domain.ArithmeticExpression;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ArithmeticExpressionServiceTest {

	private final Logger LOGGER = LoggerFactory.getLogger(ArithmeticExpressionServiceTest.class);

	@Autowired
	private ArithmeticExpressionService arithmeticExpressionService;

	@Test
	public void arithmeticExpressionService() {
		assertThat(arithmeticExpressionService, notNullValue());
	}

	@Test
	public void buildArithmeticExpression_IllegalMethodArguments() {
		Arrays.asList(new String[] { null, "", " ", "\t", "\r", "\n", "\n \t\t \n" }).forEach(expression -> {
			try {
				arithmeticExpressionService.buildArithmeticExpression((String) expression);
				throw new IllegalArgumentException(
						"The method argument for building a arithmetic expression was accepted while a ConstraintViolationException should have been thrown.");
			} catch (ConstraintViolationException e) {
				LOGGER.info("Expected exception was thrown.");
			}

		});
	}

	@Test
	public void buildArithmeticExpression_IllegalSubExpressionNesting() {
		Map<String, String> expressionMessageMap = new HashMap<>();

		// @formatter:off
 		/*expressionMessageMap.put("2 + (", "\n" +
                                 "2 + (" + " \n" + 
                                 "    ^" + " \n" + 
                                 "Missing closing tokens: ')'.");
 		
		expressionMessageMap.put("2 + (4*{", "\n" + 
                                 "2 + (4*{" + " \n" + 
                                 "    ^  ^" + " \n" +
                                 "Missing closing tokens: '}', ')'.");*/
		
		expressionMessageMap.put("2 + (4*3] - 7", "\n" + 
                                 "2 + (4*3] - 7" + " \n" + 
                                 "    ^   ^    " + " \n" +
                                 "Wrong close token ']' for open token '('. Expected ')'.");
		
		/*expressionMessageMap.put("2 + (4*3) - 7) + 2", "\n" + 
                                 "2 + (4*3) - 7) + 2" + " \n" + 
                                 "             ^    " + " \n" +
                                 "Missing open token '(' for closing token ')'.");*/
		// @formatter:on

		expressionMessageMap.forEach((expression, message) -> {
			try {

				arithmeticExpressionService.buildArithmeticExpression(expression);
				throw new IllegalArgumentException(String.format(
						"Expected exception was not thrown for building the arithmetic expression: '%s'.\nExpression was expected to be invalid but was valid.", expression));
			} catch (ArithmeticException e) {
				String actualMessage = e.getMessage();
				String expectedMessage = expressionMessageMap.get(expression);
				assertThat(actualMessage, is(expectedMessage));
			}
		});
	}

	@Test
	public void buildArithmeticExpression() {
		List<String> expressions = new ArrayList<>();

		expressions.add("1 + 2");
		expressions.add("1 + 2 * (7 + 4)");
		expressions.add("1 + 2 * (7 + 4) - 8 / (1 - 8)");
		expressions.add("1 + 2 * 5 * 4 * 7 * [7 + 4 * {9 - 2}] - 8 / (1 - 8)");

		expressions.forEach((expression) -> {

			ArithmeticExpression arithmeticExpression = arithmeticExpressionService.buildArithmeticExpression(expression);

			String actualExpression = arithmeticExpression.toString();
			String expectedExpression = expression;

			assertThat(actualExpression, is(expectedExpression));

		});
	}

}
