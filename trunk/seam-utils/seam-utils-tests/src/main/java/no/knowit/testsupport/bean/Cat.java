package no.knowit.testsupport.bean;

public class Cat extends Animal {
  
  private class TeddyBear {
    private String name = "Pooky";
  }

  private TeddyBear teddy = new TeddyBear();
  
  public Cat(final String name) { super(name); }
  @Override
  public void say() { says = "Purr"; }
}
