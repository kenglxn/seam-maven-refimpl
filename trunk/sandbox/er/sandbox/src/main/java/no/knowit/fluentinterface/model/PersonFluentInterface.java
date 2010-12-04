package no.knowit.fluentinterface.model;

//http://codemonkeyism.com/fluent-interface-and-reflection-for-object-building-in-java/
public interface PersonFluentInterface {

	public PersonFluentInterface firstName(String firstName);

	public PersonFluentInterface lastName(String lastName);

	public PersonFluentInterface age(int age);

	public PersonFluentInterface active(boolean active);

	public Person create();
}
