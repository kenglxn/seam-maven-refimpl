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
import java.util.Map.Entry;

import no.knowit.testsupport.bean.Animal;
import no.knowit.testsupport.bean.Bird;
import no.knowit.testsupport.bean.Cat;
import no.knowit.testsupport.bean.Dog;
import no.knowit.testsupport.bean.BeanWithComposition;
import no.knowit.testsupport.bean.BeanWithPrimitives;
import no.knowit.util.MetaCache.Meta;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class MetaCacheTest {
  //private static Logger log = Logger.getLogger(MetaCacheTest.class);

  private static final Integer EXPECTED_ID = 2;
  private static final int EXPECTED_FOO = 200; 
  private static final String EXPECTED_BAR = "setBar -> Hello \"BAR\"!"; 
  private static final String EXPECTED_BAZ = "Hello BAZ!"; 
  private static final BeanWithPrimitives.Color EXPECTED_COLOR = BeanWithPrimitives.Color.RED;
  
  private static final List<String> EXPECTED_STRINGLIST = Arrays.asList(
      "Array", "or", "List", "of", "strings");

  private static Map<String, String> EXPECTED_STRINGMAP = new HashMap<String, String>();
  
  private static Map<String, Dog> EXPECTED_DOGMAP = new HashMap<String, Dog>();

  private static final String ASSERT_MESSAGE_FORMAT = "Actual: [%s]. Expected: [%s].";
  private final DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private Date expectedDate;
  
  
  @BeforeSuite
  public void beforeSuite() throws Exception {
    //System.out.println("******* " + this.getClass().getSimpleName() + ".beforeSuite()");
    
    dateformat.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
    expectedDate = dateformat.parse("2010-04-11 15:11:28");
    
    EXPECTED_STRINGMAP.put("finland", "the land of a thousand lakes");
    EXPECTED_STRINGMAP.put("norway",  "the land of the midnight sun");
    EXPECTED_STRINGMAP.put("denmark", "the land of fairy-tales and mermaids");
    EXPECTED_STRINGMAP.put("sweden",  "one upon a time they had Volvo and Saab");
    EXPECTED_STRINGMAP.put("iceland", "the land of volcanoes and geysirs");
    
    EXPECTED_DOGMAP.put("Bonzo", new Dog("Bonzo"));
    EXPECTED_DOGMAP.put("Fido",  new Dog("Fido"));
    EXPECTED_DOGMAP.put("Lady",  new Dog("Lady"));
    EXPECTED_DOGMAP.put("Tramp", new Dog("Tramp"));
  }
  
  @Test
  public void shouldSetAttributesByReflection() throws Exception {
    BeanWithPrimitives beanWithPrimitives = createSimpleBean();
    
    assert (Integer)MetaCache.get("id", beanWithPrimitives) == EXPECTED_ID 
      : String.format(ASSERT_MESSAGE_FORMAT, MetaCache.get("id", beanWithPrimitives), EXPECTED_ID);
    
    assert (Integer)MetaCache.get("foo", beanWithPrimitives) == EXPECTED_FOO 
      : String.format(ASSERT_MESSAGE_FORMAT, MetaCache.get("foo", beanWithPrimitives), EXPECTED_FOO);
    
    assert ((String)MetaCache.get("bar", beanWithPrimitives)).equals(EXPECTED_BAR) 
      : String.format(ASSERT_MESSAGE_FORMAT, MetaCache.get("bar", beanWithPrimitives), EXPECTED_BAR);
    
    assert ((String)MetaCache.get("baz", beanWithPrimitives)).equals(EXPECTED_BAZ) 
      : String.format(ASSERT_MESSAGE_FORMAT, MetaCache.get("baz", beanWithPrimitives), EXPECTED_BAZ);
    
    assert ((BeanWithPrimitives.Color)MetaCache.get("color", beanWithPrimitives)).equals(EXPECTED_COLOR) 
      : String.format(ASSERT_MESSAGE_FORMAT, MetaCache.get("color", beanWithPrimitives), EXPECTED_COLOR);

    Date actual = (Date)MetaCache.get("someDate", beanWithPrimitives);
    assert actual.equals(expectedDate)
      : String.format(ASSERT_MESSAGE_FORMAT, actual, expectedDate);
  }
  
  @Test(dependsOnMethods={ "shouldSetAttributesByReflection" })
  public void shouldGetClassFromCache() throws Exception {
    Meta meta = MetaCache.getMeta(BeanWithPrimitives.class);
    assert meta != null;
  }
  
  @Test
  public void shouldSetAttributesByReflectionOnNestedBean() throws Exception {
    BeanWithComposition beanWithComposition = createNestedBean(createSimpleBean());
    
    Object actual = MetaCache.get("intArray", beanWithComposition);
    assert actual.getClass().isArray() && Array.getLength(actual) == 3;
    
    actual = MetaCache.get("stringArray", beanWithComposition);
    assert actual.getClass().isArray() && Array.getLength(actual) == 5;
    for (String v : (String[])actual) {
      assert EXPECTED_STRINGLIST.contains(v);
    }
    
    actual = MetaCache.get("stringList", beanWithComposition);
    assert actual instanceof Collection<?>;

    List<?> list = (List<?>) actual;
    for (String v : EXPECTED_STRINGLIST) {
      assert list.contains(v);
    }
    
    actual = MetaCache.get("stringMap", beanWithComposition);
    assert actual instanceof Map<?, ?>;

    Map<?, ?> map = (Map<?, ?>) actual;
    for (Entry<String, String> entry : EXPECTED_STRINGMAP.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      assert map.containsKey(key);
      assert map.get(key).toString().equals(value.toString());
    }
    
    actual = MetaCache.get("animalList", beanWithComposition);
    assert actual instanceof Collection<?>;

    List<?> animalList = (List<?>) actual;
    for (Object obj : animalList) {
      assert obj instanceof Animal;
    }
    
    actual = MetaCache.get("dogMap", beanWithComposition);
    assert actual instanceof Map<?, ?>;

    Map<?, ?> dogMap = (Map<?, ?>) actual;
    for (Entry<String, Dog> entry : EXPECTED_DOGMAP.entrySet()) {
      String key = entry.getKey();
      Dog dog = entry.getValue();
      String dogSays = MetaCache.get("says", dog).toString();
      assert dogMap.containsKey(key);
      assert dogSays.equals("Bark") : String.format(ASSERT_MESSAGE_FORMAT, dogSays, "Bark");
    }
  }
  
  private BeanWithPrimitives createSimpleBean() {
    BeanWithPrimitives beanWithPrimitives = new BeanWithPrimitives();
    MetaCache.set("id" ,      beanWithPrimitives, 2);
    MetaCache.set("foo",      beanWithPrimitives, 100);
    MetaCache.set("bar",      beanWithPrimitives, "Hello \"BAR\"!");
    MetaCache.set("baz",      beanWithPrimitives, "Hello BAZ!");
    MetaCache.set("color",    beanWithPrimitives, BeanWithPrimitives.Color.RED);
    MetaCache.set("someDate", beanWithPrimitives, expectedDate);

    return beanWithPrimitives;
  }
  
  private BeanWithComposition createNestedBean(BeanWithPrimitives beanWithPrimitives) {
    BeanWithComposition beanWithComposition = new BeanWithComposition(99, beanWithPrimitives);
    MetaCache.set("intArray",    beanWithComposition, new int[]{1,2,3});
    MetaCache.set("stringArray", beanWithComposition, new String[]{"Array", "or", "List", "of", "strings"});

    Cat cat1 = new Cat("Puss");
    Cat cat2 = new Cat("Tiger");
    MetaCache.set("says", cat2, "Roar");
    MetaCache.set("catArray",  beanWithComposition, new Cat[]{cat1, cat2});

    List<Integer> integerList = Arrays.asList(new Integer(101), new Integer(201), new Integer(301));
    MetaCache.set("integerList", beanWithComposition, integerList);

    List<String> stringList = Arrays.asList("Array", "or", "List", "of", "strings");
    MetaCache.set("stringList", beanWithComposition, stringList);

    List<Animal> animalList = Arrays.asList(new Dog("Laika"), new Cat("Fritz"), new Bird("Pip"));;
    MetaCache.set("animalList", beanWithComposition, animalList);
    
    Map<String, String> stringMap = new HashMap<String, String>();
    stringMap.put("finland", "the land of a thousand lakes");
    stringMap.put("norway",  "the land of the midnight sun");
    stringMap.put("denmark", "the land of fairy-tales and mermaids");
    stringMap.put("sweden",  "one upon a time they had Volvo and Saab");
    stringMap.put("iceland", "the land of volcanoes and geysirs");
    MetaCache.set("stringMap", beanWithComposition, stringMap);
      
    Map<String, Dog> dogMap = new HashMap<String, Dog>();
    dogMap.put("Bonzo", new Dog("Bonzo"));
    dogMap.put("Fido",  new Dog("Fido"));
    dogMap.put("Lady",  new Dog("Lady"));
    dogMap.put("Tramp", new Dog("Tramp"));
    MetaCache.set("dogMap", beanWithComposition, dogMap);    
    
    return beanWithComposition;
  }
}
