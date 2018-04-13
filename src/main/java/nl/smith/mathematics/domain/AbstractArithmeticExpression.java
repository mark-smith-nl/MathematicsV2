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
		if (aggregationOpenToken == null) {
			throw new ArithmeticExpressionCloseException(String.format("Expression does not require the closetoken '%c'.", closeToken));
		}

		char expectedCloseToken = aggregationOpenToken.getMatchingToken().getTokenCharacter();
		if (expectedCloseToken != closeToken) {
			throw new ArithmeticExpressionCloseException(
					String.format("Expression can not be close with closetoken '%c'. Expected closetoken '%c'.", closeToken, expectedCloseToken), position);
		}

		closed = true;
	}

	public void close() throws ArithmeticExpressionCloseException {
		if (aggregationOpenToken != null) {
			char expectedCloseToken = aggregationOpenToken.getMatchingToken().getTokenCharacter();
			throw new ArithmeticExpressionCloseException(String.format("Expression requires the closetoken '%c'.", expectedCloseToken));
		}

		closed = true;
	}

}
