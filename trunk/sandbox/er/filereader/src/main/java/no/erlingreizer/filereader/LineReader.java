package no.erlingreizer.filereader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/***
 * Class that will read line from line from a file. Example of use:
 * 
 * <pre>
 * <code>
 * 	LineReader.withLineHandler(new LineHandler() {
 * 	&#064;Override
 * 	public void handleLine(int lineNumber, String s) {
 * 		filInnhold.put(lineNumber, s);
 * 	}
 * }).parse("c:\\file.txt");
 *  
 * </code>
 * </pre>
 * 
 */
public class LineReader {
	final LineHandler lineReaderCallback;
	Charset charset = Charset.defaultCharset();

	private LineReader(final LineHandler lineReaderCallback) {
		this.lineReaderCallback = lineReaderCallback;
	}

	private void readFile(String fileName) {
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(fileName)), charset));
			String line;
			int lineCounter = 1;
			while ((line = bf.readLine()) != null) {
				lineReaderCallback.handleLine(lineCounter, line);
				lineCounter++;
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/***
	 * Factory method that builds a linereader with the specified LineHandler
	 * @param lineHandler
	 * @return
	 */
	public static LineReader withLineHandler(
			final LineHandler lineHandler) {
		assertNotNull("LineReaderHandler", lineHandler);
		return new LineReader(lineHandler);
	}

	/***
	 * @param filnavn
	 *            The file to parse
	 */
	public void parseFile(final String filnavn) {
		assertNotNull("Filnavn", filnavn);
		readFile(filnavn);

	}

	public LineReader withCharsetEncoding(final Charset charset) {
		assertNotNull("Charset", charset);
		this.charset = charset;
		return this;
	}

	private static <T> void assertNotNull(final String field, final T object) {
		if (object == null) {
			throw new IllegalArgumentException(String.format(
					"%s cannot be null!", field));
		}
	}

	public interface LineHandler {
		public void handleLine(int lineNumber, final String s);
	}
}
