package de.wellnerbou.chronic.replay;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class ChronicReplayTest
{
	@Test(expected = Exception.class)
	public void testMainOptionParsing() throws FileNotFoundException, IOException {
		String[] args = new String[] {};
		ChronicReplay.main(args);
	}

	@Test
	public void testGetLoggingDiscriminatorVariable() {
		String testString = "http://subdomain.example.com/path";
		String stripped = ChronicReplay.getLoggingDiscriminatorVariable(testString);
		assertThat(stripped).isEqualTo("subdomain.example.com-path");
	}
}
