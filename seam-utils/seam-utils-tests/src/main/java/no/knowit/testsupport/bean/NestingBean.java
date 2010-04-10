package no.knowit.testsupport.bean;

import java.util.List;
import java.util.Map;

public class NestingBean {
  private Integer id;
  private SimpleBean simpleBean;
  private int[] intArray;
  private String[] stringArray;
  private Cat[] catArray;
  private List<Integer> integersList;
  private List<String> stringsList;
  private List<Animal> animalsList;
  private Map<String, String> stringsMap;
  private Map<String, Dog> dogsMap;
  
  public NestingBean() {
    super();
  }

  public NestingBean(Integer id, SimpleBean simpleBean) {
    this.id = id;
    this.simpleBean = simpleBean;
  }
}

