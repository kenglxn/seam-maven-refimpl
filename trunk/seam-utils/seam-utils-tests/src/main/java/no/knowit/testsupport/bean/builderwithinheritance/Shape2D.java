package no.knowit.testsupport.bean.builderwithinheritance;

public abstract class Shape2D extends Shape {

  protected double area;

  protected Shape2D() {
    dimension = Dimension.TWO_D;
  }

  public double getArea() {
    return area;
  }

  protected abstract void calculateArea();

  // Inherited Abstract Builder
  public static abstract class Shape2DBuilder<T extends Shape2D, B extends Shape2DBuilder<T, B>>
  extends AbstractBuilder<T, B> {

    @Override
    protected T build() {
      instance.calculateArea();
      return instance;
    }

    // Builder methods
    // ....

  } //~Builder
}
