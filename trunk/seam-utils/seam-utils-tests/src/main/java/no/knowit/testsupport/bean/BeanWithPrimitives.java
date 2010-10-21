package no.knowit.testsupport.bean;

import java.util.Date;

@SuppressWarnings("unused")
public class BeanWithPrimitives {
  public static final String A_PUBLIC_CONSTANT = "A public constant should not appear when ToStringBuilder.toStrig is executed.";
  
  public enum Color {RED, YELLOW, GREEN};
  
  private Integer id;
  @MyAnnotation
  private int foo; 
  private final String finalField = "A final field";
  protected String bar;
  public String baz;
  public Color color;
  public Date someDate = new Date();
  
  private void setFoo(int foo) {
    this.foo = foo * 2;
  }
  
  public int getFoo() {
    return foo;
  }
  
  protected void setBar(final String bar) {
    this.bar = "setBar -> " + bar;
  }
  
  @MyAnnotation
  public void setBaz(String baz) {
    this.baz = baz;
  }
  
  @MyAnnotation
  public Color getColor() {
    return color;
  }
}
