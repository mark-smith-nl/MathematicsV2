package nl.smith.mathematics.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.smith.mathematics.domain.AggregationTokenSets.AggregationToken;
import nl.smith.mathematics.exceptions.ArithmeticExpressionCloseException;

public abstract class AbstractArithmeticExpression {

	private final Logger LOGGER = LoggerFactory.getLogger(ArithmeticExpression.class);

	protected final AggregationToken aggregationOpenToken;

	protected final int position;

	protected boolean closed;

	public AbstractArithmeticExpression(AggregationToken aggregationOpenToken, int position) {
		this.aggregationOpenToken = aggregationOpenToken;
		this.position = position;

		if (aggregationOpenToken == null) {
			LOGGER.info("Created expression... at position {}.", position);
		} else {
			LOGGER.info("Created expression {}... at position {}.", aggregationOpenToken.getTokenCharacter(), position);
		}

	}

	public abstract int getLength();

	public abstract void addExpression(AbstractArithmeticExpression subExpression);

	public abstract StringBuilder asStringBuilder();

	@Override
	public String toString() {
		return asStringBuilder().toString() + (closed ? "" : "...");
	}

	public AggregationTokenSets.AggregationToken getAggregationOpenToken() {
		return aggregationOpenToken;
	}

	public int getPosition() {
		return position;
	}

	public void closeWithToken(char closeToken) throws ArithmeticExpressionCloseException {
		if (aggregationOpenToken != null && aggregationOpenToken.getMatchingToken().getTokenCharacter() != closeToken) {
			throw new ArithmeticExpressionCloseException(1, 2, "ssss");
		}

		closed = true;
	}

	public void close() {
		if (aggregationOpenToken != null) {
			throw new ArithmeticException("Isse nie goe 2");
		}

		closed = true;
	}

}
