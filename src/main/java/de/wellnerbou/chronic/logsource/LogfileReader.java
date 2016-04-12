package de.wellnerbou.chronic.logsource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

/**
 * @author Paul Wellner Bou <paul@wellnerbou.de>
 */
public class LogfileReader extends BufferedReader implements LogSourceReader<CharSequence> {

	public LogfileReader(final Reader in) {
		super(in);
	}

    @Override
    public void close() throws IOException {
		super.close();
    }

	@Override
	public CharSequence next() {
		try {
			return super.readLine();
		} catch (IOException e) {
			return null;
		}
	}
}
