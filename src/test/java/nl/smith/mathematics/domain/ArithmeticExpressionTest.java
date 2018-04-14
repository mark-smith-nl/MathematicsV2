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

	private ArithmeticExpression arithmeticExpression;

	private ArithmeticExpression arithmeticSubExpression;

	@Test
	public void expression_NoAggregationOpenToken_NoContent() throws ArithmeticExpressionCloseException {
		arithmeticExpression = new ArithmeticExpression();

		assertThat(arithmeticExpression.toString(), is("..."));

		arithmeticExpression.close();

		assertThat(arithmeticExpression.toString(), is(""));
	}

	@Test
	public void expression_NoAggregationOpenToken_WithContent() throws ArithmeticExpressionCloseException {
		arithmeticExpression = new ArithmeticExpression();
		arithmeticExpression.add('1');
		arithmeticExpression.add('+');
		arithmeticExpression.add('2');

		assertThat(arithmeticExpression.toString(), is("1+2..."));

		arithmeticExpression.close();

		assertThat(arithmeticExpression.toString(), is("1+2"));
	}

	@Test
	public void expression_NoAggregationOpenToken_WithContent_ExtraCloseToken() throws ArithmeticExpressionCloseException {
		arithmeticExpression = new ArithmeticExpression();
		arithmeticExpression.add('1');
		arithmeticExpression.add('+');
		arithmeticExpression.add('2');

		try {
			arithmeticExpression.close(')');
			throw new IllegalArgumentException(String.format("An exception of type '%s' should have been thrown.", ArithmeticExpressionCloseException.class));
		} catch (ArithmeticExpressionCloseException e) {
			LOGGER.info("An expected exception of type '{}' was thrown.", ArithmeticExpressionCloseException.class);
			assertThat(e.getMessage(), is("Expression does not require the closetoken ')'."));
		}

	}

	@Test
	public void expression_WithWrongAggregationOpenToken() {
		try {
			new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter(')'));
			throw new IllegalArgumentException(String.format("An exception of type '%s' should have been thrown.", IllegalArgumentException.class));
		} catch (IllegalArgumentException e) {
			LOGGER.info("An expected exception of type '{}' was thrown.", IllegalArgumentException.class);
			assertThat(e.getMessage(), is("Token is not an opentoken."));
		}
	}

	@Test
	public void expression_WithAggregationOpenToken_NoContent() throws ArithmeticExpressionCloseException {
		arithmeticExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('('));

		assertThat(arithmeticExpression.toString(), is("(..."));

		arithmeticExpression.close(')');

		assertThat(arithmeticExpression.toString(), is("()"));
	}

	@Test
	public void expression_WithAggregationOpenToken_WithContent() throws ArithmeticExpressionCloseException {
		arithmeticExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('('));
		arithmeticExpression.add('1');
		arithmeticExpression.add('+');
		arithmeticExpression.add('2');

		assertThat(arithmeticExpression.toString(), is("(1+2..."));

		arithmeticExpression.close(')');

		assertThat(arithmeticExpression.toString(), is("(1+2)"));
	}

	@Test
	public void expression_WithAggregationOpenToken_WithContent_WrongCloseToken() throws ArithmeticExpressionCloseException {
		arithmeticExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('('));
		arithmeticExpression.add('1');
		arithmeticExpression.add('+');
		arithmeticExpression.add('2');

		assertThat(arithmeticExpression.toString(), is("(1+2..."));

		try {
			arithmeticExpression.close(']');
			throw new IllegalArgumentException(String.format("An exception of type '%s' should have been thrown.", ArithmeticExpressionCloseException.class));
		} catch (ArithmeticExpressionCloseException e) {
			LOGGER.info("An expected exception of type '{}' was thrown.", ArithmeticExpressionCloseException.class);
			assertThat(e.getMessage(), is("Expression can not be close with closetoken ']'. Expected closetoken ')'."));
		}
	}

	@Test
	public void expression_WithAggregationOpenToken_WithContent_NoCloseToken() throws ArithmeticExpressionCloseException {
		arithmeticExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('('));
		arithmeticExpression.add('1');
		arithmeticExpression.add('+');
		arithmeticExpression.add('2');

		assertThat(arithmeticExpression.toString(), is("(1+2..."));

		try {
			arithmeticExpression.close();
			throw new IllegalArgumentException(String.format("An exception of type '%s' should have been thrown.", ArithmeticExpressionCloseException.class));
		} catch (ArithmeticExpressionCloseException e) {
			LOGGER.info("An expected exception of type '{}' was thrown.", ArithmeticExpressionCloseException.class);
			assertThat(e.getMessage(), is("Expression requires the closetoken ')'."));
		}
	}

	@Test
	public void expression_WithAggregationOpenToken_WithContentAndSubExpressions() throws ArithmeticExpressionCloseException {
		arithmeticExpression = new ArithmeticExpression();
		arithmeticExpression.add('1');
		arithmeticExpression.add('+');
		arithmeticExpression.add('2');
		arithmeticExpression.add('*');

		assertThat(arithmeticExpression.toString(), is("1+2*..."));

		arithmeticSubExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('{'), arithmeticExpression.getLength());
		arithmeticExpression.add(arithmeticSubExpression);
		arithmeticSubExpression.add('3');
		arithmeticSubExpression.add('/');
		arithmeticSubExpression.add('4');
		arithmeticSubExpression.add('+');
		arithmeticSubExpression.add('5');
		arithmeticSubExpression.close('}');

		assertThat(arithmeticExpression.toString(), is("1+2*{3/4+5}..."));

		arithmeticExpression.add('-');
		arithmeticExpression.add('6');
		arithmeticExpression.add('+');
		arithmeticSubExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('['), arithmeticExpression.getLength());
		arithmeticExpression.add(arithmeticSubExpression);
		arithmeticSubExpression.add('7');
		arithmeticSubExpression.add('/');
		arithmeticSubExpression.add('8');
		arithmeticSubExpression.close(']');
		arithmeticExpression.close();

		assertThat(arithmeticExpression.toString(), is("1+2*{3/4+5}-6+[7/8]"));
	}

	@Test
	public void expression_Append_WithCharacterAfterClose() throws ArithmeticExpressionCloseException {
		arithmeticExpression = new ArithmeticExpression();

		arithmeticExpression.add('1');
		arithmeticExpression.add('+');
		arithmeticExpression.add('2');

		arithmeticExpression.close();

		try {
			arithmeticExpression.add('+');
			throw new IllegalArgumentException(String.format("An exception of type '%s' should have been thrown.", IllegalStateException.class));
		} catch (IllegalStateException e) {
			LOGGER.info("An expected exception of type '{}' was thrown.", IllegalStateException.class);
			assertThat(e.getMessage(), is("Closed arithmetic expression can not be appended."));
		}
	}

	@Test
	public void expression_Append_WithExpressionAfterClose() throws ArithmeticExpressionCloseException {
		arithmeticExpression = new ArithmeticExpression();
		arithmeticExpression.add('1');
		arithmeticExpression.add('+');
		arithmeticExpression.add('2');

		arithmeticExpression.close();

		try {
			arithmeticSubExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('{'), arithmeticExpression.getLength());
			arithmeticExpression.add(arithmeticSubExpression);
			throw new IllegalArgumentException(String.format("An exception of type '%s' should have been thrown.", IllegalStateException.class));
		} catch (IllegalStateException e) {
			LOGGER.info("An expected exception of type '{}' was thrown.", IllegalStateException.class);
			assertThat(e.getMessage(), is("Closed arithmetic expression can not be appended."));
		}
	}

	@Test
	public void test() throws ArithmeticExpressionCloseException {
		arithmeticExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('('));
		arithmeticExpression.add('1');
		System.out.println(arithmeticExpression);
		arithmeticExpression.add('+');
		System.out.println(arithmeticExpression);
		arithmeticExpression.add('2');
		System.out.println(arithmeticExpression);
		arithmeticExpression.add('+');
		System.out.println(arithmeticExpression);
		arithmeticExpression.add('3');
		System.out.println(arithmeticExpression);

		arithmeticExpression.add(',');
		System.out.println(arithmeticExpression);
		arithmeticExpression.add('4');
		System.out.println(arithmeticExpression);
		arithmeticExpression.add('*');
		System.out.println(arithmeticExpression);
		arithmeticExpression.add('6');
		System.out.println(arithmeticExpression);

		arithmeticExpression.close(')');
		System.out.println(arithmeticExpression);

	}

}
