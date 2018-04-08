package nl.smith.mathematics.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AggregationTokenSets {

	private final Map<Character, AggregationToken> aggregationTokenMap = new HashMap<>();

	public enum TokenType {
		OPEN,
		CLOSE;
	}

	@Autowired
	public AggregationTokenSets(@Value("${allTokensAsString:(){}[]<>}") String allTokensAsString) {
		initialize(allTokensAsString);
	}

	private void initialize(String allTokensAsString) {
		if (allTokensAsString == null || allTokensAsString.length() % 2 == 1) {
			throw new IllegalStateException("The number of aggregation tokencharacters should be even");
		}

		for (int index = 0; index < allTokensAsString.length(); index += 2) {
			new AggregationToken(allTokensAsString.charAt(index), new AggregationToken(allTokensAsString.charAt(index + 1)));
		}
	}

	public AggregationToken getAggregationTokenForCharacter(char character) {
		return aggregationTokenMap.get(character);
	}

	public Set<Character> getAllTokensAsCharacterSet() {
		return new HashSet<Character>(aggregationTokenMap.keySet());
	}

	public class AggregationToken {

		private final char tokenCharacter;

		private final TokenType tokenType;

		private AggregationToken matchingToken;

		private AggregationToken(char tokenCharacter, TokenType tokenType) {
			this.tokenCharacter = tokenCharacter;
			this.tokenType = tokenType;

			if (aggregationTokenMap.put(tokenCharacter, this) != null) {
				throw new IllegalStateException(String.format("Duplicate aggregation token: '%c'.", tokenCharacter));
			}
		}

		private AggregationToken(char closeTokenCharacter) {
			this(closeTokenCharacter, TokenType.CLOSE);
		}

		private AggregationToken(char openTokenCharacter, AggregationToken closingMatchingToken) {
			this(openTokenCharacter, TokenType.OPEN);

			this.matchingToken = closingMatchingToken;

			closingMatchingToken.matchingToken = this;
		}

		public char getTokenCharacter() {
			return tokenCharacter;
		}

		public boolean isOpenToken() {
			return tokenType == TokenType.OPEN;
		}

		public AggregationToken getMatchingToken() {
			return matchingToken;
		}
	}
}
