package nl.smith.mathematics.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AggregationToken {

	private static final Set<Character> tokenCharacters = new HashSet<>();

	public static Map<Character, AggregationToken> aggregationTokenMap = new HashMap<>();

	public enum TokenType {
		OPEN,
		CLOSE;
	}

	private final char tokenCharacter;

	private final TokenType tokenType;

	private AggregationToken matchingToken;

	private AggregationToken(char tokenCharacter, TokenType tokenType) {
		if (!tokenCharacters.add(tokenCharacter)) {
			throw new IllegalStateException(String.format("Duplicate aggregation token: '%c'.", tokenCharacter));
		}

		this.tokenCharacter = tokenCharacter;
		this.tokenType = tokenType;

		aggregationTokenMap.put(tokenCharacter, this);
	}

	public AggregationToken(char closeTokenCharacter) {
		this(closeTokenCharacter, TokenType.CLOSE);
	}

	public AggregationToken(char openTokenCharacter, AggregationToken closingMatchingToken) {
		this(openTokenCharacter, TokenType.OPEN);

		this.matchingToken = closingMatchingToken;

		closingMatchingToken.matchingToken = this;
	}

	public char getTokenCharacter() {
		return tokenCharacter;
	}

	public TokenType getTokenType() {
		return tokenType;
	}

	public AggregationToken getMatchingToken() {
		return matchingToken;
	}

}
