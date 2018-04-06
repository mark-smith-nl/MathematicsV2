package nl.smith.mathematics.domain;

public class PositionElementEntry<T> {

	private final int position;

	private final T element;

	public PositionElementEntry(int position, T element) {
		super();
		this.position = position;
		this.element = element;
	}

	public int getPosition() {
		return position;
	}

	public T getElement() {
		return element;
	}

}
