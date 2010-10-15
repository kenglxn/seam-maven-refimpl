package no.knowit.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.knowit.testsupport.bean.Animal;
import no.knowit.testsupport.bean.BeanWithNestedClasses;
import no.knowit.testsupport.bean.Bird;
import no.knowit.testsupport.bean.Cat;
import no.knowit.testsupport.bean.Dog;
import no.knowit.testsupport.bean.BeanWithComposition;
import no.knowit.testsupport.bean.BeanWithPrimitives;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

public class ToStringBuilderTest {

  private enum Metrics {MM, CM, M, KM};

  private static Logger log = Logger.getLogger(ToStringBuilderTest.class);
  
  private static final String HEAD_AND_BODY = "{\"%s\": %s}" ;
  
  private static int INT_VALUE = 101;
  private static final String INTEGER_HEAD = "Integer" ;
  private static final String INTEGER_BODY = "{\n" +
    "  \"value\": 101\n" +
    "}" ;
  
  private static float FLOAT_VALUE = 12.0F;
  private static final String FLOAT_HEAD = "Float" ;
  private static final String FLOAT_BODY = "{\n" +
    "  \"value\": 12.0\n" +
    "}" ;
  
  private static final String STRING_VALUE = "A string value";
  private static final String STRING_HEAD = "String" ;
  private static final String STRING_BODY = "{\n" +
    "  \"count\": 14,\n" +
    "  \"hash\": 0,\n" +
    "  \"value\": [A,  , s, t, r, i, n, g,  , v, a, l, u, e],\n" +
    "  \"offset\": 0\n" +
    "}";
  
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
  private static final String DATE_STRING = "2010-04-11 15:11:28 +0200";
  private static final String DATE_HEAD = "Date" ;
  private static final String DATE_BODY = "{\n" +
    "  \"fastTime\": 1270991488000,\n" +
    "  \"cdate\": null\n" +
    "}";

  private static final String METRICS_HEAD = "Metrics";
  private static final String METRICS_BODY = "{\n" +
    "  \"name\": \"KM\",\n" +
    "  \"ordinal\": 3\n" +
    "}" ;

  private static final int[] INT_ARRAY =  new int[]{1, 2, 3};  
  private static final String INT_ARRAY_HEAD = "int[]" ;
  private static final String INT_ARRAY_BODY = "[1, 2, 3]";
  
  private static final int[][] TWO_DIMENSIONAL_INT_ARRAY =  new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
  private static final String TWO_DIMENSIONAL_INT_ARRAY_HEAD = "int[][]" ;
  private static final String TWO_DIMENSIONAL_INT_ARRAY_BODY = "[\n" +
    "  [1, 2, 3], \n" +
    "  [4, 5, 6], \n" +
    "  [7, 8, 9]]";

  private static final String[] STRING_ARRAY = new String[]{"Array", "or", "List", "of", "strings"};
  private static final String STRING_ARRAY_HEAD = "String[]";
  private static final String STRING_ARRAY_BODY = 
    "[\"Array\", \"or\", \"List\", \"of\", \"strings\"]";
  
  private static final String CAT_ARRAY_HEAD = "Cat[]" ;
  private static final String CAT_ARRAY_BODY = "[\n" +
    "  \"Cat\": {\n" +
    "    \"name\": \"Puss\",\n" +
    "    \"says\": \"Purr\"\n" +
    "  }, \n" +
    "  \"Cat\": {\n" +
    "    \"name\": \"Tiger\",\n" +
    "    \"says\": \"Roar\"\n" +
    "  }]";

  private static List<Integer> INTEGER_ARRAY_LIST = Arrays.asList(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
  private static final String INTEGER_ARRAY_LIST_HEAD = "ArrayList" ;
  private static final String INTEGER_ARRAY_LIST_BODY = INT_ARRAY_BODY;
  
  private static final List<String> STRING_LIST = new ArrayList<String>(Arrays.asList(STRING_ARRAY));
  private static final String STRING_LIST_HEAD = "ArrayList";
  private static final String STRING_LIST_BODY = STRING_ARRAY_BODY;

  private static final List<Animal> ANIMAL_LIST = Arrays.asList(new Dog("Laika"), new Cat("Fritz"), new Bird("Pip"));
  private static final String ANIMAL_LIST_HEAD = "ArrayList" ;
  private static final String ANIMAL_LIST_BODY = "[\n" +
    "  \"Dog\": {\n" +
    "    \"name\": \"Laika\",\n" +
    "    \"says\": \"Bark\"\n" +
    "  }, \n" +
    "  \"Cat\": {\n" +
    "    \"name\": \"Fritz\",\n" +
    "    \"says\": \"Purr\"\n" +
    "  }, \n" +
    "  \"Bird\": {\n" +
    "    \"name\": \"Pip\",\n" +
    "    \"says\": \"Tweet\"\n" +
    "  }]";

  @SuppressWarnings("serial")
  private static final Map<String, String> STRING_MAP = new HashMap<String, String>() {{
    put("Finland", "the land of a thousand lakes");
    put("Norway", "the land of the midnight sun");
    put("Denmark", "the land of fairy-tales and mermaids");
    put("Sweden", "one upon a time they had Volvo and Saab");
    put("Iceland", "the land of volcanoes and geysirs");
  }};
  private static final String STRING_MAP_HEAD_ALIAS = "Map" ;
  private static final String STRING_MAP_BODY = "{\n" +
  "  \"Finland\": \"the land of a thousand lakes\",\n" +
  "  \"Denmark\": \"the land of fairy-tales and mermaids\",\n" +
  "  \"Iceland\": \"the land of volcanoes and geysirs\",\n" +
  "  \"Norway\": \"the land of the midnight sun\",\n" +
  "  \"Sweden\": \"one upon a time they had Volvo and Saab\"\n" +
  "}" ;
  
  @SuppressWarnings("serial")
  private static final Map<String, Dog> DOG_MAP = new HashMap<String, Dog>() {{
    put("Bonzo", new Dog("Bonzo"));
    put("Fido", new Dog("Fido"));
    put("Lady", new Dog("Lady"));
    put("Tramp", new Dog("Tramp"));
  }};
  private static final String DOG_MAP_HEAD_ALIAS = "dogs4all" ;
  private static final String DOG_MAP_BODY = "{\n" +
  "  \"Tramp\": \"Dog\": {\n" +
  "    \"name\": \"Tramp\",\n" +
  "    \"says\": \"Bark\"\n" +
  "  },\n" +
  "  \"Fido\": \"Dog\": {\n" +
  "    \"name\": \"Fido\",\n" +
  "    \"says\": \"Bark\"\n" +
  "  },\n" +
  "  \"Lady\": \"Dog\": {\n" +
  "    \"name\": \"Lady\",\n" +
  "    \"says\": \"Bark\"\n" +
  "  },\n" +
  "  \"Bonzo\": \"Dog\": {\n" +
  "    \"name\": \"Bonzo\",\n" +
  "    \"says\": \"Bark\"\n" +
  "  }\n" +
  "}" ;

  
  private static final String EXPECTED_SIMPLEBEAN_FRAGMENT =
    "{\"BeanWithPrimitives\": {\n" +
      "  \"id\": 2,\n" +
      "  \"baz\": \"  Hello   BAZ!\",\n" +
      "  \"color\": RED,\n" +
      "  \"finalField\": \"A final field\",\n" +
      "  \"foo\": 200,\n" +
      "  \"bar\": \"setBar -> Hello \\\"BAR\\\"!\",\n" +
      "  \"someDate\": 2010-04-11 15:11:28 +0200\n}}";

  private static final String EXPECTED_NESTEDBEAN_FRAGMENT_1 = "{\"BeanWithComposition\": {";

  private static final String EXPECTED_NESTEDBEAN_FRAGMENT_2 =
    "  \"id\": 99,\n" +
    "  \"animalList\": [\n" +
    "    \"Dog\": {\n" +
    "      \"name\": \"Laika\",\n" +
    "      \"says\": \"Bark\"\n" +
    "    }, \n" +
    "    \"Cat\": {\n" +
    "      \"name\": \"Fritz\",\n" +
    "      \"says\": \"Purr\"\n" +
    "    }, \n" +
    "    \"Bird\": {\n" +
    "      \"name\": \"Pip\",\n" +
    "      \"says\": \"Tweet\"\n" +
    "    }],\n" +
    "  \"stringMap\": {\n" +
    "    \"Finland\": \"the land of a thousand lakes\",\n" +
    "    \"Denmark\": \"the land of fairy-tales and mermaids\",\n" +
    "    \"Iceland\": \"the land of volcanoes and geysirs\",\n" +
    "    \"Norway\": \"the land of the midnight sun\",\n" +
    "    \"Sweden\": \"one upon a time they had Volvo and Saab\"\n" +
    "  },\n" +
    "  \"intArray\": [1, 2, 3],\n" +
    "  \"stringArray\": [\"Array\", \"or\", \"List\", \"of\", \"strings\"],\n" +
    "  \"twoDimensionalArray\": [\n" +
    "    [1, 2, 3], \n" +
    "    [4, 5, 6], \n" +
    "    [7, 8, 9]],\n" +
    "  \"stringList\": [\"Array\", \"or\", \"List\", \"of\", \"strings\"],\n" +
    "  \"integerList\": [101, 201, 301],\n" +
    "  \"catArray\": [\n" +
    "    \"Cat\": {\n" +
    "      \"name\": \"Puss\",\n" +
    "      \"says\": \"Purr\"\n" +
    "    }, \n" +
    "    \"Cat\": {\n" +
    "      \"name\": \"Tiger\",\n" +
    "      \"says\": \"Roar\"\n" +
    "    }],\n" +
    "  \"floatValue\": 12.0,\n" +
    "  \"dogMap\": {\n" +
    "    \"Tramp\": \"Dog\": {\n" +
    "      \"name\": \"Tramp\",\n" +
    "      \"says\": \"Bark\"\n" +
    "    },\n" +
    "    \"Fido\": \"Dog\": {\n" +
    "      \"name\": \"Fido\",\n" +
    "      \"says\": \"Bark\"\n" +
    "    },\n" +
    "    \"Lady\": \"Dog\": {\n" +
    "      \"name\": \"Lady\",\n" +
    "      \"says\": \"Bark\"\n" +
    "    },\n" +
    "    \"Bonzo\": \"Dog\": {\n" +
    "      \"name\": \"Bonzo\",\n" +
    "      \"says\": \"Bark\"\n" +
    "    }\n" +
    "  },\n" +
    "  \"beanWithPrimitives\": \"BeanWithPrimitives\": {\n" +
    "    \"id\": 2,\n" +
    "    \"baz\": \"  Hello   BAZ!\",\n" +
    "    \"color\": RED,\n" +
    "    \"finalField\": \"A final field\",\n" +
    "    \"foo\": 200,\n" +
    "    \"bar\": \"setBar -> Hello \\\"BAR\\\"!\",\n" +
    "    \"someDate\": 2010-04-11 15:11:28 +0200\n" +
    "  }\n}}";
  
  private static final String ASSERT_MESSAGE_FORMAT = "Actual: [%s]. Expected: [%s].";


  @Test
  public void primitivesToString() {
    String actual;
    String expected;
    
    // Given
    Integer intValue = Integer.valueOf(INT_VALUE);

    // When
    actual = ToStringBuilder.builder(intValue).toString();
    
    // Then
    expected = String.format(HEAD_AND_BODY, INTEGER_HEAD, INTEGER_BODY);
    assert actual.equals(expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
    
    // Given
    Float floatValue = FLOAT_VALUE;
    
    // When
    actual = ToStringBuilder.builder(floatValue).toString();
    
    // Then
    expected = String.format(HEAD_AND_BODY, FLOAT_HEAD, FLOAT_BODY);
    assert actual.equals(expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);

    // Given
    String stringValue = STRING_VALUE;
    
    // When
    actual = ToStringBuilder.builder(stringValue).toString();
    
    // Then
    expected = String.format(HEAD_AND_BODY, STRING_HEAD, STRING_BODY);
    assert actual.equals(expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
    
    // Given
    Date date = null;
    try {
      date = DATE_FORMAT.parse(DATE_STRING);
    } 
    catch (ParseException e) {}

    // When
    actual = ToStringBuilder.builder(date).toString();
    //log.debug("******\n" + actual);
    
    // Then
    expected = String.format(HEAD_AND_BODY, DATE_HEAD, DATE_BODY);
    assert actual.equals(expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
    
    
    // Given
    Metrics metrics = Metrics.KM;
    
    // When
    actual = ToStringBuilder.builder(metrics).toString();

    // Then
    expected = String.format(HEAD_AND_BODY, METRICS_HEAD, METRICS_BODY);
    assert actual.equals(expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
}

  @Test
  public void arraysToString() {
    String actual;
    String expected;
    
    // Given
    int[] intArray = Arrays.copyOf(INT_ARRAY, INT_ARRAY.length);
    
    // When
    actual = ToStringBuilder.builder(intArray).toString();
    
    // Then
    expected = String.format(HEAD_AND_BODY, INT_ARRAY_HEAD, INT_ARRAY_BODY);
    assert actual.equals(expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);

    // Given
    int[][] twoDimensionalIntArray =  TWO_DIMENSIONAL_INT_ARRAY.clone();

    // When
    actual = ToStringBuilder.builder(twoDimensionalIntArray).toString();
    
    // Then
    expected = String.format(HEAD_AND_BODY, TWO_DIMENSIONAL_INT_ARRAY_HEAD, TWO_DIMENSIONAL_INT_ARRAY_BODY);
    assert actual.equals(expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
    
    // Given
    String[] stringArray = new String[STRING_ARRAY.length];
    System.arraycopy(STRING_ARRAY, 0, stringArray, 0, STRING_ARRAY.length);
    
    // When
    actual = ToStringBuilder.builder(stringArray).toString();
    
    // Then
    expected = String.format(HEAD_AND_BODY, STRING_ARRAY_HEAD, STRING_ARRAY_BODY);
    assert actual.equals(expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
    
    // Given
    Cat puss = new Cat("Puss");
    Cat tiger = new Cat("Tiger");
    MetaCache.set("says", tiger, "Roar");
    Cat[] catArray = new Cat[]{puss, tiger};
    
    // When
    actual = ToStringBuilder.builder(catArray).toString();
    
    // Then
    expected = String.format(HEAD_AND_BODY, CAT_ARRAY_HEAD, CAT_ARRAY_BODY);
    assert actual.equals(expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
  }
  
  @Test
  public void collectionsToString() {
    
    String actual;
    String expected;

    // Given
    List<Integer> arrayList = INTEGER_ARRAY_LIST;

    // When
    actual = ToStringBuilder.builder(arrayList).toString();

    // Then
    expected = String.format(HEAD_AND_BODY, INTEGER_ARRAY_LIST_HEAD, INTEGER_ARRAY_LIST_BODY);
    assert actual.equals(expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
    
    // Given
    List<String> stringList = STRING_LIST;

    // When
    actual = ToStringBuilder.builder(stringList).toString();
    //log.debug("******\n" + actual);

    // Then
    expected = String.format(HEAD_AND_BODY, STRING_LIST_HEAD, STRING_LIST_BODY);
    assert actual.equals(expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);

    // Given
    Map<String, String> stringMap = STRING_MAP;

    // When
    // Initialization of map uses the static initializer which gives us an anonymous class instance
    // Uses rootNodeAlias("Map") to name the Map instance
    actual = ToStringBuilder.builder(stringMap).rootNodeAlias("Map").toString();

    // Then
    expected = String.format(HEAD_AND_BODY, STRING_MAP_HEAD_ALIAS, STRING_MAP_BODY);
    assert actual.equals(expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);

    // Given
    List<Animal> animalList = ANIMAL_LIST;
    
    // When
    actual = ToStringBuilder.builder(animalList).toString();

    // Then
    expected = String.format(HEAD_AND_BODY, ANIMAL_LIST_HEAD, ANIMAL_LIST_BODY);
    assert actual.equals(expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
    
    // Given
    Map<String, Dog> dogMap = DOG_MAP;
    
    // When
    // Initialization of map uses the static initializer which gives us an anonymous class instance
    // Uses rootNodeAlias("dogs4all") to name the Map instance
    actual = ToStringBuilder.builder(dogMap).rootNodeAlias("dogs4all").toString();
    
    // Then
    expected = String.format(HEAD_AND_BODY, DOG_MAP_HEAD_ALIAS, DOG_MAP_BODY);
    assert actual.equals(expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
    
    // Given
    // When
    //log.debug("******\n" + actual);
    // Then
    
  }
  
  @Test
  public void beansToString() {

    String actual;
    
    BeanWithPrimitives beanWithPrimitives = createBeanWithPrimitives();
    actual = ToStringBuilder.builder(beanWithPrimitives).toString();
    assert actual.startsWith(EXPECTED_SIMPLEBEAN_FRAGMENT)
      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_SIMPLEBEAN_FRAGMENT);

    log.debug("\n" + actual);

    BeanWithComposition nestedBean = createBeanWithComposition(beanWithPrimitives);
    actual = ToStringBuilder.builder(nestedBean).toString();
    assert actual.startsWith(EXPECTED_NESTEDBEAN_FRAGMENT_1) &&
      actual.contains(EXPECTED_NESTEDBEAN_FRAGMENT_2)
      : String.format(ASSERT_MESSAGE_FORMAT, actual,
      EXPECTED_NESTEDBEAN_FRAGMENT_1 + "\n" + EXPECTED_NESTEDBEAN_FRAGMENT_2);
    log.debug("\n" + actual);
  }

  @Test
  public void should() throws Exception {
    // Sandbox, no real tests here
    
    String actual;
    
    actual = ToStringBuilder.builder(createBeanWithPrimitives())
      .publicFieldsOnly()
      .flatten()
      .indentation(2)
      .fieldNameFormatter(new ToStringBuilder.FieldNameFormatter() {
        @Override
        public String format(Object owner, String name) {
          return name + "->";
        }
      })
      .fieldValueFormatter(new ToStringBuilder.FieldValueFormatter() {
        @Override
        public String format(final Object owner, final Object value) {
          return "@" + value.toString();
        }
      })
      .toString();
    log.debug("\n" + actual);

    BeanWithComposition beanWithComposition = createBeanWithComposition(createBeanWithPrimitives());
    actual = ToStringBuilder
      .builder(beanWithComposition)
      .publicFieldsOnly()
      .toString();
    log.debug("\n" + actual);

    actual = ToStringBuilder
      .builder(createBeanWithPrimitives())
      .indentation(1)
      .withField("id")
      .withField("color")
      .withField("baz")
      .toString();
    log.debug("\n" + actual);

    actual = ToStringBuilder.builder(new BeanWithNestedClasses(100))
      .toString();
    log.debug("\n" + actual);
    
  }
  
  private BeanWithPrimitives createBeanWithPrimitives() {
    BeanWithPrimitives beanWithPrimitives = new BeanWithPrimitives();
    MetaCache.set("id", beanWithPrimitives, 2);
    MetaCache.set("foo", beanWithPrimitives, 100);
    MetaCache.set("bar", beanWithPrimitives, "Hello \"BAR\"!");
    MetaCache.set("baz", beanWithPrimitives, "  Hello   BAZ!");
    MetaCache.set("color", beanWithPrimitives, BeanWithPrimitives.Color.RED);
    
    try {
      Date date = DATE_FORMAT.parse(DATE_STRING);
      MetaCache.set("someDate", beanWithPrimitives, date);
    } 
    catch (ParseException e) {}

    return beanWithPrimitives;
  }

  private BeanWithComposition createBeanWithComposition(BeanWithPrimitives beanWithPrimitives) {
    BeanWithComposition beanWithComposition = new BeanWithComposition(99, beanWithPrimitives);
    MetaCache.set("floatValue", beanWithComposition, 12.0F);
    int intArray[] = new int[]{1, 2, 3};
    MetaCache.set("intArray", beanWithComposition, intArray);
    MetaCache.set("twoDimensionalArray", beanWithComposition, new int[][]{intArray, {4, 5, 6}, {7, 8, 9}});
    MetaCache.set("stringArray", beanWithComposition, new String[]{"Array", "or", "List", "of", "strings"});

    Cat cat1 = new Cat("Puss");
    Cat cat2 = new Cat("Tiger");
    MetaCache.set("says", cat2, "Roar");
    MetaCache.set("catArray", beanWithComposition, new Cat[]{cat1, cat2});

    List<Integer> integerList = Arrays.asList(new Integer(101), new Integer(201), new Integer(301));
    MetaCache.set("integerList", beanWithComposition, integerList);

    List<String> stringList = Arrays.asList("Array", "or", "List", "of", "strings");
    MetaCache.set("stringList", beanWithComposition, stringList);

    List<Animal> animalsList = Arrays.asList(new Dog("Laika"), new Cat("Fritz"), new Bird("Pip"));
    MetaCache.set("animalList", beanWithComposition, animalsList);

    @SuppressWarnings("serial")
    Map<String, String> stringMap = new HashMap<String, String>() {{
      put("Finland", "the land of a thousand lakes");
      put("Norway", "the land of the midnight sun");
      put("Denmark", "the land of fairy-tales and mermaids");
      put("Sweden", "one upon a time they had Volvo and Saab");
      put("Iceland", "the land of volcanoes and geysirs");
    }};
    MetaCache.set("stringMap", beanWithComposition, stringMap);

    @SuppressWarnings("serial")
    Map<String, Dog> dogMap = new HashMap<String, Dog>() {{
      put("Bonzo", new Dog("Bonzo"));
      put("Fido", new Dog("Fido"));
      put("Lady", new Dog("Lady"));
      put("Tramp", new Dog("Tramp"));
    }};
    MetaCache.set("dogMap", beanWithComposition, dogMap);

    return beanWithComposition;
  }
}
