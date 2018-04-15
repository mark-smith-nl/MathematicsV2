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
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression();
		Stack<ArithmeticExpression> arithmeticExpressionStack = new Stack<>();
		arithmeticExpressionStack.push(arithmeticExpression);

		for (int position = 0; position < expression.length(); position++) {
			char character = expression.charAt(position);
			AggregationToken aggregationToken = aggregationTokenSets.getAggregationTokenForCharacter(character);
			if (aggregationToken != null) {
				if (aggregationToken.isOpenToken()) {
					arithmeticExpression = new ArithmeticExpression(aggregationToken, position);
					arithmeticExpressionStack.add(arithmeticExpression);
				} else {
					try {
						arithmeticExpression.close(character);
						ArithmeticExpression subExpression = arithmeticExpressionStack.pop();
						arithmeticExpression = arithmeticExpressionStack.peek();
						arithmeticExpression.add(subExpression);
					} catch (ArithmeticExpressionCloseException e) {
						// TODO Generate proper message.
						throw new ArithmeticException(e.getMessage()); /// +
																		/// textAnnotationService.getAnnotatedText(expression,
																		/// false,
																		/// e.getPositions()));
					}
				}
			} else {
				try {
					arithmeticExpression.add(character);
				} catch (ArithmeticExpressionCloseException e) {
					e.printStackTrace();
				}
			}
		}

		if (arithmeticExpressionStack.size() == 1) {
			arithmeticExpression = arithmeticExpressionStack.pop();
			try {
				arithmeticExpression.close();
			} catch (ArithmeticExpressionCloseException e) {
				throw new RuntimeException(e.getMessage());
			}
		} else {
			arithmeticExpressionStack.remove(0);
			Set<Integer> positions = new HashSet<>();
			List<Character> missingClosingTokens = new ArrayList<>();
			while (!arithmeticExpressionStack.isEmpty()) {
				ArithmeticExpression unClosedArithmeticExpression = arithmeticExpressionStack.pop();
				missingClosingTokens.add(unClosedArithmeticExpression.getAggregationOpenToken().getMatchingToken().getTokenCharacter());
				positions.add(unClosedArithmeticExpression.getPosition());
			}

			List<String> missingClosingTokensAsString = missingClosingTokens.stream().map(e -> '\'' + e.toString() + '\'').collect(Collectors.toList());
			StringBuilder message = textAnnotationService.getAnnotatedText(expression, false, positions);
			message.append("\nMissing closing tokens: " + String.join(", ", missingClosingTokensAsString) + ".");
			throw new ArithmeticException(message.toString());
		}

		return arithmeticExpression;
	}

}
