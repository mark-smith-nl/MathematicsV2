package nl.smith.mathematics.domain;

import nl.smith.mathematics.domain.AggregationTokenSets.AggregationToken;

public abstract class AbstractArithmeticExpression {

	protected final AggregationTokenSets.AggregationToken aggregationOpenToken;

	public AbstractArithmeticExpression(AggregationToken aggregationOpenToken) {
		this.aggregationOpenToken = aggregationOpenToken;
	}

	public abstract int getLength();

	public abstract void addExpression(AbstractArithmeticExpression subExpression);

	public abstract StringBuilder asStringBuilder();

	@Override
	public String toString() {
		return asStringBuilder().toString();
	}

	public AggregationTokenSets.AggregationToken getAggregationOpenToken() {
		return aggregationOpenToken;
	}

}
