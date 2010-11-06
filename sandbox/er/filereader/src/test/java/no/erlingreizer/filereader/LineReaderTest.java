package no.erlingreizer.filereader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import no.erlingreizer.filereader.LineReader.LineHandler;

import org.junit.Test;


public class LineReaderTest {

	private static final String FILE_UTF_8_ENCODED = "src/main/resources/fil.utf8.txt";
	private static final String FILE_ANSI_ENCODED = "src/main/resources/fil.ansi.txt";
	final Map<Integer, String> filInnhold = new HashMap<Integer, String>();
	
	@Test
	public void should_parse_three_lines(){
		
		LineReader.withLineHandler(new LineHandler() {
			@Override
			public void handleLine(int lineNumber, String s) {
				filInnhold.put(lineNumber, s);
			}
		}).withCharsetEncoding(Charset.availableCharsets().get("UTF-8")).parseFile(FILE_UTF_8_ENCODED);
		
		
		
		//skrivUtInnhold(filInnhold);
		assertInnhold(filInnhold);

	}
	
	@Test
	public void should_be_able_to_specify_charset_parse_three_lines(){
		
		LineReader.withLineHandler(new LineHandler() {
			@Override
			public void handleLine(int lineNumber, String s) {
				filInnhold.put(lineNumber, s);
			}
		}).withCharsetEncoding(Charset.availableCharsets().get("UTF-8")).parseFile(FILE_UTF_8_ENCODED);
		
		
		//skrivUtInnhold(filInnhold);
		assertInnhold(filInnhold);

	}
	
	@Test
	public void should_be_able_to_specify_charset_parse_three_lines_2(){
		
		LineReader.withLineHandler(new LineHandler() {
			@Override
			public void handleLine(int lineNumber, String s) {
				filInnhold.put(lineNumber, s);
			}
		}).withCharsetEncoding(Charset.availableCharsets().get("UTF-8")).parseFile(FILE_ANSI_ENCODED);
		assertFalse("på".equals(filInnhold.get(2)));
		
		LineReader.withLineHandler(new LineHandler() {
			@Override
			public void handleLine(int lineNumber, String s) {
				filInnhold.put(lineNumber, s);
			}
		}).withCharsetEncoding(Charset.availableCharsets().get("ISO-8859-1")).parseFile(FILE_ANSI_ENCODED);
		assertEquals("på", filInnhold.get(2));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void should_throw_exception_when_specified_charset_is_null(){
		
		LineReader.withLineHandler(new LineHandler() {
			@Override
			public void handleLine(int lineNumber, String s) {
				filInnhold.put(lineNumber, s);
			}
		}).withCharsetEncoding(null).parseFile(FILE_UTF_8_ENCODED);
		
		assertInnhold(filInnhold);

	}
	
	@Test(expected = IllegalArgumentException.class)
	public void should_throw_IllegalArgumentException_if_filename_is_null(){
		LineReader.withLineHandler(new LineHandler() {
			@Override
			public void handleLine(int lineNumber, String s) {
				filInnhold.put(lineNumber, s);
			}
		}).parseFile(null);

	}
	
	@Test(expected = IllegalArgumentException.class)
	public void should_throw_IllegalArgumentException_if_LineHandler_is_null(){
		LineReader.withLineHandler(null).parseFile(FILE_UTF_8_ENCODED);
	}

	private void assertInnhold(Map<Integer, String> filInnhold) {
		assertEquals(3, filInnhold.size());
		assertEquals("hei", filInnhold.get(1));
		assertEquals("på", filInnhold.get(2));
		assertEquals("deg", filInnhold.get(3));
	}

//	private void skrivUtInnhold(final Map<Integer, String> filInnhold) {
//		for (Integer key : filInnhold.keySet()) {
//			System.out.println(String.format("Linje nr. %s er %s", key, filInnhold.get(key)));
//		}
//	}
	
//	@Test
//	public void lol(){
//		for(String s : Charset.availableCharsets().keySet()){
//			System.out.println(s);
//		}
//	}

}
