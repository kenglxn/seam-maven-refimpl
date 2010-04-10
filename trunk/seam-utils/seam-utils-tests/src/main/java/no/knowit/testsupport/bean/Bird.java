package no.knowit.testsupport.bean;

public class Bird extends Animal {
  public Bird() { says = "Tweet"; }
  public Bird(final String name) { this(); this.name = name; }
}
