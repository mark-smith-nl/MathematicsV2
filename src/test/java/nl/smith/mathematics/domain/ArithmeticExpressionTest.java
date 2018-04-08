package nl.smith.mathematics.domain;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ArithmeticExpressionTest {

	private static AggregationTokenSets aggregationTokenSets = new AggregationTokenSets("(){}[]");

	@Test
	public void expression_NoAggregationOpenToken_NoContent() {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression();

		assertThat(arithmeticExpression.getLength(), is(0));
		assertThat(arithmeticExpression.toString(), is(""));
	}

	@Test
	public void expression_NoAggregationOpenToken_WithContent() {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression();
		arithmeticExpression.addCharacter('1');
		arithmeticExpression.addCharacter('+');
		arithmeticExpression.addCharacter('2');

		assertThat(arithmeticExpression.getLength(), is(3));
		assertThat(arithmeticExpression.toString(), is("1+2"));
	}

	@Test
	public void expression_WithAggregationOpenToken_NoContent() {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('('));

		assertThat(arithmeticExpression.getLength(), is(2));
		assertThat(arithmeticExpression.toString(), is("()"));
	}

	@Test
	public void expression_WithAggregationOpenToken_WithContent() {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('('));
		arithmeticExpression.addCharacter('1');
		arithmeticExpression.addCharacter('+');
		arithmeticExpression.addCharacter('2');

		assertThat(arithmeticExpression.getLength(), is(5));
		assertThat(arithmeticExpression.toString(), is("(1+2)"));
	}

	@Test
	public void expression_WithAggregationOpenToken_WithContentAndSubExpressions() {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression();
		arithmeticExpression.addCharacter('1');
		arithmeticExpression.addCharacter('+');
		arithmeticExpression.addCharacter('2');
		arithmeticExpression.addCharacter('*');

		ArithmeticExpression arithmeticSubExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('{'));
		arithmeticSubExpression.addCharacter('3');
		arithmeticSubExpression.addCharacter('/');
		arithmeticSubExpression.addCharacter('4');
		arithmeticSubExpression.addCharacter('+');
		arithmeticSubExpression.addCharacter('5');

		arithmeticExpression.addExpression(arithmeticSubExpression);

		arithmeticExpression.addCharacter('-');
		arithmeticExpression.addCharacter('6');
		arithmeticExpression.addCharacter('+');
		arithmeticSubExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('['));
		arithmeticSubExpression.addCharacter('7');
		arithmeticSubExpression.addCharacter('/');
		arithmeticSubExpression.addCharacter('8');

		arithmeticExpression.addExpression(arithmeticSubExpression);

		assertThat(arithmeticExpression.getLength(), is(19));
		assertThat(arithmeticExpression.toString(), is("1+2*{3/4+5}-6+[7/8]"));
	}
}
