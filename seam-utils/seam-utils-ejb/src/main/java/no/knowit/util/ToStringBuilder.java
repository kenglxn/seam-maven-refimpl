package no.knowit.util;

import static no.knowit.util.ReflectionUtils.OBJECT_PRIMITIVES;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import no.knowit.util.MetaCache.Meta;

/**
 * 
 * @author LeifOO
 *
 */
public class ToStringBuilder {

  private ToStringBuilder() {
    ;
  }

  public static String buildToString(final Object target) {
    return build(target).toString();
  }
  
  public static StringBuilder build(final Object target) {
    return new StringBuilder("{ ")
      .append(toStringBuilder(target, 2))
      .append("\n}");
  }

  private static StringBuilder toStringBuilder(final Object target, int indent) {
    
    if(target == null) {
      return new StringBuilder();
    }
    
    final StringBuilder sb = new StringBuilder();
    
    if(target instanceof Collection<?>) {
      sb.append("[");
      
      Collection<?> c = (Collection<?>)target;
      for (Object v : c) {
        if(v != null && isPrimitive(v.getClass())) {
          sb.append(primitiveToString(v));
        } 
        else {
          sb.append(String.format("\n%" + (indent+2) + "s", ""))
            .append(toStringBuilder(v, indent+2));
        }
        sb.append(", ");
      }

      // Delete trailing ", "
      int n = sb.length()-1;
      sb.deleteCharAt(n-1);
      sb.deleteCharAt(n-1);
      sb.append(']');
    }
    else if(target instanceof Map<?, ?>) {
      sb.append("{\n");
      
      for (Iterator<?> iter = ((Map<?, ?>) target).entrySet().iterator(); iter.hasNext();) {
        Entry<?, ?> e = (Entry<?, ?>)iter.next();
        Object v = e.getValue();
        
        sb.append(String.format("%" + (indent+2) + "s", ""))
          .append(quote(e.getKey().toString()) + " : ");
        
        if(v != null && isPrimitive(v.getClass())) {
          sb.append(primitiveToString(v));
        } 
        else {
          sb.append(toStringBuilder(v, indent+2));
        }
        if(iter.hasNext()) {
          sb.append(',');
        }
        sb.append('\n');
      }
      sb.append(String.format("%" + (indent) + "s", "")).append("}");
    }
    else if(target.getClass().isArray()) {
      sb.append("[");
      
      int l = Array.getLength(target);
      for (int k = 0; k < l; k++) {
        Object v = Array.get(target, k);
        if(v != null && isPrimitive(v.getClass())) {
          sb.append(primitiveToString(v));
        }
        else {
          sb.append(String.format("\n%" + (indent+2) + "s", ""))
            .append(toStringBuilder(v, indent+2));
        }
        int n = sb.length()-1;
        if(sb.charAt(n) == '\n') {
          sb.deleteCharAt(n);
        }
        sb.append(k < l-1 ? ", " : "");
      }
      sb.append(']');
    }
    else if(target instanceof Object) {
      sb.append(quote(target.getClass().getSimpleName()) + " : {\n");
      
      final Meta meta = MetaCache.getMeta(target.getClass());
      for (Entry<String, Field> entry : meta.fields.entrySet()) {
        
        Field field = entry.getValue();
        Object value = ReflectionUtils.get(field, target);
        Class<?> type = field.getType();
        
        sb.append(String.format("%" + (indent+2) + "s", ""))
          .append(quote(entry.getKey())).append(" : ");
        
        if(isPrimitive(type)) {
          sb.append(primitiveToString(value));
        }
        else {
          sb.append(toStringBuilder(value, indent+2));
        }
        sb.append(",\n");
      }
      
      // Delete last ',' char
      int n = sb.length()-2;
      if(sb.charAt(n) == ',') {
        sb.deleteCharAt(n);
      }
      sb.append(String.format("%" + (indent) + "s", ""))
        .append('}');
    }    
    return sb;
  }

  private static boolean isPrimitive(final Class<?> type) {
    return (type.isPrimitive() || type.isEnum() || OBJECT_PRIMITIVES.indexOf(type) > -1);
  }
  
  private static String primitiveToString(final Object primitive) {
    return primitive == null 
      ? "" : primitive instanceof String 
      ? quote((String)primitive) : primitive.toString();
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
    if (string == null || string.length() == 0) {
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
