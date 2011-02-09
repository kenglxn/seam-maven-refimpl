package no.knowit.testsupport.bean.builderwithinheritance;


public class Rectangle extends Shape2D {

  private double height;
  private double width;

  private Rectangle() {
    super();
  }

  public double getHeight() {
    return height;
  }

  public double getWidth() {
    return width;
  }

  @Override
  protected void calculateArea() {
    area = width * height;
  }

  // Concrete Builder
  public static RectangleBuilder with() {
    return new RectangleBuilder();
  }

  public static class RectangleBuilder extends Shape2DBuilder<Rectangle, RectangleBuilder> {

    private RectangleBuilder() {
    }

    @Override
    protected Rectangle createInstance() {
      return new Rectangle();
    }

    @Override
    protected Rectangle build() {
      return super.build();
    }

    // Builder methods
    public RectangleBuilder height(final double height) {
      instance.height = height;
      return this;
    }

    public RectangleBuilder width(final double width) {
      instance.width = width;
      return this;
    }
  } //~Builder

}
