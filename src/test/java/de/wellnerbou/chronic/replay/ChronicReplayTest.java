package de.wellnerbou.chronic.replay;

import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

public class ChronicReplayTest
{
	public void testMainOptionParsing() throws IOException {
		String[] args = new String[] {};
		ChronicReplay.main(args);
	}

	@Test
	public void testGetLoggingDiscriminatorVariable() {
		String testString = "http://subdomain.example.com/path";
		String stripped = ChronicReplay.getLoggingDiscriminatorVariable(testString);
		assertThat(stripped).startsWith("subdomain.example.com-path");
	}
}
