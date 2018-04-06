package nl.smith.mathematics.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import nl.smith.mathematics.domain.AggregationToken;
import nl.smith.mathematics.domain.ArithmeticExpression;
import nl.smith.mathematics.domain.PositionElementEntry;

@Validated
@Service
public class ArithmeticExpressionService {

	@Autowired
	private TextAnnotationService textAnnotationService;

	@PostConstruct
	public void getAggregationTokenMap() {
		new AggregationToken('{', new AggregationToken('}'));
		new AggregationToken('[', new AggregationToken(']'));
		new AggregationToken('(', new AggregationToken(')'));

		textAnnotationService.setEndOfLineCharacter(' ');
	}

	public ArithmeticExpression buildArithmeticExpression(@NotEmpty String expression) {
		Stack<PositionElementEntry<ArithmeticExpression>> arithmeticSubExpressionStack = new Stack<>();

		ArithmeticExpression rootArithmeticExpression = new ArithmeticExpression();
		ArithmeticExpression arithmeticExpression = rootArithmeticExpression;

		for (int position = 0; position < expression.length(); position++) {
			char character = expression.charAt(position);
			AggregationToken aggregationToken = AggregationToken.aggregationTokenMap.get(character);
			if (aggregationToken != null) {
				if (aggregationToken.getTokenType() == AggregationToken.TokenType.OPEN) {
					// Begin subexpression.
					arithmeticExpression = new ArithmeticExpression(aggregationToken);
					arithmeticSubExpressionStack.add(new PositionElementEntry<ArithmeticExpression>(position, arithmeticExpression));
				} else {
					// Close token encountered. Validate expression is correctly
					// closed.
					validateCorrectClosing(aggregationToken, arithmeticSubExpressionStack, position, expression);

					ArithmeticExpression subExpression = arithmeticExpression;

					// Retrieve parent expression.
					if (arithmeticSubExpressionStack.isEmpty()) {
						arithmeticExpression = rootArithmeticExpression;
					} else {
						arithmeticExpression = arithmeticSubExpressionStack.peek().getElement();
					}

					arithmeticExpression.addSubExpression(subExpression);
				}
			} else {
				arithmeticExpression.addCharacter(character);
			}
		}

		Set<Integer> positions = new HashSet<>();
		List<Character> missingClosingTokens = new ArrayList<>();
		while (!arithmeticSubExpressionStack.isEmpty()) {
			PositionElementEntry<ArithmeticExpression> positionElementEntry = arithmeticSubExpressionStack.pop();
			missingClosingTokens.add(positionElementEntry.getElement().getAggregationOpenToken().getMatchingToken().getTokenCharacter());
			positions.add(positionElementEntry.getPosition());
		}

		if (!positions.isEmpty()) {
			List<String> missingClosingTokensAsString = missingClosingTokens.stream().map(e -> '\'' + e.toString() + '\'').collect(Collectors.toList());
			StringBuilder message = textAnnotationService.getAnnotatedText(expression, false, positions);
			message.append("\nMissing closing tokens: " + String.join(", ", missingClosingTokensAsString));
			throw new ArithmeticException(message.toString());
		}

		return rootArithmeticExpression;
	}

	private void validateCorrectClosing(AggregationToken aggregationToken, Stack<PositionElementEntry<ArithmeticExpression>> arithmeticSubExpressionStack, int position,
			@NotEmpty String expression) {

		char actualCloseToken = aggregationToken.getTokenCharacter();

		if (arithmeticSubExpressionStack.isEmpty()) {
			char expectedOpenToken = aggregationToken.getMatchingToken().getTokenCharacter();
			StringBuilder message = textAnnotationService.getAnnotatedText(expression, false, position);
			message.append(String.format("\nMissing open token '%c' for closing token '%c'.", expectedOpenToken, actualCloseToken));
			throw new ArithmeticException(message.toString());
		} else {
			PositionElementEntry<ArithmeticExpression> positionElementEntry = arithmeticSubExpressionStack.pop();
			ArithmeticExpression arithmeticExpression = positionElementEntry.getElement();
			char openToken = arithmeticExpression.getAggregationOpenToken().getTokenCharacter();
			char expectedCloseToken = arithmeticExpression.getAggregationOpenToken().getMatchingToken().getTokenCharacter();
			int positionOpenToken = positionElementEntry.getPosition();
			if (expectedCloseToken != aggregationToken.getTokenCharacter()) {
				StringBuilder message = textAnnotationService.getAnnotatedText(expression, false, positionOpenToken, position);
				message.append(String.format("\nWrong close token '%c' for open token '%c'. Expected '%c'.", actualCloseToken, openToken, expectedCloseToken));
				throw new ArithmeticException(message.toString());
			}

		}
	}
}
