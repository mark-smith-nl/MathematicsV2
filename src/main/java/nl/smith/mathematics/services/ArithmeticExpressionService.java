package nl.smith.mathematics.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import nl.smith.mathematics.domain.AggregationTokenSets;
import nl.smith.mathematics.domain.AggregationTokenSets.AggregationToken;
import nl.smith.mathematics.domain.ArithmeticExpression;
import nl.smith.mathematics.exceptions.ArithmeticExpressionCloseException;

@Validated
@Service
public class ArithmeticExpressionService {

	@Autowired
	private TextAnnotationService textAnnotationService;

	@Autowired
	private AggregationTokenSets aggregationTokenSets;

	@PostConstruct
	public void getAggregationTokenMap() {
		textAnnotationService.setEndOfLineCharacter(' ');
	}

	public ArithmeticExpression buildArithmeticExpression(@NotBlank String expression) {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression(0);
		Stack<ArithmeticExpression> arithmeticSubExpressionStack = new Stack<>();
		// arithmeticSubExpressionStack.push(arithmeticExpression);

		for (int position = 0; position < expression.length(); position++) {
			char character = expression.charAt(position);
			AggregationToken aggregationToken = aggregationTokenSets.getAggregationTokenForCharacter(character);
			if (aggregationToken != null) {
				if (aggregationToken.isOpenToken()) {
					// Begin subexpression.
					ArithmeticExpression subExpression = new ArithmeticExpression(aggregationToken, position);
					arithmeticExpression.addExpression(subExpression);
					arithmeticSubExpressionStack.add(arithmeticExpression);
					arithmeticExpression = subExpression;

				} else {
					// Close token encountered. Validate expression is correctly
					// closed.
					try {
						arithmeticExpression.closeWithToken(character);
					} catch (ArithmeticExpressionCloseException e) {
						throw new ArithmeticException(e.getMessage() + textAnnotationService.getAnnotatedText(expression, false, e.getPositions()));
					}

					// Retrieve parent expression.
					arithmeticExpression = arithmeticSubExpressionStack.peek();
				}
			} else {
				arithmeticExpression.addCharacter(character);
			}
		}

		Set<Integer> positions = new HashSet<>();
		List<Character> missingClosingTokens = new ArrayList<>();
		while (!arithmeticSubExpressionStack.isEmpty()) {
			ArithmeticExpression unClosedArithmeticExpression = arithmeticSubExpressionStack.pop();
			missingClosingTokens.add(unClosedArithmeticExpression.getAggregationOpenToken().getMatchingToken().getTokenCharacter());
			positions.add(unClosedArithmeticExpression.getPosition());
		}

		if (!positions.isEmpty()) {
			List<String> missingClosingTokensAsString = missingClosingTokens.stream().map(e -> '\'' + e.toString() + '\'').collect(Collectors.toList());
			StringBuilder message = textAnnotationService.getAnnotatedText(expression, false, positions);
			message.append("\nMissing closing tokens: " + String.join(", ", missingClosingTokensAsString) + ".");
			throw new ArithmeticException(message.toString());
		}

		arithmeticExpression.close();

		return arithmeticExpression;
	}

}
