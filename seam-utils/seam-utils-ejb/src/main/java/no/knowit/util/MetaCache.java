package no.knowit.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import no.knowit.util.ReflectionUtils;
import static no.knowit.util.ReflectionUtils.OBJECT_PRIMITIVES;
/**
 * 
 * @author LeifOO
 *
 */
public class MetaCache {

  private static final ConcurrentMap<String, Meta> 
    metaCache = new ConcurrentHashMap<String, Meta>(); 

  private MetaCache() {
    ;
  }

  public static Object get(final String attribute, final Object target) {
    Meta meta = getMeta(target.getClass());
    Method method = meta.getters.get(attribute);
    if(method != null) {
      return ReflectionUtils.get(method, target);
    }
    
    Field field = meta.fields.get(attribute);
    if(field != null) {
      return ReflectionUtils.get(field, target);
    }

    throw new IllegalArgumentException(String.format(
      "MetaCache.get(%s): Attribute not found: \"%s.%s\"", 
      attribute, target.getClass().getName(), attribute));
  }
  
  public static void set(final String attribute, final Object target, final Object value) {
    Meta meta = getMeta(target.getClass());
    Method method = meta.setters.get(attribute);
    if(method != null) {
      ReflectionUtils.set(method, target, value);
      return;
    }
    
    Field field = meta.fields.get(attribute);
    if(field != null) {
      ReflectionUtils.set(field, target, value);
      return;
    }
    
    throw new IllegalArgumentException(String.format(
      "MetaCache.set(%s): Attribute not found: \"%s.%s\"", 
      attribute, target.getClass().getName(), attribute));
  }

  public static Meta getMeta(Class<?> clazz) {
    Meta meta = metaCache.get(clazz.getName());
    if(meta == null) {
      // To safely create values on demand you must use the putIfAbsent   
      // method and avoid making extra calls to get in the process. 
      // see: http://dmy999.com/article/34/correct-use-of-concurrenthashmap
      Meta newMeta = new Meta(clazz);
      meta = metaCache.putIfAbsent(clazz.getName(), newMeta);
      if(meta == null) {
        // put succeeded, use new value
        meta = newMeta;
      }
    }
    return meta;
  }

  public static void removeMetaFromCache(final String attributeName) {
    metaCache.remove(attributeName);
  }
  
  public static String objectToString(final Object target) {
    final StringBuilder sb = new StringBuilder("{");
    if(target == null) { 
      sb.append("\n}");
    }
    else {
      sb.append(doObjectToString(target, 2));
    }
    sb.append('}');
    return sb.toString();
  }

  /*
   * TODO: Need refactoring 
   */
  private static String doObjectToString(final Object target, int indent) {

    if(target == null) {
      return "";
    }
    
    final Meta meta = getMeta(target.getClass());
    final StringBuilder sb = new StringBuilder();

    sb.append(quote(target.getClass().getSimpleName()) + " : ");
    sb.append("{\n");
    
    int i = 0;
    int j = meta.fields.size();
    
    for (Entry<String, Field> entry : meta.fields.entrySet()) {
      String property = entry.getKey();
      
      sb.append(String.format("%" + (indent+2) + "s", ""));
      
      Field field = entry.getValue();
      Object value = ReflectionUtils.get(field, target);
      Class<?> type = field.getType();
      
      if(isPrimitive(type)) {
        sb.append(quote(property) + " : ");
        sb.append(primitiveToString(value));
      }
      else if(type.isArray()) {
        sb.append(quote(property) + " : [");
        int l = value == null ? 0 : Array.getLength(value);
        for (int k = 0; k < l; k++) {
          Object v = Array.get(value, k);
          if(v != null && isPrimitive(v.getClass())) {
            sb.append(primitiveToString(v));
          }
          else {
            sb.append(doObjectToString(v, indent+2));
          }
          
          int n = sb.length()-1;
          if(sb.charAt(n) == '\n') {
            sb.deleteCharAt(n);
          }
          
          sb.append(k < l-1 ? ", " : "");
        }
        sb.append(']');
      }
      else if(value != null && value instanceof Collection) {
        sb.append(quote(property) + " : [");
        
        Collection<Object> c = (Collection)value;
        for (Object v : c) {
          if(v != null && isPrimitive(v.getClass())) {
            sb.append(primitiveToString(v));
          } 
          else {
            sb.append(doObjectToString(v, indent+2));
          }
          
          int n = sb.length()-1;
          if(sb.charAt(n) == '\n') {
            sb.deleteCharAt(n);
          }
          sb.append(", ");
        }

        int n = sb.length()-1;
        sb.delete(n-1, n);
        
        sb.append(']');
      }
      else if(value != null && value instanceof Map) {
        sb.append(quote(property) + " : {\n");
        
        for (Iterator iter = ((Map) value).entrySet().iterator(); iter.hasNext();) {
          Entry e = (Entry)iter.next();
          
          sb.append(String.format("%" + (indent+4) + "s", ""));
          sb.append(quote(e.getKey().toString()) + " : ");
          Object v = e.getValue();

          if(v != null && isPrimitive(v.getClass())) {
            sb.append(primitiveToString(v));
          } 
          else {
            sb.append(doObjectToString(v, indent+4));
          }
        }
        sb.append(String.format("%" + (indent+2) + "s", ""));
        sb.append("}");
      }
      else if(value != null && value instanceof Object) {
        sb.append(quote(property) + " : ");
        sb.append(doObjectToString(value, indent+2));
      }
      
      if(++i < j) {
        int n = sb.length()-1;
        if(sb.charAt(n) == '\n') {
          sb.deleteCharAt(n);
        }
        sb.append(",\n");
      }
    }

    sb.append(String.format(indent > 0 ? String.format("\n%" + indent + "s}\n", "") : "\n}\n"));
    return sb.toString();
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

  /*
   * 
   */
  public static class Meta {
    //transient Class<?> metaClass;
    transient Map<String, Field> fields;
    transient Map<String, Method> getters;
    transient Map<String, Method> setters;
    
    @SuppressWarnings("unused")
    private Meta() {
    }
    
    public Meta (final Class<?> metaClass) {
      super();
      
      //this.metaClass = metaClass;
      this.fields = new HashMap<String, Field>();
      this.setters = new HashMap<String, Method>();
      this.getters = new HashMap<String, Method>();
      
      for (Class<?> clazz = metaClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
        for (Field field : clazz.getDeclaredFields()) {
          if(!ignore(field)) {
            fields.put(field.getName(), field);
          }
        }
      }

      for (Class<?> clazz = metaClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
        
        for(Method method : clazz.getDeclaredMethods()) {
          
          if(ignore(method)) {
            continue;
          }
          String methodName = method.getName();
          String propertyName = ReflectionUtils.getPropertyName(method);  // <- get, is or set
          Class<?>[] types = method.getParameterTypes();
          
          if((methodName.startsWith("get") || methodName.startsWith("is"))) {
            if((types == null || types.length == 0) && !getters.containsKey(propertyName)) 
              getters.put(propertyName, method);
          }
          else {
            if((types != null && types.length == 1) && !setters.containsKey(propertyName)) 
              setters.put(propertyName, method);
          }
        }
      }
    }
    
    private boolean ignore(final Member member) {
      return Modifier.isStatic(member.getModifiers()) || Modifier.isNative(member.getModifiers());
    }
    
  } // ~Meta  
}
