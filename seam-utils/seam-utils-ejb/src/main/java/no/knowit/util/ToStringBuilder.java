package no.knowit.util;

import static no.knowit.util.ReflectionUtils.OBJECT_PRIMITIVES;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import no.knowit.util.MetaCache.Meta;

/**
 * <p>
 * Assists in implementing {@link Object#toString()} methods. For example:<br/>
 * <b>Given</b> an object that contains two member fields, {@code foo}, and {@code bar}:
 * </p>
 * 
 * <pre>
 * <code>public class MyBean {
 *   private int foo;
 *   private String bar;
 *   public MyBean(int foo, String bar) { this.foo = foo; this.bar = bar; }
 *   public String getBar() { return "Hello: " + bar}
 *   &#064;Override
 *   public String toString() {
 *     return ToStringBuilder
 *       .builder(this)
 *       .flatten()
 *       .toString();
 *   }
 * }</code>
 * </pre>
 * <p>
 * <b>When</b> the values of {@code foo} and {@code bar} are <code>101</code> and
 * <code>"My Bean!"</code> <b>then</b> the <code>toString</code> method should return the string:
 * <code>"{MyBean": {"foo": 101, "bar": "Hello: My Bean!"}}"</code>.
 * </p>
 * <p>
 * The builder is capable of outputting values in encapsulated classes as well. <b>Given</b> a class
 * instance with values:
 * </p>
 * 
 * <pre>
 * <code>public class Frog {
 *   private String name = "Kermit";
 *   private int firstAppearance = 1955; // According to: http://en.wikipedia.org/wiki/Kermit_the_Frog
 *   private Map<String, String> address = new HashMap<String, String>() {{
 *     put("street", "Sesame Street");
 *     put("zip",    "01234");
 *   }};
 *   public String getName() { return name + " the frog"; }
 *   &#064;Override
 *   public String toString() {
 *     return ToStringBuilder
 *       .builder(this)
 *       .toString();
 *   }
 * }</code>
 * </pre>
 * <p>
 * <b>When</b> we call <code>toString</code> <b>then</b> the method should return the string;
 * </p>
 * 
 * <pre>
 * <code>{"Frog": {
 *  "name": "Kermit the frog",
 *  "firstAppearance": 1955,
 *  "address": {
 *    "street": "Sesame Street",
 *    "zip": "01234"
 *  }
 * }}</code>
 * </pre>
 * <p>
 * You can also use the builder to debug 3rd party objects:
 * </p>
 * 
 * <pre>
 * <code>Integer i = new Integer(1001);
 * System.out.println(ToStringBuilder.builder(i).toString());</code>
 * </pre>
 * 
 * </p>Reflection is used to output the field values - which for private and protected fields will
 * fail under a security manager, unless the appropriate permissions are set up correctly.
 * <p>
 * 
 * @author LeifOO
 */
public class ToStringBuilder {

  private static final String PARAM_NOT_NULL = "The \"%s\" parameter can not be null";
  private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";
  private static final int MAX_RECURSION_LEVEL = 1000;

  private Object target = null;
  private int indentation = 2;
  private int recursionLevel = MAX_RECURSION_LEVEL;
  private DateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
  private boolean flatten = false;
  private String rootNodeAlias = null;
  private boolean packageNameForClasses = false;
  private boolean skipRootNode = false;
  private boolean publicFields = false;
  private boolean allFields = false;
  private FieldNameFormatter fieldNameFormatter = null;
  private FieldValueFormatter fieldValueFormatter = null;
  private final Set<String> fieldNames = new HashSet<String>();
  private final Set<String> excludedFieldNames = new HashSet<String>();
  private final Set<Class<? extends Annotation>> annotations = new HashSet<Class<? extends Annotation>>();

  // Strategy interfaces

  public interface FieldNameFormatter {
    String format(final Object owner, final String name);
  }

  public interface FieldValueFormatter {
    String format(final Object owner, final Object value);
  }

  private ToStringBuilder() {
  }

  private ToStringBuilder(final Object target) {
    this.target = target;
  }

  /**
   * <p>Creates an instance of {@link ToStringBuilder} which assists in implementing
   * {@link Object#toString()} methods.</p>
   *
   * @param target the object to create a string representation of
   * @return the builder
   * @throws IllegalArgumentException if {@code target} is null
   */
  public static ToStringBuilder builder(final Object target) {
    if (target == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "target"));
    }
    return new ToStringBuilder(target);
  }

  /**
   * <p>Indentation of formatted output.</p>
   * <pre><code>String s = ToStringBuilder.builder(anObject).indentation(4).toString());</code></pre>
   *
   * @param indentation number of spaces to indent output, default value is 2
   * @return the same {@link ToStringBuilder} instance
   */
  public ToStringBuilder indentation(final int indentation) {
    this.indentation = indentation < 0 ? 2 : indentation;
    return this;
  }

  /**
   * Maximum level of encapsulated classes in formatted output
   * <p><b>Given</b> a package, <code>org.mypackage</code>, and two classes, <code>Hello</code> and
   * <code>World</code> where <code>Hello</code> encapsulates <code>World</code>.</p>
   * 
   * <pre><code>public class World {
   *   private String world = "World";
   * }
   * public class Hello {
   *   private String hello = "Hello";
   *   private World world
   *   &#064;Override
   *   public String toString() {
   *     return ToStringBuilder
   *       .builder(this)
   *       <b>.recursionLevel(0)</b>
   *       .flatten()
   *       .toString();
   *   }
   * }</code></pre>
   * 
   * <p><b>When</b> we call <code>toString</code>
   * <b>then</b> the method should return the string:</p>
   * <pre><code>{"Hello": {"hello": "Hello", world: "World" : org.mypackage.World@1f6df4c}}</code></pre>
   * 
   * @param recursionLevel recursion level, default value is 1000
   * @return the same {@link ToStringBuilder} instance
   */
  public ToStringBuilder recursionLevel(final int recursionLevel) {
    this.recursionLevel = recursionLevel < 0 || recursionLevel > MAX_RECURSION_LEVEL ? 1 : recursionLevel;
    return this;
  }

  /**
   * <p>Only public fields or fields with a corresponding public get method will be added to the
   * formatted output.</p>
   * 
   * <p>Reflection is used to output the field values which for private and protected fields will
   * fail under a security manager, unless the appropriate permissions are set up correctly.</p>
   * 
   * <p><b>Given</b> a class instance with values:</p>
   * <pre><code>public class Frog {
   *   private String name = "Kermit";
   *   private int firstAppearance = 1955;
   *   private Map<String, String> address = new HashMap<String, String>() {{
   *     put("street", "Sesame Street");
   *     put("zip",    "01234");
   *   }};
   *   public String getName() { return name + " the frog"; }
   *   &#064;Override
   *   public String toString() {
   *     return ToStringBuilder
   *       .builder(this)
   *       <b>.publicFieldsOnly()</b>
   *       .flatten()
   *       .toString();
   *   }
   * }</code></pre>
   * <p><b>When</b> we call <code>toString</code>
   * <b>then</b> the method should return the string:</p>
   * <pre><code>{"Frog": {"name": "Kermit the frog"}}</code></pre>
   *
   * @return the same {@link ToStringBuilder} instance
   */
  public ToStringBuilder publicFieldsOnly() {
    publicFields = true;
    return this;
  }

  /**
   * <p>The date format used to output date values.</p>
   * <pre><code>String s = ToStringBuilder.builder(anObject).dateFormat("dd-MM-yyyy HH:mm").toString());</code></pre>
   *
   * @param dateFormat the date format, default date format is <code>"yyyy-MM-dd HH:mm:ss Z"</code>
   * @return the same {@link ToStringBuilder} instance
   * @see java.text.DateFormat
   */
  public ToStringBuilder dateFormat(final DateFormat dateFormat) {
    if (dateFormat == null) {
      this.dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
    } else {
      this.dateFormat = dateFormat;
    }
    return this;
  }

  /**
   * <p>Indicates whether the output should be hierarchical, i.e. pretty formatted with indentation
   * and line breaks, or if the output should be flattened into a one line output.</p>
   * 
   * <pre><code>String s = ToStringBuilder.builder(anObject).flatten().toString());</code></pre>
   *
   * @return the same {@link ToStringBuilder} instance
   */
  public ToStringBuilder flatten() {
    flatten = true;
    return this;
  }

  /**
   * <p>You can output class names using {@link Class#getSimpleName} or {@link Class#getName}.<br/>
   * <b>Given</b> a package, <code>org.mypackage</code> and a class instance with values:</p>
   * 
   * <pre><code>public class Frog {
   *   private String name = "Kermit";
   *   &#064;Override
   *   public String toString() {
   *     return ToStringBuilder
   *       .builder(this)
   *       <b>.packageNameForClasses()</b>
   *       .flatten()
   *       .toString();
   *   }
   * }</code></pre>
   * 
   * <p><b>When</b> we call <code>toString</code>
   * <b>then</b> the method should return the string:</p>
   * 
   * <pre><code>{"org.mypackage.Toad": {"name": "Kermit"}}</code></pre>
   *
   * @return the same {@link ToStringBuilder} instance
   */
  public ToStringBuilder packageNameForClasses() {
    packageNameForClasses = true;
    return this;
  }

  /**
   * <p>Skip output of the topmost class name.<br/>
   * <b>Given</b> a class instance with values:</p>
   * 
   * <pre><code>public class Frog {
   *   private String name = "Kermit";
   *   &#064;Override
   *   public String toString() {
   *     return ToStringBuilder
   *       .builder(this)
   *       <b>.skipRootNode()</b>
   *       .flatten()
   *       .toString();
   *   }
   * }</code></pre>
   *
   * <p><b>When</b> we call <code>toString</code>
   * <b>then</b> the method should return the string;</p>
   * 
   * <pre><code>{"name": "Kermit"}</code></pre>
   *
   * @return the same {@link ToStringBuilder} instance
   */
  public ToStringBuilder skipRootNode() {
    skipRootNode = true;
    return this;
  }

  /**
   * <p>This method alters the name of the topmost class in the formatted output.<br/>
   * <b>Given</b> a class instance with values:</p>
   * 
   * <pre><code>public class Frog {
   *   private String name = "Kermit";
   *   &#064;Override
   *   public String toString() {
   *     return ToStringBuilder
   *       .builder(this)
   *       <b>.rootNodeAlias("Toad")</b>
   *       .flatten()
   *       .toString();
   *   }
   * }</code></pre>
   * 
   * <p><b>When</b> we call <code>toString</code>
   * <b>then</b> the method should return the string:</p>
   * 
   * <pre><code>{"Toad": {"name": "Kermit"}}</code></pre>
   *
   * @param name the name of the root node alias
   * @return the same {@link ToStringBuilder} instance
   */
  public ToStringBuilder rootNodeAlias(final String name) {
    final String n = name != null ? name.trim() : null;
    rootNodeAlias = n;
    return this;
  }

  /**
   * <p>You can set up a call back method to control formatting of field names.<br/>
   * <b>Given</b> a class instance with values:</p>
   * 
   * <pre><code>public class Frog {
   *   private String name = "Kermit";
   *   &#064;Override
   *   public String toString() {
   *     return ToStringBuilder
   *       .builder(this)
   *       <b>.fieldNameFormatter(new ToStringBuilder.FieldNameFormatter() {
   *         &#064;Override
   *         public String format(final Object owner, final String name) {
   *           return name + "->";
   *         }
   *       })</b>
   *       .flatten()
   *       .toString();
   *     }
   * }</code></pre>
   * 
   * <p><b>When</b> we call <code>toString</code>
   * <b>then</b> the method should return the string:</p>
   * 
   * <pre><code>{"Frog": {name-> "Kermit"}}</code></pre>
   *
   * @param fieldNameFormatter
   * @return the same {@link ToStringBuilder} instance
   */
  public ToStringBuilder fieldNameFormatter(final FieldNameFormatter fieldNameFormatter) {
    this.fieldNameFormatter = fieldNameFormatter;
    return this;
  }

  /**
   * <p>You can set up a call back method to control formatting of field values.<br/>
   * <b>Given</b> a class instance with values:</p>
   * 
   * <pre><code>public class Frog {
   *   private String name = "Kermit";
   *   &#064;Override
   *   public String toString() {
   *     return ToStringBuilder
   *       .builder(this)
   *       <b>.fieldValueFormatter(new ToStringBuilder.FieldValueFormatter() {
   *         &#064;Override
   *         public String format(final Object owner, final Object value) {
   *           return "#" + value;
   *         }
   *       })</b>
   *       .flatten()
   *       .toString();
   *     }
   * }</code></pre>
   * 
   * <p><b>When</b> we call <code>toString</code>
   * <b>then</b> the method should return the string:</p>
   * 
   * <pre><code>{"Frog": {"name": #Kermit}}</code></pre>
   *
   * @param fieldValueFormatter
   * @return the same {@link ToStringBuilder} instance
   */
  public ToStringBuilder fieldValueFormatter(final FieldValueFormatter fieldValueFormatter) {
    this.fieldValueFormatter = fieldValueFormatter;
    return this;
  }

  /**
   * <p>Only a specified set of fields will be <b>included</b> in the formatted output.
   * Use "dot" notation to include fields for encapsulated classes.<br/>
   * <b>Given</b> a class instance with values:</p>
   * 
   * <pre><code>public class Frog {
   *   public static Class Address {
   *     String street;
   *     String zip;
   *     public Address(String street, string zip) {
   *       this.street=street; this.zip=zip;
   *     }
   *   }
   *   private String name = "Kermit";
   *   private int firstAppearance = 1955;
   *   private Address address = new Address("Sesame Street", "01234");
   *   public String getName() { return name + " the frog"; }
   *   &#064;Override
   *   public String toString() {
   *     return ToStringBuilder
   *       .builder(this)
   *       <b>.includeField("name")
   *       .includeField("address")</b>
   *       .includeField("Address.street")</b>
   *       .toString();
   *   }
   * }</code></pre>
   * 
   * <p><b>When</b> we call <code>toString</code>
   * <b>then</b> the method should return the string:</p>
   * 
   * <pre><code>{"Frog": {
   *  "name": "Kermit the frog",
   *  "address": "Address" {
   *    "street": "Sesame Street"
   *  }
   * }}</code></pre>
   *
   * @param name the name of the field to output when <code>toString</code> is called
   * @return the same {@link ToStringBuilder} instance
   */
  public ToStringBuilder includeField(final String name) {
    final String n = name != null ? name.trim() : "";
    if (n.length() > 0) {
      fieldNames.add(n);
    }
    return this;
  }

  /**
   * <p>You can specify a set of fields to be <b>excluded</b> from the formatted output.
   * Use "dot" notation to include fields for encapsulated classes.<br/>
   * <b>Given</b> a class instance with values:</p>
   * 
   * <pre><code>public class Frog {
   *   public static Class Address {
   *     String street;
   *     String zip;
   *     public Address(String street, string zip) {
   *       this.street=street; this.zip=zip;
   *     }
   *   }
   *   private String name = "Kermit";
   *   private int firstAppearance = 1955;
   *   private Address address = new Address("Sesame Street", "01234");
   *   public String getName() { return name + " the frog"; }
   *   &#064;Override
   *   public String toString() {
   *     return ToStringBuilder
   *       .builder(this)
   *       <b>.excludeField("firstAppearance")
   *       .excludeField("Address.zip")</b>
   *       .toString();
   *   }
   * }</code></pre>
   * 
   * <p><b>When</b> we call <code>toString</code>
   * <b>then</b> the method should return the string:</p>
   * 
   * <pre><code>{"Frog": {
   *  "name": "Kermit the frog",
   *  "address": "Address" {
   *    "street": "Sesame Street"
   *  }
   * }}</code></pre>
   *
   * @param name the name of the field to exclude from output when <code>toString</code> is called
   * @return the same {@link ToStringBuilder} instance
   */
  public ToStringBuilder excludeField(final String name) {
    final String n = name != null ? name.trim() : "";
    if (n.length() > 0) {
      excludedFieldNames.add(n);
    }
    return this;
  }

  /**
   * <p>Output fields with a given annotation.<br/>
   * <b>Given</b> a class with a member field annotated with the {@link javax.persistence.Id}
   * annotation:</p>
   * 
   * <pre><code>&#064;Entity
   * public class Frog {
   *   &#064;Id
   *   private Integer identifier;
   *   private String name;
   *   &#064;Override
   *   public String toString() {
   *     return ToStringBuilder
   *       .builder(this)
   *       <b>.includeAnnotation(Id.class)</b>
   *       .flatten()
   *       .toString();
   *   }
   * }</code></pre>
   * 
   * <p><b>When</b> the values of {@code identifier} is <code>101</code>
   * <b>then</b> the <code>toString</code> method should return the string:
   * <code>"{Frog": {"identifier": 101}}"</code>.</p>
   * 
   * @param annotation the annotation to output fields for when <code>toString</code> is called
   * @return the same {@link ToStringBuilder} instance
   */
  public ToStringBuilder includeFieldsWithAnnotation(final Class<? extends Annotation> annotation) {
    annotations.add(annotation);
    return this;
  }

  /**
   * <p>Creates a string representation of an object.</p>
   *
   * @return a string representation of the objects' field values
   * @see {@link Object#toString()}
   */
  @Override
  public String toString() {

    initFieldNameFormatter();

    initFieldValueFormatter();

    if (rootNodeAlias == null) {
      rootNodeAlias = generateClassname(target);
    }

    allFields = fieldNames.size() < 1 && excludedFieldNames.size() < 1 && annotations.size() < 1;

    final StringBuilder sb = new StringBuilder(64);
    if (skipRootNode) {
      sb.append(build(target, indentation, 0));
    } else {
      sb.append('{')
      .append(fieldNameFormatter.format(target, rootNodeAlias))
      .append(build(target, indentation, 0))
      .append('}');
    }

    return flatten ? flattenBuild(sb) : sb.toString();
  }

  /**
   * @deprecated use {@link no.knowit.util.ReflectionUtils#isPrimitive} instead
   * @param type
   * @return
   */
  @Deprecated
  public static boolean isPrimitive(final Class<?> type) {
    return type.isPrimitive() || type.isEnum() || OBJECT_PRIMITIVES.indexOf(type) > -1;
  }

  /**
   * Copy from: org.json.JSONObject<br/>
   * Produce a string in double quotes with backslash sequences in all the
   * right places. A backslash will be inserted within </, allowing JSON
   * text to be delivered in HTML. In JSON text, a string cannot contain a
   * control character or an unescaped quote or backslash.
   *
   * @param string A String
   * @return A String correctly formatted for insertion in a JSON text.
   */
  public static String quote(final String string) {
    if (string == null || string.trim().length() == 0) {
      return "\"\"";
    }
    char b;
    char c = 0;
    int i;
    final int len = string.length();
    final StringBuffer sb = new StringBuffer(len + 4);
    String t;

    sb.append('"');
    for (i = 0; i < len; i += 1) {
      b = c;
      c = string.charAt(i);
      switch (c) {
      case '\\':
      case '"':
        sb.append('\\');
        sb.append(c);
        break;
      case '/':
        if (b == '<') {
          sb.append('\\');
        }
        sb.append(c);
        break;
      case '\b':
        sb.append("\\b");
        break;
      case '\t':
        sb.append("\\t");
        break;
      case '\n':
        sb.append("\\n");
        break;
      case '\f':
        sb.append("\\f");
        break;
      case '\r':
        sb.append("\\r");
        break;
      default:
        if (c < ' ' || c >= '\u0080' && c < '\u00a0' || c >= '\u2000' && c < '\u2100') {
          t = "000" + Integer.toHexString(c);
          sb.append("\\u" + t.substring(t.length() - 4));
        } else {
          sb.append(c);
        }
      }
    }
    sb.append('"');
    return sb.toString();
  }

  private String generateClassname(final Object obj) {
    return packageNameForClasses
    ? obj.getClass().getName()
        : obj.getClass().getSimpleName();
  }

  private String flattenBuild(StringBuilder sb) {
    final String lines[] = sb.toString().split("[\\r\\n]+");
    sb = new StringBuilder(64);
    for (final String line : lines) {
      sb.append(line.trim());
    }
    return sb.toString();
  }

  private void initFieldValueFormatter() {
    if (fieldValueFormatter == null) {
      fieldValueFormatter = new FieldValueFormatter() {
        @Override
        public String format(final Object owner, final Object value) {
          if (value == null) {
            return "";
          } else if (value instanceof String) {
            return quote((String) value);
          } else {
            return value instanceof java.util.Date ? dateFormat.format(value) : value.toString();
          }
        }
      };
    }
  }

  private void initFieldNameFormatter() {
    if (fieldNameFormatter == null) {
      fieldNameFormatter = new FieldNameFormatter() {
        @Override
        public String format(final Object owner, final String name) {
          return quote(name) + ": ";
        }
      };
    }
  }

  private StringBuilder build(final Object owner, final int indent, final int recursion) {
    if(owner != null) {
      if (owner instanceof Collection<?>) {
        return buildCollection(owner, indent, recursion);
      }
      else if (owner instanceof Map<?, ?>) {
        return buildMap(owner, indent, recursion);
      }
      else if (owner.getClass().isArray()) {
        return buildArray(owner, indent, recursion);
      }
      else if (owner instanceof Object) {
        return buildObject(owner, indent, recursion);
      }
    }
    return null;
  }

  private StringBuilder buildObject(final Object owner, final int indent, final int recursion) {
    final StringBuilder sb = new StringBuilder(32);
    if (target != owner) {
      sb.append(fieldNameFormatter.format(owner, generateClassname(owner)));
    }

    if(recursion > recursionLevel) {
      sb.append(owner.getClass().getName() + '@' + Integer.toHexString(owner.hashCode()));
      owner.toString();
    }
    else {
      sb.append("{\n");

      final Meta meta = MetaCache.getMeta(owner.getClass());
      for (final Entry<String, Field> entry : meta.fields.entrySet()) {
        final Field field = entry.getValue();
        if (field == null) {
          continue;
        }
        final Method getter = meta.getters.get(entry.getKey());
        if (!publicFields || Modifier.isPublic(field.getModifiers()) ||
            getter != null && Modifier.isPublic(getter.getModifiers())) {

          final String fieldName = entry.getKey();

          if(fieldName.startsWith("this$")) {
            // Nested classes that are not static member classes has a hidden
            // this$0 field to reference the outermost enclosing class
            continue;
          }
          if (isFieldNameInFieldNames(fieldName, owner) || hasAnnotation(fieldName, meta)) {
            final Object value = MetaCache.get(entry.getKey(), owner);
            final Class<?> type = field.getType();

            sb.append(indention(indent))
            .append(fieldNameFormatter.format(owner, fieldName))
            .append(ReflectionUtils.isPrimitive(type)
                ? fieldValueFormatter.format(owner, value)
                    : build(value, indent + indentation, recursion + 1)
            )
            .append(",\n");
          }
        }
      }
    }
    // Delete trailing ','
    final int n = sb.length() - 2;
    if (sb.charAt(n) == ',') {
      sb.deleteCharAt(n);
    }
    if(recursion <= recursionLevel) {
      sb.append(indention(indent - indentation))
      .append('}');
    }
    return sb;
  }

  private StringBuilder buildArray(final Object owner, final int indent, final int recursion) {
    final StringBuilder sb = new StringBuilder(32);
    sb.append('[');
    final int l = Array.getLength(owner);
    for (int i = 0; i < l; i++) {
      final Object v = Array.get(owner, i);
      if (v != null && isPrimitive(v.getClass())) {
        sb.append(fieldValueFormatter.format(owner, v));
      } else {
        sb.append('\n')
        .append(indention(indent))
        .append(build(v, indent + indentation, recursion + 1));
      }
      // Delete trailing '\n'
      final int n = sb.length() - 1;
      if (sb.charAt(n) == '\n') {
        sb.deleteCharAt(n);
      }
      if (i < l - 1) {
        sb.append(", ");
      }
    }
    sb.append(']');
    return sb;
  }

  private StringBuilder buildMap(final Object owner, final int indent, final int recursion) {
    final StringBuilder sb = new StringBuilder(32);
    sb.append("{\n");
    for (final Iterator<?> i = ((Map<?, ?>) owner).entrySet().iterator(); i.hasNext();) {
      final Entry<?, ?> e = (Entry<?, ?>) i.next();
      final Object v = e.getValue();

      sb.append(indention(indent))
      .append(fieldNameFormatter.format(owner, e.getKey().toString()));

      if (v != null) {
        sb.append(ReflectionUtils.isPrimitive(v.getClass())
            ? fieldValueFormatter.format(owner, v)
                : build(v, indent + indentation, recursion + 1));
      }
      sb.append(i.hasNext() ? ",\n" : '\n');
    }
    sb.append(indention(indent - indentation))
    .append('}');
    return sb;
  }

  private StringBuilder buildCollection(final Object owner, final int indent, final int recursion) {
    final StringBuilder sb = new StringBuilder(32);
    sb.append('[');
    final Collection<?> c = (Collection<?>) owner;

    int i = 0;
    final int l = c.size();
    for (final Object v : c) {
      if (v != null && isPrimitive(v.getClass())) {
        sb.append(fieldValueFormatter.format(owner, v));
      } else {
        sb.append('\n')
        .append(indention(indent))
        .append(build(v, indent + indentation, recursion + 1));
      }
      sb.append(++i < l ? ", " : "");
    }
    sb.append(']');
    return sb;
  }

  private String indention(final int indent) {
    return indent > 0 ? String.format("%" + indent + "s", "") : "";
  }

  private boolean isFieldNameInFieldNames(final String fieldName, final Object owner) {
    if(allFields) {
      return true;
    }
    final boolean excludedFieldNamesSizeGt0 = excludedFieldNames.size() > 0;
    if(excludedFieldNamesSizeGt0) {
      for (Class<?> clazz = owner.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
        if (excludedFieldNames.contains(
            target == owner ? fieldName : String.format("%s.%s", clazz.getSimpleName(), fieldName))) {
          return false;
        }
      }
    }
    if(fieldNames.size() > 0) {
      for (Class<?> clazz = owner.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
        if (fieldNames.contains(
            target == owner ? fieldName : String.format("%s.%s", clazz.getSimpleName(), fieldName))) {
          return true;
        }
      }
      return false;
    }
    return excludedFieldNamesSizeGt0 ? true : false;
  }

  private boolean hasAnnotation(final String fieldName, final Meta meta) {
    if(annotations.size() > 0) {
      final Field field = meta.fields.get(fieldName);  // Assumes field != null
      final Method getter = meta.getters.get(fieldName);
      final Method setter = meta.setters.get(fieldName);
      for(final Class<? extends Annotation> annotation : annotations) {
        if(field.isAnnotationPresent(annotation)) {
          return true;
        }
        if(getter != null && getter.isAnnotationPresent(annotation)) {
          return true;
        }
        if(setter != null && setter.isAnnotationPresent(annotation)) {
          return true;
        }
      }
    }
    return false;
  }
}
