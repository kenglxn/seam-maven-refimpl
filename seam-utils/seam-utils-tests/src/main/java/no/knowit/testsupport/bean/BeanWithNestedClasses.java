package no.knowit.testsupport.bean;

public class BeanWithNestedClasses {
  class InnerClass {
    private int i;
  }

  static class StaticNestedClass {
    private int i;
  }
 
//  private int i;
  private InnerClass innerClass;
  private StaticNestedClass staticNestedClass;
  
  public BeanWithNestedClasses(int i) {
//    this.i = i;
//    innerClass = new InnerClass();
//    innerClass.i = i + 10;
//    System.out.println(innerClass.getClass().getSimpleName());
//    System.out.println(innerClass.getClass().getName());
    
    staticNestedClass = new StaticNestedClass();
    staticNestedClass.i = i + 20;
  }
}
