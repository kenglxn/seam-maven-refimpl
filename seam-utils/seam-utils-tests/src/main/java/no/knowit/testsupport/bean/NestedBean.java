package no.knowit.testsupport.bean;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class NestedBean {
  private Integer id;
  private Float floatValue;
  private SimpleBean simpleBean;
  private int[] intArray;
  private String[] stringArray;
  private Cat[] catArray;
  private List<Integer> integerList;
  private List<String> stringList;
  private List<Animal> animalList;
  private Map<String, String> stringMap;
  private Map<String, Dog> dogMap;
  
  public NestedBean() {
    super();
  }

  public NestedBean(Integer id, SimpleBean simpleBean) {
    this.id = id;
    this.simpleBean = simpleBean;
  }
  
  public List<Animal> getAnimalList() {
    return animalList;
  }

}

