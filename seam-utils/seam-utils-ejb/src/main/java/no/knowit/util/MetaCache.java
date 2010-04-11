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
    if(target == null) {
      throw new IllegalArgumentException(String.format("%s can not be null!", "target"));
    }

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
      "MetaCache.get: Attribute not found: \"%s.%s\"", target.getClass().getName(), attribute));
  }
  
  public static void set(final String attribute, final Object target, final Object value) {
    if(target == null) {
      throw new IllegalArgumentException(String.format("%s can not be null!", "target"));
    }
    
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
      "MetaCache.set: Attribute not found: \"%s.%s\"", target.getClass().getName(), attribute));
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
  
  public static void clearMetaCache() {
    metaCache.clear();
  }
  
  /*
   * 
   */
  public static class Meta {
    transient Class<?> metaClass;
    transient Map<String, Field>  fields;
    transient Map<String, Method> getters;
    transient Map<String, Method> setters;
    
    @SuppressWarnings("unused")
    private Meta() {
    }
    
    public Meta (final Class<?> targetClass) {
      super();
      
      this.metaClass = targetClass;
      this.fields = new HashMap<String, Field>();
      this.setters = new HashMap<String, Method>();
      this.getters = new HashMap<String, Method>();
      
      for (Class<?> clazz = targetClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
        for (Field field : clazz.getDeclaredFields()) {
          if(!ignore(field)) {
            fields.put(field.getName(), field);
          }
        }
      }

      for (Class<?> clazz = targetClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
        
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
