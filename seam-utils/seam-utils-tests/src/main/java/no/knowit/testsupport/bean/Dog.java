package no.knowit.testsupport.bean;

public class Dog extends Animal {
  public Dog(final String name) { super(name); }
  @Override
  public void say() { says = "Bark"; }
}
