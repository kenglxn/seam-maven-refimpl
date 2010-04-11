package no.knowit.util;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import no.knowit.testsupport.bean.Animal;
import no.knowit.testsupport.bean.Bird;
import no.knowit.testsupport.bean.Cat;
import no.knowit.testsupport.bean.Dog;
import no.knowit.testsupport.bean.NestedBean;
import no.knowit.testsupport.bean.SimpleBean;
import no.knowit.util.MetaCache.Meta;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class MetaCacheTest {
  //private static Logger log = Logger.getLogger(MetaCacheTest.class);

  private static final Integer EXPECTED_ID = 2;
  private static final int EXPECTED_FOO = 200; 
  private static final String EXPECTED_BAR = "setBar -> Hello \"BAR\"!"; 
  private static final String EXPECTED_BAZ = "Hello BAZ!"; 
  private static final SimpleBean.Color EXPECTED_COLOR = SimpleBean.Color.RED;
  
  private static final String ASSERT_MESSAGE_FORMAT = "Actual: [%s]. Expected: [%s].";
  private final DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private Date expectedDate;
  
  
  @BeforeSuite
  public void beforeSuite() throws Exception {
    //System.out.println("******* " + this.getClass().getSimpleName() + ".beforeSuite()");
    
    dateformat.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
    expectedDate = dateformat.parse("2010-04-11 15:11:28");
  }
  
  @Test
  public void shouldSetAttributesByReflection() throws Exception {
    SimpleBean simpleBean = createSimpleBean();
    
    assert (Integer)MetaCache.get("id", simpleBean) == EXPECTED_ID 
      : String.format(ASSERT_MESSAGE_FORMAT, MetaCache.get("id", simpleBean), EXPECTED_ID);
    
    assert (Integer)MetaCache.get("foo", simpleBean) == EXPECTED_FOO 
      : String.format(ASSERT_MESSAGE_FORMAT, MetaCache.get("foo", simpleBean), EXPECTED_FOO);
    
    assert ((String)MetaCache.get("bar", simpleBean)).equals(EXPECTED_BAR) 
      : String.format(ASSERT_MESSAGE_FORMAT, MetaCache.get("bar", simpleBean), EXPECTED_BAR);
    
    assert ((String)MetaCache.get("baz", simpleBean)).equals(EXPECTED_BAZ) 
      : String.format(ASSERT_MESSAGE_FORMAT, MetaCache.get("baz", simpleBean), EXPECTED_BAZ);
    
    assert ((SimpleBean.Color)MetaCache.get("color", simpleBean)).equals(EXPECTED_COLOR) 
      : String.format(ASSERT_MESSAGE_FORMAT, MetaCache.get("color", simpleBean), EXPECTED_COLOR);

    Date actual = (Date)MetaCache.get("someDate", simpleBean);
    assert actual.equals(expectedDate)
      : String.format(ASSERT_MESSAGE_FORMAT, actual, expectedDate);
  }
  
  @Test(dependsOnMethods={ "shouldSetAttributesByReflection" })
  public void shouldGetClassFromCache() throws Exception {
    Meta meta = MetaCache.getMeta(SimpleBean.class);
    assert meta != null;
  }
  
  @Test
  public void shouldSetAttributesByReflectionOnNestedBean() throws Exception {
    NestedBean nestedBean = createNestedBean(createSimpleBean());
    
    {
      Object actual = MetaCache.get("intArray", nestedBean);
      assert actual.getClass().isArray() && Array.getLength(actual) == 3;
    }
    
    {
      Object actual = MetaCache.get("stringArray", nestedBean);
      assert actual.getClass().isArray() && Array.getLength(actual) == 5;
      
      List<String> expected = Arrays.asList("Array", "or", "List", "of", "strings");
      for (String v : (String[])actual) {
        assert expected.contains(v);
      }
    }
    
    {
      Object actual = MetaCache.get("stringList", nestedBean);
      assert actual instanceof Collection<?>;

      List<String> expected = Arrays.asList("Array", "or", "List", "of", "strings");
      for (String v : (Collection<String>)actual) {
        assert expected.contains(v);
      }
    }
    
  }
  
  private SimpleBean createSimpleBean() {
    SimpleBean simpleBean = new SimpleBean();
    MetaCache.set("id" ,      simpleBean, 2);
    MetaCache.set("foo",      simpleBean, 100);
    MetaCache.set("bar",      simpleBean, "Hello \"BAR\"!");
    MetaCache.set("baz",      simpleBean, "Hello BAZ!");
    MetaCache.set("color",    simpleBean, SimpleBean.Color.RED);
    MetaCache.set("someDate", simpleBean, expectedDate);

    return simpleBean;
  }
  
  private NestedBean createNestedBean(SimpleBean simpleBean) {
    NestedBean nestedBean = new NestedBean(99, simpleBean);
    MetaCache.set("intArray",    nestedBean, new int[]{1,2,3});
    MetaCache.set("stringArray", nestedBean, new String[]{"Array", "or", "List", "of", "strings"});

    Cat cat1 = new Cat("Puss");
    Cat cat2 = new Cat("Tiger");
    MetaCache.set("says", cat2, "Roar");
    MetaCache.set("catArray",  nestedBean, new Cat[]{cat1, cat2});

    List<Integer> integerList = Arrays.asList(new Integer(101), new Integer(201), new Integer(301));
    MetaCache.set("integerList", nestedBean, integerList);

    List<String> stringList = Arrays.asList("Array", "or", "List", "of", "strings");
    MetaCache.set("stringList", nestedBean, stringList);

    List<Animal> animalsList = Arrays.asList(new Dog("Laika"), new Cat("Fritz"), new Bird("Pip"));;
    MetaCache.set("animalList", nestedBean, animalsList);
    
    Map<String, String> stringMap = new HashMap<String, String>();
    stringMap.put("finland", "the land of a thousand lakes");
    stringMap.put("norway",  "the land of the midnight sun");
    stringMap.put("denmark", "the land of fairy-tales and mermaids");
    stringMap.put("sweden",  "one upon a time they had Volvo and Saab");
    stringMap.put("iceland", "the land of volcanoes and geysirs");
    MetaCache.set("stringMap", nestedBean, stringMap);
      
    Map<String, Dog> dogMap = new HashMap<String, Dog>();
    dogMap.put("Bonzo", new Dog("Bonzo"));
    dogMap.put("Fido",  new Dog("Fido"));
    dogMap.put("Lady",  new Dog("Lady"));
    dogMap.put("Tramp", new Dog("Tramp"));
    MetaCache.set("dogMap", nestedBean, dogMap);    
    
    return nestedBean;
  }
}
