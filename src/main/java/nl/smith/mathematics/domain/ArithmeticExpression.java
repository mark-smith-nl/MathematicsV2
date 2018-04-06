package nl.smith.mathematics.domain;

import java.util.Stack;

public class ArithmeticExpression {

	private final AggregationToken aggregationOpenToken;

	private StringBuilder expression = new StringBuilder();

	private Stack<PositionElementEntry<ArithmeticExpression>> subExpressions = new Stack<>();

	public ArithmeticExpression() {
		this(null);
	}

	public ArithmeticExpression(AggregationToken aggregationOpenToken) {
		this.aggregationOpenToken = aggregationOpenToken;
	}

	public int getLength() {
		int length = expression.length();

		for (PositionElementEntry<ArithmeticExpression> positionSubExpressionEntry : subExpressions) {
			length += positionSubExpressionEntry.getElement().getLength();
		}

		if (aggregationOpenToken != null) {
			length += 2;
		}

		return length;
	}

	public void addCharacter(char character) {
		expression.append(character);
	}

	public void addSubExpression(ArithmeticExpression subExpression) {
		subExpressions.add(new PositionElementEntry<ArithmeticExpression>(getLength(), subExpression));
	}

	public StringBuilder toStringBuilder() {
		StringBuilder result = expression;

		subExpressions.forEach(subExpression -> {
			result.insert(subExpression.getPosition(), subExpression.getElement().toString());
		});

		if (aggregationOpenToken != null) {
			result.insert(0, aggregationOpenToken.getTokenCharacter());
			result.append(aggregationOpenToken.getMatchingToken().getTokenCharacter());
		}

		return result;
	}

	@Override
	public String toString() {
		return toStringBuilder().toString();
	}

	public AggregationToken getAggregationOpenToken() {
		return aggregationOpenToken;
	}

}
