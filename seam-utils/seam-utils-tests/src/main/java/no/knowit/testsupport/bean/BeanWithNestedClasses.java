package no.knowit.testsupport.bean;

public class BeanWithNestedClasses {
  
  private class InnerClass {
    private int i;
  }

  private static class StaticNestedClass {
    private int i;
  }
 
  public enum NestedEnum {RED, YELLOW, GREEN};
  
  public interface NestedInterface {
    void foo();
  }

  private InnerClass innerClass;
  
  private StaticNestedClass staticNestedClass;
  
  private NestedEnum nestedEnum;
  
  private int i;
  
  public BeanWithNestedClasses(int i) {
    this.i = i;
    this.nestedEnum = NestedEnum.YELLOW;
    
    innerClass = new InnerClass();
    innerClass.i = i + 10;
    
    staticNestedClass = new StaticNestedClass();
    staticNestedClass.i = i + 20;
  }
}
