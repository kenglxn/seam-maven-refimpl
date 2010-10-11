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
import no.knowit.testsupport.bean.BeanWithNestedClasses;
import no.knowit.testsupport.bean.Bird;
import no.knowit.testsupport.bean.Cat;
import no.knowit.testsupport.bean.Dog;
import no.knowit.testsupport.bean.BeanWithComposition;
import no.knowit.testsupport.bean.BeanWithPrimitives;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class ToStringBuilderTest {
  
  private static Logger log = Logger.getLogger(ToStringBuilderTest.class);
  
  private static final String EXPECTED_INTARRAY_TOSTRING = "{ [1, 2, 3]\n}";
  private static final String EXPECTED_FLOATVALUE_TOSTRING = 
    "{ \"Float\" : {\n    \"value\" : 12.0\n  }\n}";
  
  private static final String EXPECTED_STRINGARRAY_TOSTRING = 
    "{ [\"Array\", \"or\", \"List\", \"of\", \"strings\"]\n}";
  
  private static final String EXPECTED_STRINGMAP_TOSTRING = 
    "{ {\n" +
    "    \"Finland\" : \"the land of a thousand lakes\",\n" +
    "    \"Norway\" : \"the land of the midnight sun\"\n" +
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
    "\"Norway\" : \"the land of the midnight sun\"";
    
    
  private static final String ASSERT_MESSAGE_FORMAT = "Actual: [%s]. Expected: [%s].";
  private final DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
  private Date expectedDate;
  
  
  @BeforeSuite
  public void beforeSuite() throws Exception {
    //dateformat.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
    expectedDate = dateformat.parse("2010-04-11 15:11:28 +0200");
  }

  @Test
  public void should() throws Exception {
    String actual;
    
//    BeanWithPrimitives beanWithPrimitives = createSimpleBean();
//    
//    actual = ToStringBuilder.builder(beanWithPrimitives)
//      .publicFieldsOnly(true)
//      .hierarchical(true)
//      .indentation(2)
//      .rootNodeAlias("FOAA")
//      .printRootNode(true)
//      .fieldNameFormatter(new ToStringBuilder.FieldNameFormatter() {
//        @Override
//        public String format(Object owner, String name) {
//          return name + "->";
//        }
//      })
//      .fieldValueFormatter(new ToStringBuilder.FieldValueFormatter() {
//        @Override
//        public String format(final Object owner, final Object value) {
//          return value == null ? "" : "@" + value.toString();
//        }
//      })
//      .toString();
//    log.debug("\n"+actual);
//
//    BeanWithComposition beanWithComposition = createNestedBean(beanWithPrimitives);
//    actual = ToStringBuilder.builder(beanWithComposition)
//      .publicFieldsOnly(false)
//      .toString();
//    log.debug("\n"+actual);
//
//    actual = ToStringBuilder.builder(beanWithPrimitives)
//      .indentation(1)
//      .withField("id")
//      .withField("color")
//      .withField("baz")
//      .toString();
//    log.debug("\n"+actual);
//    
//    actual = ToStringBuilder.builder(beanWithComposition)
//      .indentation(2)
//      .withField("animalList")
//      .withField("catArray")
//      .withField("Animal.name")
//      .withField("Animal.says")
//      .hierarchical(false)
//      .toString();
//    log.debug("\n"+actual);
//    
//    List<Integer> integerList = Arrays.asList(new Integer(101), new Integer(201), new Integer(301));
//    actual = ToStringBuilder.builder(integerList)
//      .toString();
//    log.debug("\n"+actual);
//    
//    Map<String, String> stringMap = new HashMap<String, String>();
//    stringMap.put("Finland", "the land of a thousand lakes");
//    stringMap.put("Norway",  "the land of the midnight sun");
//    stringMap.put("Denmark", "the land of fairy-tales and mermaids");
//    stringMap.put("Sweden",  "one upon a time they had Volvo and Saab");
//    stringMap.put("Iceland", "the land of volcanoes and geysirs");
//    
//    actual = ToStringBuilder.builder(stringMap)
//      .toString();
//    log.debug("\n"+actual);
//  
//    actual = ToStringBuilder.builder(new Integer(1000))
//      .toString();
//    log.debug("\n"+actual);
    
    actual = ToStringBuilder.builder(new BeanWithNestedClasses(100))
      .toString();
    log.debug("\n"+actual);
  }
  
//  @Test
//  public void anythingToString() throws Exception {
//    
//    int[] intArray = new int[]{1,2,3};
//    String actual = ToStringBuilder.build(intArray).toString(); 
//    assert actual.equals(EXPECTED_INTARRAY_TOSTRING)  
//      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_INTARRAY_TOSTRING);
//    
//    String[] stringArray = new String[]{"Array", "or", "List", "of", "strings"};
//    actual = ToStringBuilder.build(stringArray).toString(); 
//    assert actual.equals(EXPECTED_STRINGARRAY_TOSTRING)  
//      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_STRINGARRAY_TOSTRING);
//
//    List<Integer> integerList = Arrays.asList(new Integer(1), new Integer(2), new Integer(3));
//    actual = ToStringBuilder.build(integerList).toString(); 
//    log.debug("\n"+actual);
//    assert actual.equals(EXPECTED_INTARRAY_TOSTRING)  
//      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_INTARRAY_TOSTRING);
//    
//    Float floatValue = 12.0F;
//    actual = ToStringBuilder.build(floatValue).toString(); 
//    log.debug("\n"+actual);
//    assert actual.equals(EXPECTED_FLOATVALUE_TOSTRING)  
//      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_FLOATVALUE_TOSTRING);
//
//
//    List<String> stringList = Arrays.asList("Array", "or", "List", "of", "strings");
//    actual = ToStringBuilder.build(stringList).toString(); 
//    assert actual.equals(EXPECTED_STRINGARRAY_TOSTRING)  
//      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_STRINGARRAY_TOSTRING);
//    
//    Map<String, String> stringMap = new HashMap<String, String>();
//    stringMap.put("Finland", "the land of a thousand lakes");
//    stringMap.put("Norway",  "the land of the midnight sun");
//    actual = ToStringBuilder.build(stringMap).toString(); 
//    assert actual.equals(EXPECTED_STRINGMAP_TOSTRING)  
//      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_STRINGMAP_TOSTRING);
//
//    SimpleBean simpleBean = createSimpleBean();
//    actual = ToStringBuilder.build(simpleBean).toString();
//    assert actual.startsWith(EXPECTED_SIMPLEBEAN_FRAGMENT)
//      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_SIMPLEBEAN_FRAGMENT);
//    
//    log.debug("\n"+actual);
//
//    NestedBean nestedBean = createNestedBean(simpleBean);
//    actual = ToStringBuilder.build(nestedBean).toString();
//    assert actual.startsWith(EXPECTED_NESTEDBEAN_FRAGMENT_1) && 
//      actual.contains(EXPECTED_NESTEDBEAN_FRAGMENT_2)
//      : String.format(ASSERT_MESSAGE_FORMAT, actual, 
//          EXPECTED_NESTEDBEAN_FRAGMENT_1 + "\n" + EXPECTED_NESTEDBEAN_FRAGMENT_2);
//    
//    log.debug("\n"+actual);
//  }

  private BeanWithPrimitives createSimpleBean() {
    BeanWithPrimitives beanWithPrimitives = new BeanWithPrimitives();
    MetaCache.set("id" ,   beanWithPrimitives, 2);
    MetaCache.set("foo",   beanWithPrimitives, 100);
    MetaCache.set("bar",   beanWithPrimitives, "Hello \"BAR\"!");
    MetaCache.set("baz",   beanWithPrimitives, "  Hello   BAZ!");
    MetaCache.set("color", beanWithPrimitives, BeanWithPrimitives.Color.RED);
    MetaCache.set("someDate", beanWithPrimitives, expectedDate);

    return beanWithPrimitives;
  }

  private BeanWithComposition createNestedBean(BeanWithPrimitives beanWithPrimitives) {
    BeanWithComposition beanWithComposition = new BeanWithComposition(99, beanWithPrimitives);
    MetaCache.set("floatValue",  beanWithComposition, 12.0F);
    int intArray[] = new int[]{1,2,3};
    MetaCache.set("intArray", beanWithComposition, intArray);
    MetaCache.set("twoDimensionalArray", beanWithComposition, new int[][]{intArray,{4,5,6},{7,8,9}});
    MetaCache.set("stringArray", beanWithComposition, new String[]{"Array", "or", "List", "of", "strings"});

    Cat cat1 = new Cat("Puss");
    Cat cat2 = new Cat("Tiger");
    MetaCache.set("says", cat2, "Roar");
    MetaCache.set("catArray",  beanWithComposition, new Cat[]{cat1, cat2});

    List<Integer> integerList = Arrays.asList(new Integer(101), new Integer(201), new Integer(301));
    MetaCache.set("integerList", beanWithComposition, integerList);

    List<String> stringList = Arrays.asList("Array", "or", "List", "of", "strings");
    MetaCache.set("stringList", beanWithComposition, stringList);

    List<Animal> animalsList = Arrays.asList(new Dog("Laika"), new Cat("Fritz"), new Bird("Pip"));
    MetaCache.set("animalList", beanWithComposition, animalsList);
    
    Map<String, String> stringMap = new HashMap<String, String>() {{
      put("Finland", "the land of a thousand lakes");
      put("Norway",  "the land of the midnight sun");
      put("Denmark", "the land of fairy-tales and mermaids");
      put("Sweden",  "one upon a time they had Volvo and Saab");
      put("Iceland", "the land of volcanoes and geysirs");
    }};
    MetaCache.set("stringMap", beanWithComposition, stringMap);
      
    Map<String, Dog> dogMap = new HashMap<String, Dog>() {{
      put("Bonzo", new Dog("Bonzo"));
      put("Fido",  new Dog("Fido"));
      put("Lady",  new Dog("Lady"));
      put("Tramp", new Dog("Tramp"));
    }};
    MetaCache.set("dogMap", beanWithComposition, dogMap);    
    
    return beanWithComposition;
  }
}
