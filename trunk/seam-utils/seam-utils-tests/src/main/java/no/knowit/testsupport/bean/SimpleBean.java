package no.knowit.testsupport.bean;

import java.util.Date;

public class SimpleBean {
  public enum Color {RED, YELLOW, GREEN};
  
  private Integer id;
  private int foo; 
  protected String bar;
  public String baz;
  public Color color;
  public Date someTime = new Date();
  
  private void setFoo(int foo) {
    this.foo = foo * 2;
  }
  
  protected void setBar(final String bar) {
    this.bar = "setBar -> " + bar;
  }
  
  public void setBaz(String baz) {
    this.baz = baz;
  }
}
