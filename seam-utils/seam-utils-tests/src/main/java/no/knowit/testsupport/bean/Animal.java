package no.knowit.testsupport.bean;

public abstract class Animal {
  @MyAnnotation
  protected String name;
  protected String says;
  private Animal() {say(); };
  protected Animal(final String name) { this(); this.name = name;  }
  public abstract void say();
}
