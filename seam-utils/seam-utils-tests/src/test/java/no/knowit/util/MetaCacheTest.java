package no.knowit.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.knowit.seam.framework.SeamFrameworkTest;
import no.knowit.testsupport.bean.Animal;
import no.knowit.testsupport.bean.Bird;
import no.knowit.testsupport.bean.Cat;
import no.knowit.testsupport.bean.Dog;
import no.knowit.testsupport.bean.NestingBean;
import no.knowit.testsupport.bean.SimpleBean;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class MetaCacheTest {
  private static final LogProvider log = Logging.getLogProvider(SeamFrameworkTest.class);

  @BeforeSuite
  public void beforeSuite() throws Exception {
    
    //System.out.println("******* " + this.getClass().getSimpleName() + ".beforeSuite()");
    
  }
  
  @Test
  public void should() throws Exception {
    SimpleBean simpleBean = new SimpleBean();
    MetaCache.set("id" ,   simpleBean, 2);
    MetaCache.set("foo",   simpleBean, 100);
    MetaCache.set("bar",   simpleBean, "Hello \"BAR\"!");
    MetaCache.set("baz",   simpleBean, "Hello BAZ!");
    MetaCache.set("color", simpleBean, SimpleBean.Color.RED);
    
    System.out.println(MetaCache.toStringBuilder(simpleBean));
    
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
    
    
    System.out.println(MetaCache.toStringBuilder(nestingBean));
  }
  
}
