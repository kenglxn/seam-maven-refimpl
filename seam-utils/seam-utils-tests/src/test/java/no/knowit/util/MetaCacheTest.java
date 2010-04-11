package no.knowit.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.knowit.testsupport.bean.Animal;
import no.knowit.testsupport.bean.Bird;
import no.knowit.testsupport.bean.Cat;
import no.knowit.testsupport.bean.Dog;
import no.knowit.testsupport.bean.NestingBean;
import no.knowit.testsupport.bean.SimpleBean;
import no.knowit.util.MetaCache.Meta;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class MetaCacheTest {
  private static Logger log = Logger.getLogger(MetaCacheTest.class);

  private static final Integer EXPECTED_ID = 2;
  private static final int EXPECTED_FOO = 200; 
  private static final String EXPECTED_BAR = "setBar -> Hello \"BAR\"!"; 
  private static final String EXPECTED_BAZ = "Hello BAZ!"; 
  private static final SimpleBean.Color EXPECTED_COLOR = SimpleBean.Color.RED;
  
  private static final String FORMAT = "Actual: %s. Expected: %s";
  
  @BeforeSuite
  public void beforeSuite() throws Exception {
    //System.out.println("******* " + this.getClass().getSimpleName() + ".beforeSuite()");
  }
  
  @Test
  public void shouldSetAttributesByReflection() throws Exception {
    SimpleBean simpleBean = new SimpleBean();
    MetaCache.set("id" ,   simpleBean, 2);
    MetaCache.set("foo",   simpleBean, 100);
    MetaCache.set("bar",   simpleBean, "Hello \"BAR\"!");
    MetaCache.set("baz",   simpleBean, "Hello BAZ!");
    MetaCache.set("color", simpleBean, SimpleBean.Color.RED);

    assert (Integer)MetaCache.get("id", simpleBean) == EXPECTED_ID 
      : String.format(FORMAT, MetaCache.get("id", simpleBean), EXPECTED_ID);
    
    assert (Integer)MetaCache.get("foo", simpleBean) == EXPECTED_FOO 
      : String.format(FORMAT, MetaCache.get("foo", simpleBean), EXPECTED_FOO);
    
    assert ((String)MetaCache.get("bar", simpleBean)).equals(EXPECTED_BAR) 
      : String.format(FORMAT, MetaCache.get("bar", simpleBean), EXPECTED_BAR);
    
    assert ((String)MetaCache.get("baz", simpleBean)).equals(EXPECTED_BAZ) 
      : String.format(FORMAT, MetaCache.get("baz", simpleBean), EXPECTED_BAZ);
    
    assert ((SimpleBean.Color)MetaCache.get("color", simpleBean)).equals(EXPECTED_COLOR) 
      : String.format(FORMAT, MetaCache.get("color", simpleBean), EXPECTED_COLOR);
    
    assert MetaCache.get("someTime", simpleBean) instanceof java.util.Date 
      : String.format(
        FORMAT, MetaCache.get("someTime", simpleBean).getClass().getName(), "java.util.Date");
  }
  
  @Test(dependsOnMethods={ "shouldSetAttributesByReflection" })
  public void shouldGetClassFromCache() throws Exception {
    Meta meta = MetaCache.getMeta(SimpleBean.class);
    assert meta != null;
  }
  
  @Test
  public void toStringBuilderForVisualInspection() throws Exception {
    SimpleBean simpleBean = new SimpleBean();
    MetaCache.set("id" ,   simpleBean, 2);
    MetaCache.set("foo",   simpleBean, 100);
    MetaCache.set("bar",   simpleBean, "Hello \"BAR\"!");
    MetaCache.set("baz",   simpleBean, "Hello BAZ!");
    MetaCache.set("color", simpleBean, SimpleBean.Color.RED);
    
    log.debug(MetaCache.toStringBuilder(simpleBean).insert(0, '\n'));
    
    NestingBean nestingBean = new NestingBean(99, simpleBean);
    MetaCache.set("intArray",    nestingBean, new int[]{1,2,3});
    MetaCache.set("stringArray", nestingBean, new String[]{"I am", "a", "string", "array"});

    Cat cat1 = new Cat("Puss");
    Cat cat2 = new Cat("Tiger");
    MetaCache.set("says", cat2, "Roar");
    MetaCache.set("catArray",  nestingBean, new Cat[]{cat1, cat2});

    List<Integer> integers = Arrays.asList(new Integer(101), new Integer(201), new Integer(301));
    MetaCache.set("integersList", nestingBean, integers);

    List<String> stringList = Arrays.asList("A", "list", "of", "strings");
    MetaCache.set("stringsList", nestingBean, stringList);

    List<Animal> animals = Arrays.asList(new Dog("Laika"), new Cat("Fritz"), new Bird("Pip"));;
    MetaCache.set("animalsList", nestingBean, animals);
    
    Map<String, String> strings = new HashMap<String, String>();
    strings.put("finland", "the land of a thousand lakes");
    strings.put("norway",  "the land of the midnight sun");
    strings.put("denmark", "the land of fairy-tales and mermaids");
    strings.put("sweden",  "one upon a time they had Volvo and Saab");
    strings.put("iceland", "the land of volcanoes and geysirs");
    MetaCache.set("stringsMap", nestingBean, strings);
      
    Map<String, Dog> dogs = new HashMap<String, Dog>();
    dogs.put("Bonzo", new Dog("Bonzo"));
    dogs.put("Fido",  new Dog("Fido"));
    dogs.put("Lady",  new Dog("Lady"));
    dogs.put("Tramp", new Dog("Tramp"));
    MetaCache.set("dogsMap", nestingBean, dogs);
    
    log.debug(MetaCache.toStringBuilder(nestingBean).insert(0, '\n'));
  }
}
