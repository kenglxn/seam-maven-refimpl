package no.knowit.testsupport.bean.builderwithinheritance;

public class Circle extends Shape2D {

  private double radius;

  private Circle() {
    super();
  }

  public double getRadius() {
    return radius;
  }

  @Override
  protected void calculateArea() {
    area = Math.PI * radius * radius;
  }

  // Concrete Builder
  public static CircleBuilder with() {
    return new CircleBuilder();
  }

  public static class CircleBuilder extends Shape2DBuilder<Circle, CircleBuilder> {

    private CircleBuilder() {
    }

    @Override
    protected Circle createInstance() {
      return new Circle();
    }

    @Override
    protected Circle build() {
      return super.build();
    }

    // Builder methods
    public CircleBuilder radius(final double radius) {
      instance.radius = radius;
      return this;
    }
  } //~Builder
}
