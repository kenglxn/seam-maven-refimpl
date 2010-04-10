package no.knowit.testsupport.bean;

public class Dog extends Animal {
  public Dog() { says = "Bark"; }
  public Dog(final String name) { this(); this.name = name; }
}
