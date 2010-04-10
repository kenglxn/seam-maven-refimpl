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
    
    System.out.println(MetaCache.objectToString(simpleBean));
    
    
    SimpleBean sb1 = new SimpleBean();
    MetaCache.set("id" , sb1, 10001);
    MetaCache.set("bar", sb1, "Instance One");
    
    SimpleBean sb2 = new SimpleBean();
    MetaCache.set("id" , sb2, 10002);
    MetaCache.set("bar", sb2, "Instance Two");
    
    NestingBean nestingBean = new NestingBean(99, simpleBean);
    MetaCache.set("intArray",    nestingBean, new int[]{1,2,3});
    MetaCache.set("stringArray", nestingBean, new String[]{"I am", "a", "string", "array"});
    MetaCache.set("beanArray",   nestingBean, new SimpleBean[]{sb1, sb2});
    
    List<Animal> animals = Arrays.asList(new Dog("Laika"), new Cat("Fritz"), new Bird("Pip"));;
    MetaCache.set("animalsList", nestingBean, animals);
    
    Map<String, Dog> dogs = new HashMap<String, Dog>();
    dogs.put("Bonzo", new Dog("Bonzo"));
    dogs.put("Fido",  new Dog("Fido"));
    dogs.put("Lady",  new Dog("Lady"));
    dogs.put("Tramp", new Dog("Tramp"));
    MetaCache.set("dogsMap", nestingBean, dogs);
    
    System.out.println(MetaCache.objectToString(nestingBean));
  }
  
}
