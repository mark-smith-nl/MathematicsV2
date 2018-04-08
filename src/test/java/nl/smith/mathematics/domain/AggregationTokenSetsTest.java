package nl.smith.mathematics.domain;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.HashSet;

import org.assertj.core.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import nl.smith.mathematics.domain.AggregationTokenSets.AggregationToken;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AggregationTokenSetsTest {

	@Autowired
	private AggregationTokenSets aggregationTokens;

	@Test
	public void construct_NoTokenSet() {
		new AggregationTokenSets("");
	}

	@Test
	public void construct_OneTokenSet() {
		AggregationTokenSets aggregationTokens = new AggregationTokenSets("()");

		AggregationToken openAggregationToken = aggregationTokens.getAggregationTokenForCharacter('(');
		AggregationToken closeAggregationToken = aggregationTokens.getAggregationTokenForCharacter(')');
		assertThat(openAggregationToken.getTokenCharacter(), is('('));
		assertThat(openAggregationToken.getMatchingToken(), is(closeAggregationToken));
		assertThat(closeAggregationToken.getTokenCharacter(), is(')'));
		assertThat(closeAggregationToken.getMatchingToken(), is(openAggregationToken));
	}

	@Test
	public void construct_TwoTokenSets() {
		AggregationTokenSets aggregationTokens = new AggregationTokenSets("(){}");

		AggregationToken openAggregationToken = aggregationTokens.getAggregationTokenForCharacter('(');
		AggregationToken closeAggregationToken = aggregationTokens.getAggregationTokenForCharacter(')');
		assertThat(openAggregationToken.getTokenCharacter(), is('('));
		assertThat(openAggregationToken.getMatchingToken(), is(closeAggregationToken));
		assertThat(closeAggregationToken.getTokenCharacter(), is(')'));
		assertThat(closeAggregationToken.getMatchingToken(), is(openAggregationToken));

		openAggregationToken = aggregationTokens.getAggregationTokenForCharacter('{');
		closeAggregationToken = aggregationTokens.getAggregationTokenForCharacter('}');
		assertThat(openAggregationToken.getTokenCharacter(), is('{'));
		assertThat(openAggregationToken.getMatchingToken(), is(closeAggregationToken));
		assertThat(closeAggregationToken.getTokenCharacter(), is('}'));
		assertThat(closeAggregationToken.getMatchingToken(), is(openAggregationToken));
	}

	@Test
	public void construct_ThreeTokenSets() {
		AggregationTokenSets aggregationTokens = new AggregationTokenSets("(){}[]");

		AggregationToken openAggregationToken = aggregationTokens.getAggregationTokenForCharacter('(');
		AggregationToken closeAggregationToken = aggregationTokens.getAggregationTokenForCharacter(')');
		assertThat(openAggregationToken.getTokenCharacter(), is('('));
		assertThat(openAggregationToken.getMatchingToken(), is(closeAggregationToken));
		assertThat(closeAggregationToken.getTokenCharacter(), is(')'));
		assertThat(closeAggregationToken.getMatchingToken(), is(openAggregationToken));

		openAggregationToken = aggregationTokens.getAggregationTokenForCharacter('{');
		closeAggregationToken = aggregationTokens.getAggregationTokenForCharacter('}');
		assertThat(openAggregationToken.getTokenCharacter(), is('{'));
		assertThat(openAggregationToken.getMatchingToken(), is(closeAggregationToken));
		assertThat(closeAggregationToken.getTokenCharacter(), is('}'));
		assertThat(closeAggregationToken.getMatchingToken(), is(openAggregationToken));

		openAggregationToken = aggregationTokens.getAggregationTokenForCharacter('[');
		closeAggregationToken = aggregationTokens.getAggregationTokenForCharacter(']');
		assertThat(openAggregationToken.getTokenCharacter(), is('['));
		assertThat(openAggregationToken.getMatchingToken(), is(closeAggregationToken));
		assertThat(closeAggregationToken.getTokenCharacter(), is(']'));
		assertThat(closeAggregationToken.getMatchingToken(), is(openAggregationToken));
	}

	@Test(expected = IllegalStateException.class)
	public void construct_NullTokenSet() {
		new AggregationTokenSets(null);
	}

	@Test(expected = IllegalStateException.class)
	public void construct_IllegalTokenSet_OddNumberOfTokens() {
		new AggregationTokenSets("(");
	}

	@Test(expected = IllegalStateException.class)
	public void construct_IllegalTokenSet_DuplicateTokens() {
		new AggregationTokenSets("()()");
	}

	@Test
	public void construct_DefaultValue() {
		AggregationToken openAggregationToken = aggregationTokens.getAggregationTokenForCharacter('(');
		AggregationToken closeAggregationToken = aggregationTokens.getAggregationTokenForCharacter(')');
		assertThat(openAggregationToken.getTokenCharacter(), is('('));
		assertThat(openAggregationToken.getMatchingToken(), is(closeAggregationToken));
		assertThat(closeAggregationToken.getTokenCharacter(), is(')'));
		assertThat(closeAggregationToken.getMatchingToken(), is(openAggregationToken));

		openAggregationToken = aggregationTokens.getAggregationTokenForCharacter('{');
		assertThat(openAggregationToken, notNullValue());
		assertThat(aggregationTokens.getAllTokensAsCharacterSet(), is(new HashSet<>(Arrays.asList(new char[] { '(', '{', '[', '<', '>', ']', '}', ')' }))));
	}

}
