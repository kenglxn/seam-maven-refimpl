package no.knowit.testsupport.bean;

public class Cat extends Animal {
  public Cat() { says = "Purr"; }
  public Cat(final String name) { this(); this.name = name; }
}
