package no.knowit.testsupport.bean;

public class Bird extends Animal {
  public Bird(final String name) { super(name); }
  @Override
  public void say() { says = "Tweet"; }
}
