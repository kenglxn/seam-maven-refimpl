package no.knowit.testsupport.bean;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class BeanWithComposition {
  private Integer id;
  private Float floatValue;
  private BeanWithPrimitives beanWithPrimitives;
  private int[] intArray;
  private int[][] twoDimensionalArray;
  private String[] stringArray;
  private Cat[] catArray;
  private List<Integer> integerList;
  private List<String> stringList;
  private List<Animal> animalList;
  private Map<String, String> stringMap;
  private Map<String, Dog> dogMap;
  
  public BeanWithComposition() {
    super();
  }

  public BeanWithComposition(Integer id, BeanWithPrimitives beanWithPrimitives) {
    this.id = id;
    this.beanWithPrimitives = beanWithPrimitives;
  }
  
  public List<Animal> getAnimalList() {
    return animalList;
  }

}

