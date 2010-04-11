package no.knowit.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class ToStringBuilderTest {
  
  private static Logger log = Logger.getLogger(ToStringBuilderTest.class);
  
  private static final String EXPECTED_INTARRAY_TOSTRING = "{ [1, 2, 3]\n}";
  private static final String EXPECTED_STRINGARRAY_TOSTRING = 
    "{ [\"Array\", \"or\", \"List\", \"of\", \"strings\"]\n}";
  
  private static final String EXPECTED_STRINGMAP_TOSTRING = 
    "{ {\n" +
    "    \"finland\" : \"the land of a thousand lakes\",\n" +
    "    \"norway\" : \"the land of the midnight sun\"\n" +
    "  }\n" +
    "}";

  private static final String EXPECTED_SIMPLEBEAN_FRAGMENT = 
    "{ \"SimpleBean\" : {\n" +
    "    \"id\" : 2,\n" +
    "    \"baz\" : \"Hello BAZ!\",\n" +
    "    \"color\" : RED,\n" +
    "    \"foo\" : 200,\n" +
    "    \"bar\" : \"setBar -> Hello \\\"BAR\\\"!\",\n";
    
  private static final String EXPECTED_NESTEDBEAN_FRAGMENT_1 = "{ \"NestedBean\" : {";

  private static final String EXPECTED_NESTEDBEAN_FRAGMENT_2 = 
    "\"norway\" : \"the land of the midnight sun\"";
    
    
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
  public void anythingToString() throws Exception {
    int[] intArray = new int[]{1,2,3};
    log.debug(ToStringBuilder.build(intArray).insert(0, '\n'));

    String actual = ToStringBuilder.build(intArray).toString(); 
    assert actual.equals(EXPECTED_INTARRAY_TOSTRING)  
      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_INTARRAY_TOSTRING);
    
    String[] stringArray = new String[]{"Array", "or", "List", "of", "strings"};
    actual = ToStringBuilder.build(stringArray).toString(); 
    assert actual.equals(EXPECTED_STRINGARRAY_TOSTRING)  
      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_STRINGARRAY_TOSTRING);

    List<Integer> integerList = Arrays.asList(new Integer(1), new Integer(2), new Integer(3));
    actual = ToStringBuilder.build(integerList).toString(); 
    assert actual.equals(EXPECTED_INTARRAY_TOSTRING)  
      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_INTARRAY_TOSTRING);

    List<String> stringList = Arrays.asList("Array", "or", "List", "of", "strings");
    actual = ToStringBuilder.build(stringList).toString(); 
    assert actual.equals(EXPECTED_STRINGARRAY_TOSTRING)  
      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_STRINGARRAY_TOSTRING);
    
    Map<String, String> stringMap = new HashMap<String, String>();
    stringMap.put("finland", "the land of a thousand lakes");
    stringMap.put("norway",  "the land of the midnight sun");
    actual = ToStringBuilder.build(stringMap).toString(); 
    assert actual.equals(EXPECTED_STRINGMAP_TOSTRING)  
      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_STRINGMAP_TOSTRING);

    SimpleBean simpleBean = createSimpleBean();
    actual = ToStringBuilder.build(simpleBean).toString();
    assert actual.startsWith(EXPECTED_SIMPLEBEAN_FRAGMENT)
      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_SIMPLEBEAN_FRAGMENT);
    
    log.debug(ToStringBuilder.build(simpleBean).insert(0, '\n'));

    NestedBean nestedBean = createNestedBean(simpleBean);
    actual = ToStringBuilder.build(nestedBean).toString();
    assert actual.startsWith(EXPECTED_NESTEDBEAN_FRAGMENT_1) && 
      actual.contains(EXPECTED_NESTEDBEAN_FRAGMENT_2)
      : String.format(ASSERT_MESSAGE_FORMAT, actual, 
          EXPECTED_NESTEDBEAN_FRAGMENT_1 + "\n" + EXPECTED_NESTEDBEAN_FRAGMENT_2);
    
    log.debug(ToStringBuilder.build(nestedBean).insert(0, '\n'));
  }

  
  private SimpleBean createSimpleBean() {
    SimpleBean simpleBean = new SimpleBean();
    MetaCache.set("id" ,   simpleBean, 2);
    MetaCache.set("foo",   simpleBean, 100);
    MetaCache.set("bar",   simpleBean, "Hello \"BAR\"!");
    MetaCache.set("baz",   simpleBean, "Hello BAZ!");
    MetaCache.set("color", simpleBean, SimpleBean.Color.RED);
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
