/**
 * http://www.vshank77.com/2009/12/builder-pattern-abused.html
 */
package no.knowit.testsupport.bean.builderwithinheritance;

public abstract class Shape {
  protected Dimension dimension;
  private String name;

  public enum Dimension {
    TWO_D, THREE_D;
  }

  public String getName() {
    return name;
  }

  public Dimension getDimension() {
    return dimension;
  }

  // Abstract Builder
  public static abstract class AbstractBuilder<T extends Shape, B extends AbstractBuilder<T, B>> {

    protected T instance = createInstance();

    protected abstract T createInstance();

    protected abstract T build();

    protected AbstractBuilder() {
      instance = createInstance();
    }

    @SuppressWarnings("unchecked")
    protected B self() {
      return (B) this;
    }

    // Builder methods
    public B name(final String name) {
      instance.name = name;
      return self();
    }

  } //~Builder
}