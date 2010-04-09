package no.knowit.testsupport.bean;

public class NestingBean {
  private Integer id;
  private int[] intArray;
  private String[] stringArray;
  private SimpleBean[] beanArray;
  private SimpleBean simpleBean;
  
  public NestingBean() {
    super();
  }

  public NestingBean(Integer id, SimpleBean simpleBean) {
    this.id = id;
    this.simpleBean = simpleBean;
  }
}

