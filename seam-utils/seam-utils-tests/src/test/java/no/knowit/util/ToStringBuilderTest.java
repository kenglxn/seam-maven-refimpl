package no.knowit.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.knowit.testsupport.bean.Animal;
import no.knowit.testsupport.bean.BeanWithNestedClasses;
import no.knowit.testsupport.bean.Bird;
import no.knowit.testsupport.bean.Cat;
import no.knowit.testsupport.bean.Dog;
import no.knowit.testsupport.bean.BeanWithComposition;
import no.knowit.testsupport.bean.BeanWithPrimitives;
import no.knowit.testsupport.bean.Mouse;
import no.knowit.testsupport.bean.MyAnnotation;
import no.knowit.testsupport.bean.PetOwner;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

public class ToStringBuilderTest {

  private enum Metrics {
    MM("Millimetres"), CM("Centimetres"), M("Metres"), KM("Kilometres");
    private String description;
    Metrics(String description) { this.description = description; }
    @SuppressWarnings("unused")
    public String getDescription() { return description; }
  };

  private static Logger log = Logger.getLogger(ToStringBuilderTest.class);
  
  private static final String HEAD_BODY_TAIL = "{\"%s\": %s}" ;
  
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
  
  private static final Date DATE_NOW = new Date(); 
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
  private static final String DATE_STRING = DATE_FORMAT.format(DATE_NOW); //e.g. "2010-04-11 15:11:28 +0200";
  private static final String DATE_HEAD = "Date" ;
  private static final String DATE_BODY_FRAGMENT_1 =  "\"fastTime\":";
  private static final String DATE_BODY_FRAGMENT_2 =  "\"cdate\":";
  private static final String EXPECTED_DATE_NOW = 
    "{\"Date\":{\n" +
    "  \"fastTime\": someNumber,\n" +
    "  \"cdate\": someObject\n" +
    "}}";

  private static final String METRICS_HEAD = "Metrics";
  private static final String METRICS_BODY = "{\n" +
    "  \"description\": \"Kilometres\",\n" +
    "  \"name\": \"KM\",\n" +
    "  \"ordinal\": 3\n" +
    "}" ;

  private static final int[] INT_ARRAY =  new int[]{1, 2, 3};  
  private static final String INT_ARRAY_HEAD = "int[]" ;
  private static final String INT_ARRAY_BODY = "[1, 2, 3]";
  
  private static final int[][] TWO_DIMENSIONAL_INT_ARRAY =  new int[][]{
    {1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
  private static final String TWO_DIMENSIONAL_INT_ARRAY_HEAD = "int[][]" ;
  private static final String TWO_DIMENSIONAL_INT_ARRAY_BODY = "[\n" +
    "  [1, 2, 3],\n" +
    "  [4, 5, 6],\n" +
    "  [7, 8, 9]]";

  private static final String[] STRING_ARRAY = new String[]{"Array", "or", "List", "of", "strings"};
  private static final String STRING_ARRAY_HEAD = "String[]";
  private static final String STRING_ARRAY_BODY = 
    "[\"Array\", \"or\", \"List\", \"of\", \"strings\"]";
  
  private static final String CAT_ARRAY_HEAD = "Cat[]" ;
  private static final String CAT_ARRAY_BODY = "[\n" +
    "  \"Cat\": {\n" +
    "    \"name\": \"Puss\",\n" +
    "    \"says\": \"Purr\",\n" +
    "    \"teddy\": \"TeddyBear\": {\n" +
    "      \"name\": \"Pooky\"\n" +
    "     }\n" +
    "  }, \n" +
    "  \"Cat\": {\n" +
    "    \"name\": \"Tiger\",\n" +
    "    \"says\": \"Roar\",\n" +
    "    \"teddy\": \"TeddyBear\": {\n" +
    "      \"name\": \"Pooky\"\n" +
    "     }\n" +
    "  }]";

  private static List<Integer> INTEGER_ARRAY_LIST = Arrays.asList(
      Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3));
  private static final String INTEGER_ARRAY_LIST_HEAD = "ArrayList" ;
  private static final String INTEGER_ARRAY_LIST_BODY = INT_ARRAY_BODY;
  
  private static final List<String> STRING_LIST = new ArrayList<String>(Arrays.asList(STRING_ARRAY));
  private static final String STRING_LIST_HEAD = "ArrayList";
  private static final String STRING_LIST_BODY = STRING_ARRAY_BODY;

  private static final List<Animal> ANIMAL_LIST = Arrays.asList(
      new Dog("Laika"), new Cat("Fritz"), new Bird("Pip"));
  private static final String ANIMAL_LIST_HEAD = "ArrayList" ;
  private static final String ANIMAL_LIST_BODY = "[\n" +
    "  \"Dog\": {\n" +
    "    \"name\": \"Laika\",\n" +
    "    \"says\": \"Bark\"\n" +
    "  }, \n" +
    "  \"Cat\": {\n" +
    "    \"name\": \"Fritz\",\n" +
    "    \"says\": \"Purr\",\n" +
    "    \"teddy\": \"TeddyBear\": {\n" +
    "      \"name\": \"Pooky\"\n" +
    "     }\n" +
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

  private static final String EXPECTED_BEAN_WITH_PRIMITIVES =
    "{\"BeanWithPrimitives\": {\n" +
      "  \"id\": 2,\n" +
      "  \"baz\": \"  Hello   BAZ!\",\n" +
      "  \"color\": RED,\n" +
      "  \"finalField\": \"A final field\",\n" +
      "  \"foo\": 200,\n" +
      "  \"bar\": \"setBar -> Hello \\\"BAR\\\"!\",\n" +
      "  \"someDate\": " + DATE_STRING + "\n" +
      "}}";
  
  private static final String EXPECTED_BEAN_WITH_PRIMITIVES_PUBLIC_FIELDS =
    "{\"BeanWithPrimitives\": {\n" +
      "  \"baz\": \"  Hello   BAZ!\",\n" +
      "  \"color\": RED,\n" +
      "  \"foo\": 200,\n" +
      "  \"someDate\": " + DATE_STRING + "\n" +
      "}}";

  private static final String EXPECTED_COMPOSED_BEAN =
    "{\"BeanWithComposition\": {\n" +
    "  \"id\": 99,\n" +
    "  \"animalList\": [\n" +
    "    \"Dog\": {\n" +
    "      \"name\": \"Laika\",\n" +
    "      \"says\": \"Bark\"\n" +
    "    }, \n" +
    "    \"Cat\": {\n" +
    "      \"name\": \"Fritz\",\n" +
    "      \"says\": \"Purr\",\n" +
    "      \"teddy\": \"TeddyBear\": {\n" +
    "        \"name\": \"Pooky\"\n" +
    "       }\n" +
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
    "    [1, 2, 3],\n" +
    "    [4, 5, 6],\n" +
    "    [7, 8, 9]],\n" +
    "  \"stringList\": [\"Array\", \"or\", \"List\", \"of\", \"strings\"],\n" +
    "  \"integerList\": [101, 201, 301],\n" +
    "  \"catArray\": [\n" +
    "    \"Cat\": {\n" +
    "      \"name\": \"Puss\",\n" +
    "      \"says\": \"Purr\",\n" +
    "      \"teddy\": \"TeddyBear\": {\n" +
    "        \"name\": \"Pooky\"\n" +
    "       }\n" +
    "    }, \n" +
    "    \"Cat\": {\n" +
    "      \"name\": \"Tiger\",\n" +
    "      \"says\": \"Roar\",\n" +
    "      \"teddy\": \"TeddyBear\": {\n" +
    "        \"name\": \"Pooky\"\n" +
    "       }\n" +
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
    "    \"someDate\": " + DATE_STRING + "\n" +
    "  }\n}}";
  
  private static final String EXPECTED_COMPOSED_BEAN_WITH_INCLUDED_FIELDS =
    "  {\"BeanWithComposition\": {\n" +
    "    \"id\": 99,\n" +
    "    \"dogMap\": {\n" +
    "      \"Tramp\": \"Dog\": {\n" +
    "        \"name\": \"Tramp\"\n" +
    "      },\n" +
    "      \"Fido\": \"Dog\": {\n" +
    "        \"name\": \"Fido\"\n" +
    "      },\n" +
    "      \"Lady\": \"Dog\": {\n" +
    "        \"name\": \"Lady\"\n" +
    "      },\n" +
    "      \"Bonzo\": \"Dog\": {\n" +
    "        \"name\": \"Bonzo\"\n" +
    "      }\n" +
    "    },\n" +
    "    \"beanWithPrimitives\": \"BeanWithPrimitives\": {\n" +
    "      \"id\": 2,\n" +
    "      \"color\": RED\n" +
    "    }\n" +
    "  }}";
  
  private static final String EXPECTED_COMPOSED_BEAN_WITH_EXCLUDED_FIELDS = 
    EXPECTED_COMPOSED_BEAN_WITH_INCLUDED_FIELDS;
    
  private static final String EXPECTED_BEAN_WITH_NESTED_CLASSES =
    "  {\"BeanWithNestedClasses\": {\n" +
    "    \"nestedEnum\": YELLOW,\n" +
    "    \"innerClass\": \"InnerClass\": {\n" +
    "      \"i\": 110\n" +
    "    },\n" +
    "    \"staticNestedClass\": \"StaticNestedClass\": {\n" +
    "      \"i\": 120\n" +
    "    },\n" +
    "    \"i\": 100\n" +
    "  }}";
  
  private static final String EXPECTED_BEAN_WITH_PRIMITIVES_INCLUDED_ANNTATIONS =
    "{\"BeanWithPrimitives\": {\n" +
      "  \"baz\": \"  Hello   BAZ!\",\n" +
      "  \"color\": RED,\n" +
      "  \"foo\": 200\n" +
      "}}";

  private static final String EXPECTED_COMPOSED_BEAN_INCLUDED_ANNOTATIONS =
    "{\"BeanWithComposition\": {\n" +
    "  \"id\": 99,\n" +
    "  \"animalList\": [\n" +
    "    \"Dog\": {\n" +
    "      \"name\": \"Laika\"\n" +
    "    }, \n" +
    "    \"Cat\": {\n" +
    "      \"name\": \"Fritz\"\n" +
    "    }, \n" +
    "    \"Bird\": {\n" +
    "      \"name\": \"Pip\"\n" +
    "    }]\n" +
    "}}";
  
  private static final String EXPECTED_RECURSION_LEVEL_1 =
    "{\"PetOwner\": {\n" +
    "  \"pets\": {\n" +
    "    \"Odie\": \"Dog\": no.knowit.testsupport.bean.Dog@abcdef,\n" +
    "    \"Garfield\": \"Cat\": no.knowit.testsupport.bean.Cat@affafa,\n" +
    "    \"Squeak\": \"Mouse\": no.knowit.testsupport.bean.Mouse@doesnotmatterwhatsafter@\n" +
    "  },\n" +
    "  \"name\": \"Jon Arbuckle\"\n" +
    "}}";

  private static final String EXPECTED_RECURSION_LEVEL_1_FRAGMENT_1 =
    "\"Odie\": \"Dog\": no.knowit.testsupport.bean.Dog@";
  
  private static final String EXPECTED_RECURSION_LEVEL_1_FRAGMENT_2 =
    "\"Garfield\": \"Cat\": no.knowit.testsupport.bean.Cat@";
  
  private static final String EXPECTED_RECURSION_LEVEL_1_FRAGMENT_3 =
    "\"Squeak\": \"Mouse\": no.knowit.testsupport.bean.Mouse@";
  
  private static final String EXPECTED_RECURSION_LEVEL_2 =
    "{\"PetOwner\": {\n" +
    "  \"pets\": {\n" +
    "    \"Odie\": \"Dog\": {\n" +
    "      \"name\": \"Odie\",\n" +
    "      \"says\": \"Bark\"\n" +
    "    },\n" +
    "    \"Garfield\": \"Cat\": {\n" +
    "      \"name\": \"Garfield\",\n" +
    "      \"says\": \"Purr\",\n" +
    "      \"teddy\": \"TeddyBear\": no.knowit.testsupport.bean.Cat$TeddyBear@\n" +
    "    },\n" +
    "    \"Squeak\": \"Mouse\": {\n" +
    "      \"name\": \"Squeak\",\n" +
    "      \"says\": \"Peep\"\n" +
    "    }\n" +
    "  },\n" +
    "  \"name\": \"Jon Arbuckle\"\n" +
    "}}";
  
  private static final String EXPECTED_RECURSION_LEVEL_2_FRAGMENT =
    "\"teddy\": \"TeddyBear\": no.knowit.testsupport.bean.Cat$TeddyBear@";
    
    
  private static final String EXPECTED_RECURSION_LEVEL_3 =
    "{\"PetOwner\": {\n" +
    "  \"pets\": {\n" +
    "    \"Odie\": \"Dog\": {\n" +
    "      \"name\": \"Odie\",\n" +
    "      \"says\": \"Bark\"\n" +
    "    },\n" +
    "    \"Garfield\": \"Cat\": {\n" +
    "      \"name\": \"Garfield\",\n" +
    "      \"says\": \"Purr\",\n" +
    "      \"teddy\": \"TeddyBear\": {\n" +
    "        \"name\": \"Pooky\"\n" +
    "      }\n" +
    "    },\n" +
    "    \"Squeak\": \"Mouse\": {\n" +
    "      \"name\": \"Squeak\",\n" +
    "      \"says\": \"Peep\"\n" +
    "    }\n" +
    "  },\n" +
    "  \"name\": \"Jon Arbuckle\"\n" +
    "}}";
  
  private static final String ASSERT_MESSAGE_FORMAT = 
    "Actual result was not expected! Actual: [\n%s\n]. Expected: [\n%s\n]";


  @Test
  public void primitivesToString() {
    String actual;
    String expected;
    
    // Given
    Integer intValue = Integer.valueOf(INT_VALUE);

    // When
    actual = ToStringBuilder.builder(intValue).toString();
    
    // Then
    expected = String.format(HEAD_BODY_TAIL, INTEGER_HEAD, INTEGER_BODY);
    assert actualInExpected(actual, expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
    
    
    // Given
    Float floatValue = FLOAT_VALUE;
    
    // When
    actual = ToStringBuilder.builder(floatValue).toString();
    
    // Then
    expected = String.format(HEAD_BODY_TAIL, FLOAT_HEAD, FLOAT_BODY);
    assert actualInExpected(actual, expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);

    
    // Given
    String stringValue = STRING_VALUE;
    
    // When
    actual = ToStringBuilder.builder(stringValue).toString();
    
    // Then
    expected = String.format(HEAD_BODY_TAIL, STRING_HEAD, STRING_BODY);
    assert actualInExpected(actual, expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
    
    
    // Given
    Date date = DATE_NOW;

    // When
    actual = ToStringBuilder.builder(date).toString();
    //log.debug("******\n" + actual);
    
    // Then
    assert actual.contains(DATE_HEAD) 
      && actual.contains(DATE_BODY_FRAGMENT_1) 
      && actual.contains(DATE_BODY_FRAGMENT_2)
        : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_DATE_NOW);
    
    
    // Given
    Metrics metrics = Metrics.KM;
    
    // When
    actual = ToStringBuilder.builder(metrics).toString();
    //log.debug("******\n" + actual);

    // Then
    expected = String.format(HEAD_BODY_TAIL, METRICS_HEAD, METRICS_BODY);
    assert actualInExpected(actual, expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
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
    expected = String.format(HEAD_BODY_TAIL, INT_ARRAY_HEAD, INT_ARRAY_BODY);
    assert actualInExpected(actual, expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);

    
    // Given
    int[][] twoDimensionalIntArray =  TWO_DIMENSIONAL_INT_ARRAY.clone();

    // When
    actual = ToStringBuilder.builder(twoDimensionalIntArray).toString();
    
    // Then
    expected = String.format(HEAD_BODY_TAIL, TWO_DIMENSIONAL_INT_ARRAY_HEAD, TWO_DIMENSIONAL_INT_ARRAY_BODY);
    assert actualInExpected(actual, expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
    
    
    // Given
    String[] stringArray = new String[STRING_ARRAY.length];
    System.arraycopy(STRING_ARRAY, 0, stringArray, 0, STRING_ARRAY.length);
    
    // When
    actual = ToStringBuilder.builder(stringArray).toString();
    
    // Then
    expected = String.format(HEAD_BODY_TAIL, STRING_ARRAY_HEAD, STRING_ARRAY_BODY);
    assert actualInExpected(actual, expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
    
    
    // Given
    Cat puss = new Cat("Puss");
    Cat tiger = new Cat("Tiger");
    MetaCache.set("says", tiger, "Roar");
    Cat[] catArray = new Cat[]{puss, tiger};
    
    // When
    actual = ToStringBuilder.builder(catArray).toString();
    //log.debug("******\n" + actual);
    
    // Then
    expected = String.format(HEAD_BODY_TAIL, CAT_ARRAY_HEAD, CAT_ARRAY_BODY);
    assert actualInExpected(actual, expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
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
    expected = String.format(HEAD_BODY_TAIL, INTEGER_ARRAY_LIST_HEAD, INTEGER_ARRAY_LIST_BODY);
    assert actualInExpected(actual, expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
    
    
    // Given
    List<String> stringList = STRING_LIST;

    // When
    actual = ToStringBuilder.builder(stringList).toString();

    // Then
    expected = String.format(HEAD_BODY_TAIL, STRING_LIST_HEAD, STRING_LIST_BODY);
    assert actualInExpected(actual, expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);

    
    // Given
    Map<String, String> stringMap = STRING_MAP;

    // When
    // Initialization of map uses the static initializer which gives us an anonymous class instance
    // Uses rootNodeAlias("Map") to name the Map instance
    actual = ToStringBuilder.builder(stringMap).rootNodeAlias("Map").toString();

    // Then
    expected = String.format(HEAD_BODY_TAIL, STRING_MAP_HEAD_ALIAS, STRING_MAP_BODY);
    assert actualInExpected(actual, expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);

    
    // Given
    List<Animal> animalList = ANIMAL_LIST;
    
    // When
    actual = ToStringBuilder.builder(animalList).toString();

    // Then
    expected = String.format(HEAD_BODY_TAIL, ANIMAL_LIST_HEAD, ANIMAL_LIST_BODY);
    assert actualInExpected(actual, expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
    
    
    // Given
    Map<String, Dog> dogMap = DOG_MAP;
    
    // When
    // Initialization of map uses the static initializer which gives us an anonymous class instance
    // Uses rootNodeAlias("dogs4all") to name the Map instance
    actual = ToStringBuilder.builder(dogMap).rootNodeAlias(DOG_MAP_HEAD_ALIAS).toString();
    //log.debug("******\n" + actual);
    
    // Then
    expected = String.format(HEAD_BODY_TAIL, DOG_MAP_HEAD_ALIAS, DOG_MAP_BODY);
    assert actualInExpected(actual, expected) : String.format(ASSERT_MESSAGE_FORMAT, actual, expected);
  }
  
  @Test
  public void beansToString() {

    String actual;
    
    // Given
    BeanWithPrimitives beanWithPrimitives = createBeanWithPrimitives();
    
    // When
    actual = ToStringBuilder.builder(beanWithPrimitives).toString();
    
    // Then
    assert actualInExpected(actual, EXPECTED_BEAN_WITH_PRIMITIVES) 
      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_BEAN_WITH_PRIMITIVES);

    
    // Given
    BeanWithComposition beanWithComposition = createBeanWithComposition(beanWithPrimitives);
    
    // When
    actual = ToStringBuilder
      .builder(beanWithComposition)
      .indentation(4)
      .toString();
    //log.debug("******\n" + actual);
    
    // Then
    assert actualInExpected(actual, EXPECTED_COMPOSED_BEAN) 
      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_COMPOSED_BEAN);

    
    // Given
    BeanWithNestedClasses beanWithNestedClasses = new BeanWithNestedClasses(100);

    // When
    actual = ToStringBuilder.builder(beanWithNestedClasses).toString();
    
    // Then
    assert actualInExpected(actual, EXPECTED_BEAN_WITH_NESTED_CLASSES) 
      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_BEAN_WITH_NESTED_CLASSES);
  }

  @Test
  public void publicFieldsToString() throws Exception {
    String actual;
    
    // Given
    BeanWithPrimitives beanWithPrimitives = createBeanWithPrimitives();
    
    // When
    actual = ToStringBuilder.builder(beanWithPrimitives)
      .publicFieldsOnly()
      .toString();
    
    // Then
    assert actualInExpected(actual, EXPECTED_BEAN_WITH_PRIMITIVES_PUBLIC_FIELDS) 
      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_BEAN_WITH_PRIMITIVES_PUBLIC_FIELDS);
  }

  @Test
  public void includedFieldsToString() {
    // Given
    BeanWithComposition beanWithComposition = createBeanWithComposition(createBeanWithPrimitives());
    
    // When
    String actual = ToStringBuilder.builder(beanWithComposition)
      .includeField("id")
      .includeField("beanWithPrimitives")
      .includeField("dogMap")
      .includeField("BeanWithPrimitives.id")
      .includeField("BeanWithPrimitives.color")
      .includeField("Animal.name")
      .toString();
    
    //log.debug("******\n" + actual);
    
    // Then
    assert actualInExpected(actual, EXPECTED_COMPOSED_BEAN_WITH_INCLUDED_FIELDS) 
      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_COMPOSED_BEAN_WITH_INCLUDED_FIELDS);
  }
  
  @Test
  public void excludedFieldsToString() {
    // Given
    BeanWithComposition beanWithComposition = createBeanWithComposition(createBeanWithPrimitives());
    
    // When
    String actual = ToStringBuilder.builder(beanWithComposition)
      .excludeField("floatValue")
      .excludeField("animalList")
      .excludeField("stringMap")
      .excludeField("stringList")
      .excludeField("stringArray")
      .excludeField("twoDimensionalArray")
      .excludeField("integerList")
      .excludeField("intArray")
      .excludeField("catArray")
      .excludeField("Animal.says")
      .excludeField("BeanWithPrimitives.baz")
      .excludeField("BeanWithPrimitives.finalField")
      .excludeField("BeanWithPrimitives.foo")
      .excludeField("BeanWithPrimitives.bar")
      .excludeField("BeanWithPrimitives.someDate")
      .toString();
    
    //log.debug("******\n" + actual);
    
    // Then
    assert actualInExpected(actual, EXPECTED_COMPOSED_BEAN_WITH_EXCLUDED_FIELDS) 
      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_COMPOSED_BEAN_WITH_EXCLUDED_FIELDS);
  }
  
  @Test
  public void includeAnnotationsToString() {
    String actual;
    
    // Given
    BeanWithPrimitives beanWithPrimitives = createBeanWithPrimitives();
    
    // When
    actual = ToStringBuilder.builder(beanWithPrimitives)
      .includeFieldsWithAnnotation(MyAnnotation.class)
      .toString();
    //log.debug("******\n" + actual);
    
    // Then
    assert actualInExpected(actual, EXPECTED_BEAN_WITH_PRIMITIVES_INCLUDED_ANNTATIONS) 
      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_BEAN_WITH_PRIMITIVES_INCLUDED_ANNTATIONS);

    
    // Given
    BeanWithComposition beanWithComposition = createBeanWithComposition(createBeanWithPrimitives());
    
    // When
    actual = ToStringBuilder.builder(beanWithComposition)
      .includeFieldsWithAnnotation(MyAnnotation.class)
      .toString();
    //log.debug("******\n" + actual);
    
    // Then
    assert actualInExpected(actual, EXPECTED_COMPOSED_BEAN_INCLUDED_ANNOTATIONS) 
      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_COMPOSED_BEAN_INCLUDED_ANNOTATIONS);
  }
  
  @Test
  public void flattenToString() {
    
    // Given
    BeanWithPrimitives beanWithPrimitives = createBeanWithPrimitives();
    
    // When
    String actual = ToStringBuilder.builder(beanWithPrimitives)
      .flatten()
      .toString();
    
    String lines[] = actual.split("[\\r\\n]+");
    
    // Then
    assert lines.length == 1 : "A flattened result should be exactly one line";
    // ... structure and content of BeanWithPrimitives class tested elsewhere
  }
  
  @Test
  public void overrideFormat() {
    String actual;

    // Given / When
    actual = ToStringBuilder.builder(createBeanWithPrimitives())
      .publicFieldsOnly()
      .flatten()
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
    //log.debug("\n" + actual);
    
    // Then
    assert actual.contains("->@") : "Overridden format should contain \"->@\"";
  }
  
  
  @Test
  public void skipRootNode() {
    // Given
    List<Animal> animalList = ANIMAL_LIST;
    
    // When
    String actual = ToStringBuilder.builder(animalList)
      .skipRootNode()
      .toString();
  
    // Then
    assert actualInExpected(actual, ANIMAL_LIST_BODY) 
      : String.format(ASSERT_MESSAGE_FORMAT, actual, ANIMAL_LIST_BODY);
  }

  
  @Test
  public void packageNameForClasses() {
    // Given
    BeanWithComposition beanWithComposition = createBeanWithComposition(createBeanWithPrimitives());
    
    // When
    String actual = ToStringBuilder.builder(beanWithComposition)
      .packageNameForClasses()
      .flatten()
      .toString();
    //log.debug("\n" + actual);
  
    // Then
    assert actual.contains("no.knowit.testsupport.bean") 
      : "Output should contain package name\"no.knowit.testsupport.bean\"";
  }

  @Test
  public void modifyDateFormat() {
    // Given
    BeanWithPrimitives beanWithPrimitives = createBeanWithPrimitives();
    
    // When
    final DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy-HHmmss");
    String actual = ToStringBuilder.builder(beanWithPrimitives)
      .dateFormat(dateFormat)
      .includeField("someDate")
      .toString();
    
    // Then
    String expected = dateFormat.format(DATE_NOW); // e.g. "11042010-151128";
    assert actual.contains(expected) 
      : String.format("Output should contain expected date: \"%s\"", expected);
  }
  
  @Test
  public void recursionLevels() {
    String actual;
    
    // Given
    PetOwner petOwner = PetOwner
      .owner("Jon Arbuckle")
      .owns(new Cat("Garfield"))
      .owns(new Dog("Odie"))
      .owns(new Mouse("Squeak"))
      .build();
    
    // When
    actual = ToStringBuilder.builder(petOwner)
      .recursionLevel(1)
      .toString();
    log.debug("\n" + actual);
  
    // Then
    assert actual.contains(EXPECTED_RECURSION_LEVEL_1_FRAGMENT_1) 
      && actual.contains(EXPECTED_RECURSION_LEVEL_1_FRAGMENT_2) 
      && actual.contains(EXPECTED_RECURSION_LEVEL_1_FRAGMENT_3)
      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_RECURSION_LEVEL_1);
      
    
    // When
    actual = ToStringBuilder.builder(petOwner)
      .recursionLevel(2)
      .toString();
    log.debug("\n" + actual);
  
    // Then
    assert actual.contains(EXPECTED_RECURSION_LEVEL_2_FRAGMENT) 
      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_RECURSION_LEVEL_2);
    
    
    // When
    actual = ToStringBuilder.builder(petOwner)
      .recursionLevel(3)
      .toString();
    log.debug("\n" + actual);
  
    // Then
    assert actualInExpected(actual, EXPECTED_RECURSION_LEVEL_3) 
      : String.format(ASSERT_MESSAGE_FORMAT, actual, EXPECTED_RECURSION_LEVEL_3);
  }

  private boolean actualInExpected(final String actual, final String expected) {
    String lines[] = actual.split("[\\r\\n]+");
    final Set<String> actualSet = new HashSet<String>();
    for (String line : lines) {
      String s = line.trim();
      int n = s.length();
      if(n > 0) {
        actualSet.add(s.endsWith(",") ? s.substring(0, n) : s);
      }
    }
    
    lines = expected.split("[\\r\\n]+");
    final Set<String> expectedSet = new HashSet<String>();
    for (String line : lines) {
      String s = line.trim();
      int n = s.length();
      if(n > 0) {
        expectedSet.add(s.endsWith(",") ? s.substring(0, n) : s);
      }
    }
    
    return actualSet.containsAll(expectedSet);
  }
  
  private BeanWithPrimitives createBeanWithPrimitives() {
    BeanWithPrimitives beanWithPrimitives = new BeanWithPrimitives();
    MetaCache.set("id", beanWithPrimitives, 2);
    MetaCache.set("foo", beanWithPrimitives, 100);
    MetaCache.set("bar", beanWithPrimitives, "Hello \"BAR\"!");
    MetaCache.set("baz", beanWithPrimitives, "  Hello   BAZ!");
    MetaCache.set("color", beanWithPrimitives, BeanWithPrimitives.Color.RED);
    MetaCache.set("someDate", beanWithPrimitives, DATE_NOW);

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
