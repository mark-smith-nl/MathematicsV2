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
		Stack<PositionElementEntry<ArithmeticExpression>> arithmeticExpressionStack = new Stack<>();

		ArithmeticExpression rootArithmeticExpression = new ArithmeticExpression();
		ArithmeticExpression arithmeticExpression = rootArithmeticExpression;

		for (int position = 0; position < expression.length(); position++) {
			char character = expression.charAt(position);
			AggregationToken aggregationToken = AggregationToken.aggregationTokenMap.get(character);
			if (aggregationToken != null) {

				if (aggregationToken.getTokenType() == AggregationToken.TokenType.OPEN) {
					// Open subexpression
					arithmeticExpression = new ArithmeticExpression(aggregationToken);
					arithmeticExpressionStack.add(new PositionElementEntry<ArithmeticExpression>(position, arithmeticExpression));
				} else {
					// Close subexpression.
					if (arithmeticExpressionStack.isEmpty()) {
						char expectedOpenToken = aggregationToken.getMatchingToken().getTokenCharacter();
						StringBuilder message = textAnnotationService.getAnnotatedText(expression, false, position);
						message.append(String.format("\nMissing open token '%c' for closing token '%c'.", expectedOpenToken, character));
						throw new ArithmeticException(message.toString());
					} else {
						PositionElementEntry<ArithmeticExpression> positionElementEntry = arithmeticExpressionStack.pop();
						arithmeticExpression = positionElementEntry.getElement();
						char openToken = arithmeticExpression.getAggregationOpenToken().getTokenCharacter();
						char expectedCloseToken = arithmeticExpression.getAggregationOpenToken().getMatchingToken().getTokenCharacter();
						int positionOpenToken = positionElementEntry.getPosition();
						if (expectedCloseToken != character) {
							StringBuilder message = textAnnotationService.getAnnotatedText(expression, false, positionOpenToken, position);
							message.append(String.format("\nWrong close token '%c' for open token '%c'. Expected '%c'.", character, openToken, expectedCloseToken));
							throw new ArithmeticException(message.toString());
						} else {
							if (arithmeticExpressionStack.isEmpty()) {
								arithmeticExpression = rootArithmeticExpression;
							} else {
								arithmeticExpression = arithmeticExpressionStack.peek().getElement();
							}
						}
					}
				}
			} else {
				arithmeticExpression.addCharacter(character);
			}
		}

		Set<Integer> positions = new HashSet<>();
		List<Character> missingClosingTokens = new ArrayList<>();
		while (!arithmeticExpressionStack.isEmpty()) {
			PositionElementEntry<ArithmeticExpression> positionElementEntry = arithmeticExpressionStack.pop();
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

}
