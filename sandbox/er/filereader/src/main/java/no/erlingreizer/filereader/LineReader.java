package no.erlingreizer.filereader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import com.google.common.base.Preconditions;
import com.google.common.io.Closeables;

/***
 * Class that will read line after line from a file. Example of use:
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
		BufferedReader bfr = null;
		try {
			bfr = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(fileName)), charset));
			String line;
			int lineCounter = 1;
			while ((line = bfr.readLine()) != null) {
				lineReaderCallback.handleLine(lineCounter, line);
				lineCounter++;
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally{
			Closeables.closeQuietly(bfr);
		}
	}

	/***
	 * Factory method that builds a linereader with the specified LineHandler
	 * @param lineHandler
	 * @return
	 */
	public static LineReader withLineHandler(
			final LineHandler lineHandler) {
		Preconditions.checkArgument(lineHandler != null, "Linehandler cannot be null");
		return new LineReader(lineHandler);
	}

	/***
	 * @param filename
	 *            The file to parse
	 */
	public void parseFile(final String filename) {
		Preconditions.checkArgument(filename != null, "Filename cannot be null");
		readFile(filename);

	}

	public LineReader withCharsetEncoding(final Charset charset) {
		Preconditions.checkArgument(charset != null, "Charset cannot be null");
		this.charset = charset;
		return this;
	}

	public interface LineHandler {
		public void handleLine(int lineNumber, final String s);
	}
}
