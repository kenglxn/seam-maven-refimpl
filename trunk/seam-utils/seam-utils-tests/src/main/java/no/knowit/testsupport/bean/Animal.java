package no.knowit.testsupport.bean;

public abstract class Animal {
  @MyAnnotation
  protected String name;
  protected String says;
  public Animal() {};
  public Animal(final String name) { this.name = name; }
  public final String say() { return says; }
}
