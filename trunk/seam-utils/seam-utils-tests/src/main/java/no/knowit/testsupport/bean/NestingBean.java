package no.knowit.testsupport.bean;

import java.util.List;
import java.util.Map;

public class NestingBean {
  private Integer id;
  private int[] intArray;
  private String[] stringArray;
  private SimpleBean[] beanArray;
  private SimpleBean simpleBean;
  private List<Animal> animalsList;
  private Map<String, Dog> dogsMap;
  private Map<String, String> stringsMap;
  
  public NestingBean() {
    super();
  }

  public NestingBean(Integer id, SimpleBean simpleBean) {
    this.id = id;
    this.simpleBean = simpleBean;
  }
}

