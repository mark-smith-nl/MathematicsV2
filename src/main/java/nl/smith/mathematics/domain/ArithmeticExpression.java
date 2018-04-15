package nl.smith.mathematics.domain;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.smith.mathematics.domain.AggregationTokenSets.AggregationToken;
import nl.smith.mathematics.exceptions.ArithmeticExpressionCloseException;
import nl.smith.mathematics.exceptions.ArithmeticExpressionMissingCloseTokenException;
import nl.smith.mathematics.exceptions.ArithmeticExpressionUnexpectedCloseException;
import nl.smith.mathematics.exceptions.ArithmeticExpressionWongCloseTokenException;

public class ArithmeticExpression {

	private final Logger LOGGER = LoggerFactory.getLogger(ArithmeticExpression.class);

	private final String VALID_CHARACTERS = "[a-zA-Z_\\s\\d\\^\\-\\*/\\+\\.]";

	private final AggregationToken aggregationOpenToken;

	private final int position;

	private boolean closed;

	private StringBuilder expression = new StringBuilder();

	private List<ArithmeticExpression> subExpressions = new ArrayList<>();

	private List<ArithmeticExpression> siblingExpressions = new ArrayList<>();

	public ArithmeticExpression() {
		this(null, 0);
	}

	public ArithmeticExpression(char character) {
		throw new IllegalArgumentException("Constructor can not be invoked using a character argument.\n"
				+ "Did you mean to use the constructor with an integer argument in which the argument specifies the absolute begin positien of the expression?");
	}

	public ArithmeticExpression(int position) {
		this(null, position);
	}

	public ArithmeticExpression(AggregationToken aggregationOpenToken) {
		this(aggregationOpenToken, 0);
	}

	public ArithmeticExpression(AggregationToken aggregationToken, int position) {
		if (aggregationToken != null && !aggregationToken.isOpenToken()) {
			throw new IllegalArgumentException("Token is not an opentoken.");
		}

		this.aggregationOpenToken = aggregationToken;
		this.position = position;

		if (aggregationOpenToken == null) {
			LOGGER.info("Created expression... at position {}.", position);
		} else {
			LOGGER.info("Created expression {}... at position {}.", aggregationOpenToken.getTokenCharacter(), position);
		}
	}

	private ArithmeticExpression(ArithmeticExpression arithmeticExpression) {
		this(null, arithmeticExpression.position + arithmeticExpression.getLength());

		expression = arithmeticExpression.expression;
		subExpressions = arithmeticExpression.subExpressions;

		closeExpression();
	}

	public void add(char character) throws ArithmeticExpressionCloseException {
		if (closed) {
			// TODO Test
			throw new IllegalStateException("Closed arithmetic expression can not be appended.");
		}

		if (character == ',') {
			addCurrentExpressionAsSibling();
		} else {
			validCharacter(character);
			expression.append(character);
		}
	}

	public void add(ArithmeticExpression subExpression) {
		if (closed) {
			// TODO Test
			throw new IllegalStateException("Closed arithmetic expression can not be appended.");
		}

		subExpressions.add(subExpression);

		int relativePosition = subExpression.position - position;
		LOGGER.info("Added subexpression '{}' at position {} (relative position ({}).", subExpression, subExpression.position, relativePosition);
	}

	public void close(char closeToken) throws ArithmeticExpressionCloseException {
		if (aggregationOpenToken == null) {
			throw new ArithmeticExpressionUnexpectedCloseException(closeToken);
		}

		char expectedCloseToken = aggregationOpenToken.getMatchingToken().getTokenCharacter();
		if (expectedCloseToken != closeToken) {
			throw new ArithmeticExpressionWongCloseTokenException(closeToken, expectedCloseToken);
		}

		closeExpression();
	}

	public void close() throws ArithmeticExpressionCloseException {
		if (aggregationOpenToken != null) {
			char expectedCloseToken = aggregationOpenToken.getMatchingToken().getTokenCharacter();
			throw new ArithmeticExpressionMissingCloseTokenException(expectedCloseToken);
		}

		closeExpression();
	}

	private void closeExpression() {
		if (!siblingExpressions.isEmpty()) {
			addCurrentExpressionAsSibling();
		}

		closed = true;
	}

	public AggregationToken getAggregationOpenToken() {
		return aggregationOpenToken;
	}

	public int getPosition() {
		return position;
	}

	public String getExpression() {
		return expression.toString();
	}

	public StringBuilder asStringBuilder() {
		StringBuilder result = new StringBuilder();
		if (aggregationOpenToken != null) {
			result.append(aggregationOpenToken.getTokenCharacter());
		}

		if (!siblingExpressions.isEmpty()) {

			result.append(siblingExpressions.get(0).asStringBuilder());
			for (int i = 1; i < siblingExpressions.size(); i++) {
				result.append(String.valueOf(',') + siblingExpressions.get(i).asStringBuilder());
			}

			if (!closed) {
				result.append(String.valueOf(','));
			}
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

	public int getLength() {
		return asStringBuilder().length();
	}

	@Override
	public String toString() {
		return asStringBuilder().toString() + (closed ? "" : "...");
	}

	private void validCharacter(char character) throws ArithmeticExpressionCloseException {
		// TODO Test
		if (!String.valueOf(character).matches(VALID_CHARACTERS)) {
			throw new ArithmeticExpressionCloseException(String.format("Illegal character '%c'.\n Accepted expression characters are %s.", character, VALID_CHARACTERS));
		}

	}

	private void addCurrentExpressionAsSibling() {
		siblingExpressions.add(new ArithmeticExpression(this));

		expression = new StringBuilder();
		subExpressions = new ArrayList<>();
	}

}
