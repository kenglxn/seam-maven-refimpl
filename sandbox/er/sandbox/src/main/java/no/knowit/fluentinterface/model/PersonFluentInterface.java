package no.knowit.fluentinterface.model;

public interface PersonFluentInterface {

	public PersonFluentInterface firstName(String firstName);

	public PersonFluentInterface lastName(String lastName);

	public PersonFluentInterface age(int age);

	public PersonFluentInterface active(boolean active);

	public Person create();
}
