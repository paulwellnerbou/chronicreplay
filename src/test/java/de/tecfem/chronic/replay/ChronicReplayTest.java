package de.tecfem.chronic.replay;

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
}
