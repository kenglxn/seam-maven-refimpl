package no.knowit.fluentinterface.model;

import static org.junit.Assert.*;

import no.knowit.fluentinterface.model.Person;

import org.junit.Test;

public class PersonTest {
	private static final int AGE = 40;
	private static final String FIRSTNAME = "Ola";
	private static final String LASTNAME = "Nordmann";

	@Test
	public void shall_create_active_person(){
		Person p = Person.with().firstName(FIRSTNAME)//
		.lastName(LASTNAME)//
		.age(AGE)//
		.active(true)//
		.create();
		assertEquals(FIRSTNAME, p.getFirstName());
		assertEquals(LASTNAME, p.getLastName());
		assertEquals(40, p.getAge());
		assertTrue(p.isActive());
	}
	@Test
	public void shall_create_inactive_person(){
		Person p = Person.with().firstName(FIRSTNAME)//
		.lastName(LASTNAME)//
		.age(AGE)//
		.active(false)//
		.create();
		assertEquals(FIRSTNAME, p.getFirstName());
		assertEquals(LASTNAME, p.getLastName());
		assertEquals(40, p.getAge());
		assertFalse(p.isActive());
	}
}
