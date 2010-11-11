package no.erlingreizer.filereader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.nio.charset.Charset;
import java.util.Map;

import no.erlingreizer.filereader.LineReader.LineHandler;

import org.junit.Test;

import com.google.common.collect.Maps;

public class LineReaderTest {

	private static final String LINE_NUMBER_1_CONTENT = "hei";
	private static final String LINE_NUMBER_2_CONTENT = "på";
	private static final String LINE_NUMBER_3_CONTENT = "deg";
	private static final int LINE_NUMBER_1 = 1;
	private static final int LINE_NUMBER_2 = 2;
	private static final int LINE_NUMBER_3 = 3;
	private static final String UTF_8_ENCODING = "UTF-8";
	private static final String ISO_8859_1_ENCODING = "ISO-8859-1";
	private static final String FILE_UTF_8_ENCODED = "src/main/resources/fil.utf8.txt";
	private static final String FILE_ANSI_ENCODED = "src/main/resources/fil.ansi.txt";
	final Map<Integer, String> fileContent = Maps.newHashMap();

	@Test
	public void should_parse_three_lines() {

		LineReader.withLineHandler(new LineHandler() {
			@Override
			public void handleLine(int lineNumber, String s) {
				fileContent.put(lineNumber, s);
			}
		}).withCharsetEncoding(Charset.availableCharsets().get(UTF_8_ENCODING))
				.parseFile(FILE_UTF_8_ENCODED);

		// skrivUtInnhold();
		assertContent();

	}

	@Test
	public void should_be_able_to_specify_charset_parse_three_lines() {

		LineReader.withLineHandler(new LineHandler() {
			@Override
			public void handleLine(int lineNumber, String s) {
				fileContent.put(lineNumber, s);
			}
		}).withCharsetEncoding(Charset.availableCharsets().get(UTF_8_ENCODING))
				.parseFile(FILE_UTF_8_ENCODED);

		// skrivUtInnhold();
		assertContent();

	}

	@Test
	public void should_be_able_to_specify_charset_parse_three_lines_2() {

		LineReader.withLineHandler(new LineHandler() {
			@Override
			public void handleLine(int lineNumber, String s) {
				fileContent.put(lineNumber, s);
			}
		}).withCharsetEncoding(Charset.availableCharsets().get(UTF_8_ENCODING))
				.parseFile(FILE_ANSI_ENCODED);
		assertFalse(LINE_NUMBER_2_CONTENT.equals(fileContent.get(LINE_NUMBER_2)));

		LineReader
				.withLineHandler(new LineHandler() {
					@Override
					public void handleLine(int lineNumber, String s) {
						fileContent.put(lineNumber, s);
					}
				})
				.withCharsetEncoding(
						Charset.availableCharsets().get(ISO_8859_1_ENCODING))
				.parseFile(FILE_ANSI_ENCODED);
		assertEquals(LINE_NUMBER_2_CONTENT, fileContent.get(LINE_NUMBER_2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_exception_when_specified_charset_is_null() {

		LineReader.withLineHandler(new LineHandler() {
			@Override
			public void handleLine(int lineNumber, String s) {
				fileContent.put(lineNumber, s);
			}
		}).withCharsetEncoding(null).parseFile(FILE_UTF_8_ENCODED);

		fail("Should never come her!");

	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_IllegalArgumentException_if_filename_is_null() {
		LineReader.withLineHandler(new LineHandler() {
			@Override
			public void handleLine(int lineNumber, String s) {
				fileContent.put(lineNumber, s);
			}
		}).parseFile(null);
		fail("Should never come her!");
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_IllegalArgumentException_if_LineHandler_is_null() {
		LineReader.withLineHandler(null).parseFile(FILE_UTF_8_ENCODED);
		fail("Should never come her!");
	}

	private void assertContent() {
		assertEquals(3, fileContent.size());
		assertEquals(LINE_NUMBER_1_CONTENT, fileContent.get(LINE_NUMBER_1));
		assertEquals(LINE_NUMBER_2_CONTENT, fileContent.get(LINE_NUMBER_2));
		assertEquals(LINE_NUMBER_3_CONTENT, fileContent.get(LINE_NUMBER_3));
	}

	// private void skrivUtInnhold() {
	// for (Integer key : fileContent.keySet()) {
	// System.out.println(String.format("Linje nr. %s er %s", key,
	// fileContent.get(key)));
	// }
	// }

	// @Test
	// public void lol(){
	// for(String s : Charset.availableCharsets().keySet()){
	// System.out.println(s);
	// }
	// }

}
