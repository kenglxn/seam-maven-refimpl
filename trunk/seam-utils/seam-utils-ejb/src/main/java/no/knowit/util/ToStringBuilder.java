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
  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
  private boolean prettyFormat = true;
  private boolean publicFields = false;
  private boolean allFields = false;
  private FieldNameFormatter fieldNameFormatter = null;
  private ValueFormatter valueFormatter = null;
  private final Set<String> fieldNames = new HashSet<String>();
  
  // Strategy interfaces
  public interface FieldNameFormatter {
    String format(final Object Owner, final String name);
  }
  
  public interface ValueFormatter {
    String format(final Object Owner, final Object value);
  }

  private ToStringBuilder() {}
  
  private ToStringBuilder(final Object target) {
    this.target = target;
  }
  
  public static ToStringBuilder builder(final Object target) {
    if(target == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "target"));
    }
    return new ToStringBuilder(target);
  }

  public ToStringBuilder withIndentation(final int indentation) {
    this.indentation = indentation;
    return this;
  }
  
  public ToStringBuilder withPublicFieldsOnly(final boolean publicFields) {
    this.publicFields = publicFields;
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
  
  public ToStringBuilder withPrettyFormat(final boolean prettyFormat) {
    this.prettyFormat = prettyFormat;
    return this;
  }
  
  public ToStringBuilder withFieldNameFormatter(FieldNameFormatter fieldNameFormatter) {
    this.fieldNameFormatter = fieldNameFormatter;
    return this;
  }
  
  public ToStringBuilder withValueFormatter(ValueFormatter valueFormatter) {
    this.valueFormatter = valueFormatter;
    return this;
  }
  
  public ToStringBuilder withField(final String name) {
    final String n = name != null ? name.trim() : ""; 
    if (n.length() > 0) {
      fieldNames.add(n);
    }
    return this;
  }
  
  @Override
  public String toString() {
    
    if(fieldNames.size() < 1) {
      final Meta meta = MetaCache.getMeta(target.getClass());
      fieldNames.addAll(meta.fields.keySet());
      allFields = true;
    }
    StringBuilder sb = new StringBuilder(64)
      .append("{ ")
      .append(build(target, indentation))
      .append(indentation > 0 ? String.format("%" + (indentation) + "s", "") : "")
      .append("\n}");
    
    return prettyFormat ? sb.toString() : sb.toString().replaceAll("\\s+", " ");
  }

  private StringBuilder build(final Object owner, int indent) {
    
    final StringBuilder sb = new StringBuilder(32);
    if(owner == null) {
      return sb;
    }
    if(owner instanceof Collection<?>) {
      sb.append("[");
      final Collection<?> c = (Collection<?>)owner;
      int i = 0, l = c.size();
      for (Object v : c) {
        if(v != null && isPrimitive(v.getClass())) {
          sb.append(formatValue(owner, v));
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
    else if(owner instanceof Map<?, ?>) {
      sb.append("{\n");
      for (Iterator<?> i = ((Map<?, ?>) owner).entrySet().iterator(); i.hasNext();) {
        final Entry<?, ?> e = (Entry<?, ?>)i.next();
        final Object v = e.getValue();
        
        sb.append(indent > 0 ? String.format("%" + (indent+indentation) + "s", "") : "")
          .append(formatFieldName(owner, e.getKey().toString()));
        
        if(v != null) {
          sb.append(isPrimitive(v.getClass()) ? formatValue(owner, v) : build(v, indent+indentation));
        }
        sb.append(i.hasNext() ? ",\n" : '\n');
      }
      sb.append(indent > 0 ? String.format("%" + (indent) + "s", "") : "")
        .append("}");
    }
    else if(owner.getClass().isArray()) {
      sb.append("[");
      int l = Array.getLength(owner);
      for (int i = 0; i < l; i++) {
        Object v = Array.get(owner, i);
        if(v != null && isPrimitive(v.getClass())) {
          sb.append(formatValue(owner, v));
        }
        else {
          sb.append('\n')
            .append(indent > 0 ? String.format("%" + (indent+indentation) + "s", "") : "")
            .append(build(v, indent+indentation));
        }
        // Delete trailing LF
        int n = sb.length()-1;
        if(sb.charAt(n) == '\n') {
          sb.deleteCharAt(n);
        }
        sb.append(i < l-1 ? ", " : "");
      }
      sb.append(']');
    }
    else if(owner instanceof Object) {
      sb.append(quote(owner.getClass().getSimpleName()) + " : {\n");
      final Meta meta = MetaCache.getMeta(owner.getClass());
      for (Entry<String, Field> entry : meta.fields.entrySet()) {
        final Field field = entry.getValue();
        if(field == null) {
          continue;
        }
        final Method getter = meta.getters.get(entry.getKey());
        if( !publicFields || ( Modifier.isPublic(field.getModifiers()) ||
            ( getter != null && Modifier.isPublic(getter.getModifiers()) ) ) ) {
        
          final String fieldName = entry.getKey();
          if( allFields || isFieldNameInFieldNames(owner, fieldName) ) {
            final Object value = MetaCache.get(entry.getKey(), owner);
            final Class<?> type = field.getType();
              
            sb.append(indent > 0 ? String.format("%" + (indent+indentation) + "s", "") : "")
              .append(formatFieldName(owner, fieldName));
            
            if(isPrimitive(type)) {
              sb.append(formatValue(owner, value));
            }
            else {
              sb.append(build(value, indent+indentation));
            }
            sb.append(",\n");
          }
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

  private String formatFieldName(final Object owner, final String name) {
    if(fieldNameFormatter != null) {
      return fieldNameFormatter.format(owner, name); 
    }
    return quote(name) + " : ";
  }
  
  private String formatValue(final Object owner, final Object value) {
    if(valueFormatter != null) {
      return valueFormatter.format(owner, value); 
    }
    return value == null 
      ? "" : value instanceof String 
      ? quote((String)value) : value instanceof java.util.Date 
      ? dateFormat.format(value) : value.toString();
  }
  
  private boolean isFieldNameInFieldNames(final Object owner, final String fieldName) {
    for (Class<?> clazz = owner.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
      if(fieldNames.contains( 
        target == owner ? fieldName : String.format("%s.%s", clazz.getSimpleName(), fieldName))
      ) {
        return true;
      }
    }
    return false;
  }

  /**
   * Copy from: org.json.JSONObject<br/>
   * Produce a string in double quotes with backslash sequences in all the
   * right places. A backslash will be inserted within </, allowing JSON
   * text to be delivered in HTML. In JSON text, a string cannot contain a
   * control character or an unescaped quote or backslash.
   * @param string A String
   * @return  A String correctly formatted for insertion in a JSON text.
   */
  public static String quote(String string) {
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
  
  private static boolean isPrimitive(final Class<?> type) {
    return (type.isPrimitive() || type.isEnum() || OBJECT_PRIMITIVES.indexOf(type) > -1);
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


}
