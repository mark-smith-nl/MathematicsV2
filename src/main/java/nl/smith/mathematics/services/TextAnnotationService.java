package nl.smith.mathematics.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import nl.smith.mathematics.annotations.ValidLine;

@Validated
@Service
public class TextAnnotationService {

	private char endOfLineCharacter = (char) 182;

	public void setEndOfLineCharacter(char endOfLineCharacter) {
		this.endOfLineCharacter = endOfLineCharacter;
	}

	public StringBuilder getAnnotatedText(@NotNull String text, boolean showLineNumbers, int... position) {
		Set<Integer> positions = new HashSet<>();
		for (int p : position) {
			positions.add(p);
		}

		return getAnnotatedText(text, showLineNumbers, positions);
	}

	public StringBuilder getAnnotatedText(@NotNull String text, boolean showLineNumbers, char... character) {
		Set<Character> characterSet = new HashSet<>();
		for (int i = 0; i < character.length; i++) {
			characterSet.add(character[i]);
		}

		Set<Integer> positions = new HashSet<>();
		for (int i = 0; i < text.length(); i++) {
			if (characterSet.contains(text.charAt(i))) {
				positions.add(i);
			}
		}

		return getAnnotatedText(text, showLineNumbers, positions);
	}

	public StringBuilder getAnnotatedText(@NotNull String text, boolean showLineNumbers, @NotNull @ValidLine String subString) {
		Set<Integer> positions = new HashSet<>();
		Pattern pattern = Pattern.compile("\\Q" + subString + "\\E");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			for (int i = start; i < end; i++) {
				positions.add(i);
			}
		}

		return getAnnotatedText(text, showLineNumbers, positions);
	}

	public StringBuilder getAnnotatedText(@NotNull String text, boolean showLineNumbers, @NotEmpty Set<Integer> positions) {
		if (Collections.min(positions) < 0 || Collections.max(positions) >= text.length()) {
			throw new IllegalArgumentException("Illegal positions");
		}

		List<LineWithPositionsToAnnotate> linesWithPositionsToAnnotate = getLinesWithPositionsToAnnotate(text, positions);

		StringBuilder annotatedText = new StringBuilder();

		for (int lineNumber = 0; lineNumber < linesWithPositionsToAnnotate.size(); lineNumber++) {
			LineWithPositionsToAnnotate lineWithPositionsToAnnotate = linesWithPositionsToAnnotate.get(lineNumber);
			String line = lineWithPositionsToAnnotate.getLine();
			annotatedText.append(String.format(getFormatString(showLineNumbers, linesWithPositionsToAnnotate.size()), line, lineNumber));

			Set<Integer> relativePositions = lineWithPositionsToAnnotate.getRelativePositions();
			if (!relativePositions.isEmpty()) {
				char[] annotation = line.replaceAll("\\S", " ").toCharArray();
				relativePositions.forEach(relativePosition -> annotation[relativePosition] = annotation[relativePosition] == '\t' ? '*' : '^');

				annotatedText.append(
						String.format(getFormatString(showLineNumbers, linesWithPositionsToAnnotate.size()), String.valueOf(annotation).replaceAll("\\*", "\\^\t"), lineNumber));
			}
		}

		return annotatedText;
	}

	private List<LineWithPositionsToAnnotate> getLinesWithPositionsToAnnotate(String text, Collection<Integer> positions) {
		List<LineWithPositionsToAnnotate> linesWithPositionsToAnnotate = new ArrayList<LineWithPositionsToAnnotate>();

		List<String> lines = new ArrayList<>();
		for (String line : text.split("\\n")) {
			lines.add(line + endOfLineCharacter);
		}

		List<Set<Integer>> positionsInLine = new ArrayList<>();
		lines.forEach(line -> positionsInLine.add(new HashSet<Integer>()));

		if (!CollectionUtils.isEmpty(positions)) {
			positions.forEach(position -> {
				int relPosition = position;
				for (int i = 0; i < lines.size(); i++) {
					if (relPosition < lines.get(i).length()) {
						positionsInLine.get(i).add(relPosition);
						break;
					}
					relPosition -= lines.get(i).length();
				}
			});
		}

		for (int i = 0; i < lines.size(); i++) {
			linesWithPositionsToAnnotate.add(new LineWithPositionsToAnnotate(lines.get(i), positionsInLine.get(i)));
		}

		return linesWithPositionsToAnnotate;
	}

	private static String getFormatString(boolean showLineNumbers, int numberOfLines) {
		if (showLineNumbers) {
			int maxDigits = String.valueOf(numberOfLines).length();
			return "\n%2$" + maxDigits + "d - %1$s";
		}

		return "\n%s";

	}

	private static class LineWithPositionsToAnnotate {

		private final String line;

		private final Set<Integer> relativePositions;

		public LineWithPositionsToAnnotate(String line, Set<Integer> relativePositions) {
			super();
			this.line = line;
			this.relativePositions = relativePositions;
		}

		public String getLine() {
			return line;
		}

		public Set<Integer> getRelativePositions() {
			return relativePositions;
		}

	}
}
