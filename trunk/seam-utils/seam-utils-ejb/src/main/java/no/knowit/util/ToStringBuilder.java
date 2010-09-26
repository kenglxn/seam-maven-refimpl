package no.knowit.util;

import static no.knowit.util.ReflectionUtils.OBJECT_PRIMITIVES;

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
import java.util.Set;
import java.util.Map.Entry;

import no.knowit.util.MetaCache.Meta;

/**
 * Utility methods to build a string representation of an objects' field values. 
 * @author LeifOO
 */
public class ToStringBuilder {
  private static final String PARAM_NOT_NULL = "The \"%s\" parameter can not be null";
  
  private Object target = null;
  private int indentation = 2;
  private boolean publicOnly = false;
  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
  private boolean prettyPrint = true;
  private final Set<String> attributes = new HashSet<String>();

  private ToStringBuilder() {
    ;
  }
  
  private ToStringBuilder(final Object target) {
    this.target = target;
  }
  
  public static ToStringBuilder builder(final Object target) {
    if(target == null) {
      throw new IllegalArgumentException(
        String.format(PARAM_NOT_NULL, "target"));
    }
    return new ToStringBuilder(target);
  }

  public ToStringBuilder withIndentation(final int indentation) {
    this.indentation = indentation;
    return this;
  }
  
  public ToStringBuilder withPublicOnly(final boolean publicOnly) {
    this.publicOnly = publicOnly;
    return this;
  }
  
  public ToStringBuilder withDateFormat(final DateFormat dateFormat) {
    if(target == null) {
      throw new IllegalArgumentException(
        String.format(PARAM_NOT_NULL, "dateFormat"));
    }
    this.dateFormat = dateFormat;
    return this;
  }
  
  public ToStringBuilder withPrettyPrint(final boolean prettyPrint) {
    this.prettyPrint = prettyPrint;
    return this;
  }
  
  public ToStringBuilder withAttribute(final String attribute) {
    if (attribute != null && attribute.trim().length() > 0) {
      attributes.add(attribute);
    }
    return this;
  }
  
  @Override
  public String toString() {
    
    if(attributes.size() < 1) {
      final Meta meta = MetaCache.getMeta(target.getClass());
      attributes.addAll(meta.fields.keySet());
    }
    
    StringBuilder sb = new StringBuilder(32)
      .append("{ ")
      .append(quote(target.getClass().getSimpleName()))
      .append(" : {\n");

    final Meta meta = MetaCache.getMeta(target.getClass());
    for (String attribute : attributes) {
      final Field field = meta.fields.get(attribute);
      if(field == null) {
        continue;
      }
      final Method getter = meta.getters.get(attribute);
      
      if( !publicOnly || ( Modifier.isPublic(field.getModifiers()) ||
        ( getter != null && Modifier.isPublic(getter.getModifiers()) ) ) ) {
      
        final Object value = MetaCache.get(attribute, target);
        final Class<?> type = field.getType();
        
        sb.append(indentation > 0 ? String.format("%" + (indentation*2) + "s", "") : "")
          .append(quote(attribute)).append(" : ")
          .append(isPrimitive(type) ? primitiveToString(value) : build(value, indentation*2))
          .append(",\n");
      }
    }
    
    // Delete trailing ','
    int n = sb.length()-2;
    if(sb.charAt(n) == ',') {
      sb.deleteCharAt(n);
    }
    sb.append(indentation > 0 ? String.format("%" + (indentation) + "s", "") : "")
      .append("}\n}");
    
    return prettyPrint ? sb.toString() : sb.toString().replaceAll("\\s+", "");
  }
  
  /**
   * Creates a string representation of an object
   * @param target the object to create a string representation of
   * @return a string representation of the objects' field values
   */
//  public static String toString(final Object target) {
//    return build(target).toString();
//  }
  
  /**
   * Creates a string representation of an object
   * @param target the object to create a string representation of
   * @return a string buffer representation of the objects' field values
   */
//  public static StringBuilder build(final Object target) {
//    return new StringBuilder("{ ")
//      .append(toStringBuilder(target, 2))
//      .append("\n}");
//  }

  private StringBuilder build(final Object target, int indent) {
    
    final StringBuilder sb = new StringBuilder(32);
    
    if(target == null) {
      return sb;
    }
    
    if(target instanceof Collection<?>) {
      sb.append("[");
      final Collection<?> c = (Collection<?>)target;
      int i = 0, l = c.size();
      for (Object v : c) {
        if(v != null && isPrimitive(v.getClass())) {
          sb.append(primitiveToString(v));
        } 
        else {
          sb.append('\n')
            .append(indent > 0 ? String.format("%" + (indent+indentation) + "s", "") : "")
            .append(build(v, indent+indentation));
        }
        sb.append(++i < l ? ", " : "");
      }
      sb.append(']');
    }
    else if(target instanceof Map<?, ?>) {
      sb.append("{\n");
      for (Iterator<?> i = ((Map<?, ?>) target).entrySet().iterator(); i.hasNext();) {
        final Entry<?, ?> e = (Entry<?, ?>)i.next();
        final Object v = e.getValue();
        
        sb.append(indent > 0 ? String.format("%" + (indent+indentation) + "s", "") : "")
          .append(quote(e.getKey().toString()) + " : ");
        
        if(v != null) {
          sb.append(isPrimitive(v.getClass()) ? primitiveToString(v) : build(v, indent+indentation));
        }
        
        sb.append(i.hasNext() ? ",\n" : '\n');
      }
      sb.append(indent > 0 ? String.format("%" + (indent) + "s", "") : "")
        .append("}");
    }
    else if(target.getClass().isArray()) {
      sb.append("[");
      int l = Array.getLength(target);
      for (int i = 0; i < l; i++) {
        Object v = Array.get(target, i);
        if(v != null && isPrimitive(v.getClass())) {
          sb.append(primitiveToString(v));
        }
        else {
          sb.append('\n')
            .append(indent > 0 ? String.format("%" + (indent+indentation) + "s", "") : "")
            .append(build(v, indent+indentation));
        }
        int n = sb.length()-1;
        if(sb.charAt(n) == '\n') {
          sb.deleteCharAt(n);
        }
        sb.append(i < l-1 ? ", " : "");
      }
      sb.append(']');
    }
    else if(target instanceof Object) {
      sb.append(quote(target.getClass().getSimpleName()) + " : {\n");
      
      final Meta meta = MetaCache.getMeta(target.getClass());
      for (Entry<String, Field> entry : meta.fields.entrySet()) {
        final Field field = entry.getValue();
        if(field == null) {
          continue;
        }
        final Method getter = meta.getters.get(entry.getKey());
        if( !publicOnly || ( Modifier.isPublic(field.getModifiers()) ||
            ( getter != null && Modifier.isPublic(getter.getModifiers()) ) ) ) {
        
          final Object value = MetaCache.get(entry.getKey(), target);
          final Class<?> type = field.getType();
            
          sb.append(indent > 0 ? String.format("%" + (indent+indentation) + "s", "") : "")
            .append(quote(entry.getKey())).append(" : ")
            .append(isPrimitive(type) ? primitiveToString(value) : build(value, indent+indentation))
            .append(",\n");
        }
      }
      // Delete trailing ','
      int n = sb.length()-2;
      if(sb.charAt(n) == ',') {
        sb.deleteCharAt(n);
      }
      // Closing '}'
      sb.append(indent > 0 ? String.format("%" + (indent) + "s", "") : "")
        .append('}');
    }    
    return sb;
  }

  private String primitiveToString(final Object primitive) {
    return primitive == null 
      ? "" : primitive instanceof String 
      ? quote((String)primitive) : primitive instanceof java.util.Date
      ? dateFormat.format(primitive) : primitive.toString();
  }

  private static boolean isPrimitive(final Class<?> type) {
    return (type.isPrimitive() || type.isEnum() || OBJECT_PRIMITIVES.indexOf(type) > -1);
  }
  
  /**
   * Copy from: org.json.JSONObject\n
   * Produce a string in double quotes with backslash sequences in all the
   * right places. A backslash will be inserted within </, allowing JSON
   * text to be delivered in HTML. In JSON text, a string cannot contain a
   * control character or an unescaped quote or backslash.
   * @param string A String
   * @return  A String correctly formatted for insertion in a JSON text.
   */
  private static String quote(String string) {
    if (string == null || string.trim().length() == 0) {
      return "\"\"";
    }
    char b;
    char c = 0;
    int i;
    int len = string.length();
    StringBuffer sb = new StringBuffer(len + 4);
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
        if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
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
}
