package nl.smith.mathematics.services;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
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

@RunWith(SpringRunner.class)
@SpringBootTest
public class ArithmeticExpressionServiceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ArithmeticExpressionService arithmeticExpressionService;
	@Autowired
	private TextAnnotationService textAnnotationService;

	@Test
	public void arithmeticExpressionService() {
		assertThat(arithmeticExpressionService, notNullValue());

	}

	@Test
	public void buildArithmeticExpression_IllegalMethodArguments() {
		Arrays.asList(new String[] { null, "", " ", "\t", "\r", "\n", "\n \t\t \n" }).forEach(expression -> {
			try {
				arithmeticExpressionService.buildArithmeticExpression((String) expression);
				throw new IllegalArgumentException("The method argument was accepted while a ConstraintViolationException should have been thrown.");
			} catch (ConstraintViolationException e) {
				logger.info("Expected exception was thrown for method argument.");
			}

		});
	}

	@Test
	public void buildArithmeticExpression_IllegalSubExpressionNesting() {
		Map<String, String> expressionMessageMap = new HashMap<>();

		// @formatter:off
 		expressionMessageMap.put("2 + (", "\n" +
 				                 "2 + (" + " " + "\n" + 
 				                 "    ^ " + "\n" + 
 				                 "Missing closing tokens: ')'");
 		
 	
		expressionMessageMap.put("2 + (4*{", "\n" + 
		                         "2 + (4*{" + " \n" + 
				                 "    ^  ^ " + "\n" +
		                         "Missing closing tokens: '}', ')'");
		// @formatter:on

		expressionMessageMap.forEach((expression, message) -> {
			try {
				arithmeticExpressionService.buildArithmeticExpression(expression);
			} catch (ArithmeticException e) {
				String actualMessage = e.getMessage();
				String expectedMessage = expressionMessageMap.get(expression);

				assertThat(actualMessage, is(expectedMessage));
			}
		});

	}

}
