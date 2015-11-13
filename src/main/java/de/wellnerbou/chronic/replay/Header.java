package de.wellnerbou.chronic.replay;

/**
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public class Header {
	private final String name;
	private final String value;

	public Header(final String name, final String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
}
