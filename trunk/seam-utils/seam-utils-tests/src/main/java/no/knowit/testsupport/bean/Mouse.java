package no.knowit.testsupport.bean;

public class Mouse extends Animal {
  public Mouse(final String name) { super(name); }
  @Override
  public void say() { says = "Peep"; }

}
