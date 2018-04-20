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
import nl.smith.mathematics.exceptions.ArithmeticExpressionIllegalCharacterException;
import nl.smith.mathematics.exceptions.ArithmeticExpressionUnexpectedCloseException;
import nl.smith.mathematics.exceptions.ArithmeticExpressionWongCloseTokenException;

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

	public ArithmeticExpression buildArithmeticExpression(@NotBlank String rawEexpression) {
		ArithmeticExpression arithmeticExpression = new ArithmeticExpression();
		Stack<ArithmeticExpression> arithmeticExpressionStack = new Stack<>();
		arithmeticExpressionStack.push(arithmeticExpression);

		for (int position = 0; position < rawEexpression.length(); position++) {
			char character = rawEexpression.charAt(position);
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
						if (e instanceof ArithmeticExpressionWongCloseTokenException) {
							StringBuilder message = new StringBuilder(e.getMessage());
							message.append(textAnnotationService.getAnnotatedText(rawEexpression, false, arithmeticExpression.getPosition(), position));
							throw new ArithmeticException(message.toString());
						} else if (e instanceof ArithmeticExpressionUnexpectedCloseException) {
							StringBuilder message = new StringBuilder(e.getMessage());
							message.append(textAnnotationService.getAnnotatedText(rawEexpression, false, position));
							throw new ArithmeticException(message.toString());
						}

						throw new IllegalStateException(e.getMessage());
					}
				}
			} else {
				try {
					arithmeticExpression.add(character);
				} catch (ArithmeticExpressionIllegalCharacterException e) {
					throw new ArithmeticException(e.getMessage());
				}
			}
		}

		if (arithmeticExpressionStack.size() == 1) {
			arithmeticExpression = arithmeticExpressionStack.pop();
			try {
				arithmeticExpression.close();
			} catch (ArithmeticExpressionCloseException e) {
				throw new IllegalStateException(e.getMessage());
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
			StringBuilder message = new StringBuilder("Missing closing tokens: " + String.join(", ", missingClosingTokensAsString) + ".");
			message.append(textAnnotationService.getAnnotatedText(rawEexpression, false, positions));
			throw new ArithmeticException(message.toString());
		}

		return arithmeticExpression;
	}

}
