package nl.smith.mathematics.domain;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.smith.mathematics.exceptions.ArithmeticExpressionCloseException;

public class ArithmeticExpressionTest {

	private final Logger LOGGER = LoggerFactory.getLogger(ArithmeticExpressionTest.class);

	private static AggregationTokenSets aggregationTokenSets = new AggregationTokenSets("(){}[]");

	@Test
	public void expression_NoAggregationOpenToken_NoContent() throws ArithmeticExpressionCloseException {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression();

		assertThat(arithmeticExpression.toString(), is("..."));

		arithmeticExpression.close();

		assertThat(arithmeticExpression.toString(), is(""));
	}

	@Test
	public void expression_NoAggregationOpenToken_WithContent() throws ArithmeticExpressionCloseException {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression();
		arithmeticExpression.addCharacter('1');
		arithmeticExpression.addCharacter('+');
		arithmeticExpression.addCharacter('2');

		assertThat(arithmeticExpression.toString(), is("1+2..."));

		arithmeticExpression.close();

		assertThat(arithmeticExpression.toString(), is("1+2"));
	}

	@Test
	public void expression_NoAggregationOpenToken_WithContent_ExtraCLoseToken() {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression();
		arithmeticExpression.addCharacter('1');
		arithmeticExpression.addCharacter('+');
		arithmeticExpression.addCharacter('2');

		try {
			arithmeticExpression.closeWithToken(')');
			throw new IllegalArgumentException("The expression could be closed while a ArithmeticExpressionCloseException should have been thrown.");
		} catch (ArithmeticExpressionCloseException e) {
			LOGGER.info("Expected exception was thrown by closeWithToken method.");
			assertThat(e.getMessage(), is("Expression does not require the closetoken ')'."));
		}

	}

	@Test
	public void expression_WithAggregationOpenToken_NoContent() throws ArithmeticExpressionCloseException {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('('));

		assertThat(arithmeticExpression.toString(), is("(..."));

		arithmeticExpression.closeWithToken(')');

		assertThat(arithmeticExpression.toString(), is("()"));
	}

	@Test
	public void expression_WithAggregationOpenToken_WithContent() throws ArithmeticExpressionCloseException {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('('));
		arithmeticExpression.addCharacter('1');
		arithmeticExpression.addCharacter('+');
		arithmeticExpression.addCharacter('2');

		assertThat(arithmeticExpression.toString(), is("(1+2..."));

		arithmeticExpression.closeWithToken(')');

		assertThat(arithmeticExpression.toString(), is("(1+2)"));
	}

	@Test
	public void expression_WithAggregationOpenToken_WithContent_WrongCLoseToken() {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('('));
		arithmeticExpression.addCharacter('1');
		arithmeticExpression.addCharacter('+');
		arithmeticExpression.addCharacter('2');

		assertThat(arithmeticExpression.toString(), is("(1+2..."));

		try {
			arithmeticExpression.closeWithToken(']');
		} catch (ArithmeticExpressionCloseException e) {
			LOGGER.info("Expected exception was thrown by closeWithToken method.");
			assertThat(e.getMessage(), is("Expression can not be close with closetoken ']'. Expected closetoken ')'."));
		}
	}

	@Test
	public void expression_WithAggregationOpenToken_WithContent_NoCLoseToken() {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('('));
		arithmeticExpression.addCharacter('1');
		arithmeticExpression.addCharacter('+');
		arithmeticExpression.addCharacter('2');

		assertThat(arithmeticExpression.toString(), is("(1+2..."));

		try {
			arithmeticExpression.close();
		} catch (ArithmeticExpressionCloseException e) {
			LOGGER.info("Expected exception was thrown by closeWithToken method.");
			assertThat(e.getMessage(), is("Expression requires the closetoken ')'."));
		}
	}

	@Test
	public void expression_WithAggregationOpenToken_WithContentAndSubExpressions() throws ArithmeticExpressionCloseException {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression();
		arithmeticExpression.addCharacter('1');
		arithmeticExpression.addCharacter('+');
		arithmeticExpression.addCharacter('2');
		arithmeticExpression.addCharacter('*');

		assertThat(arithmeticExpression.toString(), is("1+2*..."));

		ArithmeticExpression arithmeticSubExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('{'), arithmeticExpression.getLength());
		arithmeticExpression.addExpression(arithmeticSubExpression);
		arithmeticSubExpression.addCharacter('3');
		arithmeticSubExpression.addCharacter('/');
		arithmeticSubExpression.addCharacter('4');
		arithmeticSubExpression.addCharacter('+');
		arithmeticSubExpression.addCharacter('5');
		arithmeticSubExpression.closeWithToken('}');

		assertThat(arithmeticExpression.toString(), is("1+2*{3/4+5}..."));

		arithmeticExpression.addCharacter('-');
		arithmeticExpression.addCharacter('6');
		arithmeticExpression.addCharacter('+');
		arithmeticSubExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('['), arithmeticExpression.getLength());
		arithmeticExpression.addExpression(arithmeticSubExpression);
		arithmeticSubExpression.addCharacter('7');
		arithmeticSubExpression.addCharacter('/');
		arithmeticSubExpression.addCharacter('8');
		arithmeticSubExpression.closeWithToken(']');
		arithmeticExpression.close();

		assertThat(arithmeticExpression.toString(), is("1+2*{3/4+5}-6+[7/8]"));
	}
}
