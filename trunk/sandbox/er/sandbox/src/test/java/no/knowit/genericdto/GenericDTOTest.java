package no.knowit.genericdto;

import static org.junit.Assert.*;

import org.junit.Test;

public class GenericDTOTest {
	private static final String FIRST_STRING_PROPERTY = "firstproperty";
	private static final String FIRST_INTEGER_PROPERTY = "secondproperty";
	private static final String FIRST_LONG_PROPERTY = "thirdproperty";

	@Test
	public void should_get_string_from_dto() {
		SimpleGDTO dt = new SimpleGDTO().add(FIRST_STRING_PROPERTY,
				"abba");
		assertEquals(dt.get(FIRST_STRING_PROPERTY), "abba");
	}

	@Test
	public void should_get_integer_from_dto() {
		SimpleGDTO dt = new SimpleGDTO()
				.add(FIRST_INTEGER_PROPERTY, 1);
		assertEquals(dt.get(FIRST_INTEGER_PROPERTY), 1);
	}
	
	@Test
	public void should_get_long_from_dto() {
		SimpleGDTO dt = new SimpleGDTO()
				.add(FIRST_LONG_PROPERTY, 2L);
		assertEquals(dt.get(FIRST_LONG_PROPERTY), 2L);
	}
	
	@Test
	public void should_get_all_three_types_from_dto() {
		SimpleGDTO dt = new SimpleGDTO()
				.add(FIRST_STRING_PROPERTY, "abba")//
				.add(FIRST_INTEGER_PROPERTY, 1)//
				.add(FIRST_LONG_PROPERTY, 2L);
		assertEquals(dt.get(FIRST_STRING_PROPERTY),"abba");
		assertEquals(dt.get(FIRST_INTEGER_PROPERTY),1);
		assertEquals(dt.get(FIRST_LONG_PROPERTY), 2L);
	}
	
	@Test
	public void should_get_all_three_types_from_dto_with_type_param() {
		SimpleGDTO dt = new SimpleGDTO()
				.add(FIRST_STRING_PROPERTY, "abba")//
				.add(FIRST_INTEGER_PROPERTY, 1)//
				.add(FIRST_LONG_PROPERTY, 2L);
		
		assertEquals(dt.get(FIRST_STRING_PROPERTY),"abba");
		assertEquals(dt.get(FIRST_INTEGER_PROPERTY),1);
		assertEquals(dt.get(FIRST_LONG_PROPERTY), 2L);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void should_throw_illegal_argument_exception_if_null_property_name(){
		new SimpleGDTO().add(null, "hei");
		fail("should not happen!");
		
	}
	
	@Test
	public void should_allow_null_value(){
		SimpleGDTO dt = new SimpleGDTO();
		dt.add(FIRST_STRING_PROPERTY, null);
		assertNull(dt.get(FIRST_STRING_PROPERTY));
	}
}
