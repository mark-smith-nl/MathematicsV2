package nl.smith.mathematics.domain;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.smith.mathematics.domain.AggregationTokenSets.AggregationToken;

public class ArithmeticExpression extends AbstractArithmeticExpression {

	private final Logger LOGGER = LoggerFactory.getLogger(ArithmeticExpression.class);

	private StringBuilder expression = new StringBuilder();

	private List<PositionElementEntry<AbstractArithmeticExpression>> subExpressions = new ArrayList<>();

	private int insertPosition = 0;

	public ArithmeticExpression() {
		this(null);
	}

	public ArithmeticExpression(AggregationToken aggregationOpenToken) {
		super(aggregationOpenToken);

		insertPosition = aggregationOpenToken == null ? 0 : 1;
	}

	@Override
	public int getLength() {
		return insertPosition + (aggregationOpenToken == null ? 0 : 1);
	}

	public void addCharacter(char character) {
		expression.append(character);
		insertPosition++;
	}

	@Override
	public void addExpression(AbstractArithmeticExpression subExpression) {
		subExpressions.add(new PositionElementEntry<AbstractArithmeticExpression>(insertPosition, subExpression));
		LOGGER.info("Add subexpression at position: {}", insertPosition);
		insertPosition += subExpression.getLength();
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
			result.insert(subExpression.getPosition(), subExpression.getElement().asStringBuilder());
		});

		if (aggregationOpenToken != null) {
			result.append(aggregationOpenToken.getMatchingToken().getTokenCharacter());
		}

		return result;
	}

}
