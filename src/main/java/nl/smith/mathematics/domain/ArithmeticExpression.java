package nl.smith.mathematics.domain;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.smith.mathematics.domain.AggregationTokenSets.AggregationToken;

public class ArithmeticExpression extends AbstractArithmeticExpression {

	private final Logger LOGGER = LoggerFactory.getLogger(ArithmeticExpression.class);

	private StringBuilder expression = new StringBuilder();

	private List<AbstractArithmeticExpression> subExpressions = new ArrayList<>();

	public ArithmeticExpression() {
		this(null, 0);
	}

	public ArithmeticExpression(int position) {
		this(null, position);
	}

	public ArithmeticExpression(AggregationToken aggregationOpenToken) {
		this(aggregationOpenToken, 0);
	}

	public ArithmeticExpression(AggregationToken aggregationOpenToken, int position) {
		super(aggregationOpenToken, position);
	}

	public void addCharacter(char character) {
		expression.append(character);
	}

	@Override
	public void addExpression(AbstractArithmeticExpression subExpression) {
		subExpressions.add(subExpression);

		int relativePosition = subExpression.position - position;
		LOGGER.info("Added subexpression '{}' at position {} (relative position ({}).", subExpression, subExpression.position, relativePosition);
	}

	public String getExpression() {
		return expression.toString();
	}

	@Override
	public StringBuilder asStringBuilder() {
		StringBuilder result = new StringBuilder();
		if (aggregationOpenToken != null) {
			result.append(aggregationOpenToken.getTokenCharacter());
		}

		result.append(expression);

		subExpressions.forEach(subExpression -> {
			int relativePosition = subExpression.position - position;
			result.insert(relativePosition, subExpression.asStringBuilder());
		});

		if (aggregationOpenToken != null && closed) {
			result.append(aggregationOpenToken.getMatchingToken().getTokenCharacter());
		}

		return result;
	}

	@Override
	public int getLength() {
		return asStringBuilder().length();
	}

}
