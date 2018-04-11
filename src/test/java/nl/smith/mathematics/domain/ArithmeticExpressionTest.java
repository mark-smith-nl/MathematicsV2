package nl.smith.mathematics.domain;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ArithmeticExpressionTest {

	private static AggregationTokenSets aggregationTokenSets = new AggregationTokenSets("(){}[]");

	@Test
	public void expression_NoAggregationOpenToken_NoContent() {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression();

		assertThat(arithmeticExpression.toString(), is("..."));

		arithmeticExpression.close();

		assertThat(arithmeticExpression.toString(), is(""));
	}

	@Test
	public void expression_NoAggregationOpenToken_WithContent() {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression();
		arithmeticExpression.addCharacter('1');
		arithmeticExpression.addCharacter('+');
		arithmeticExpression.addCharacter('2');

		assertThat(arithmeticExpression.toString(), is("1+2..."));

		arithmeticExpression.close();

		assertThat(arithmeticExpression.toString(), is("1+2"));
	}

	@Test
	public void expression_WithAggregationOpenToken_NoContent() {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('('));

		assertThat(arithmeticExpression.toString(), is("(..."));

		arithmeticExpression.closeWithToken(')');

		assertThat(arithmeticExpression.toString(), is("()"));
	}

	@Test
	public void expression_WithAggregationOpenToken_WithContent() {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression(aggregationTokenSets.getAggregationTokenForCharacter('('));
		arithmeticExpression.addCharacter('1');
		arithmeticExpression.addCharacter('+');
		arithmeticExpression.addCharacter('2');

		assertThat(arithmeticExpression.toString(), is("(1+2..."));

		arithmeticExpression.closeWithToken(')');

		assertThat(arithmeticExpression.toString(), is("(1+2)"));
	}

	@Test
	public void expression_WithAggregationOpenToken_WithContentAndSubExpressions() {
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
