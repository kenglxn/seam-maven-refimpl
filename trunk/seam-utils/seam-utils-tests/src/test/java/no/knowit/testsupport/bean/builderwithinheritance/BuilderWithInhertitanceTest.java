package no.knowit.testsupport.bean.builderwithinheritance;

import org.testng.annotations.Test;

public class BuilderWithInhertitanceTest {

  @Test
  public void shouldBuildCircle() {
    final Circle c = Circle.with()
    .radius(10)
    .name("My Circle")
    .build();

    assert Circle.Dimension.TWO_D.equals(c.getDimension());
    assert c.getRadius() == 10;
    assert "My Circle".equals(c.getName());
    assert Math.round(c.getArea()) == 314 : "Expected 314 but was " + c.getArea();
  }

  @Test
  public void shouldBuildRectangle() {
    final Rectangle r = Rectangle.with()
    .name("My Rectangle")
    .height(10)
    .width(10)
    .build();

    assert Rectangle.Dimension.TWO_D.equals(r.getDimension());
    assert r.getHeight() == 10;
    assert r.getWidth() == 10;
    assert "My Rectangle".equals(r.getName());
    assert Math.round(r.getArea()) == 100 : "Expected 100 but was " + r.getArea();

  }

}
