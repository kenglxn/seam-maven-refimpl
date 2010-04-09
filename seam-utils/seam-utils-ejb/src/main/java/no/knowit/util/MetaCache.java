package no.knowit.util;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author LeifOO
 *
 */
public class MetaCache {
  
  // TODO: Must be thread safe??
  private static transient final Map<String, Meta> metaCache = new HashMap<String, Meta>(); 

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
      "Could not get attribute: %s.%s", target.getClass().getName(), attribute));
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
      "Cold not set attribute: %s.%s", target.getClass().getName(), attribute));
  }

  public static Meta getMeta(Class<?> clazz) {
    Meta meta = metaCache.get(clazz.getName());
    if(meta == null) {
      meta = new Meta(clazz);
      metaCache.put(clazz.getName(), meta);
    }
    return meta;
  }

  public static void removeMetaFromCache(final String attributeName) {
    metaCache.remove(attributeName);
  }

  /*
   * 
   */
  private static class Meta {
    //transient Class<?> metaClass;
    transient Map<String, Field> fields = null;
    transient Map<String, Method> getters = null;
    transient Map<String, Method> setters = null;
    
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
          String propertyName = ReflectionUtils.getPropertyName(method);
          Class<?>[] types = method.getParameterTypes();
          
          if(methodName.startsWith("get") || methodName.startsWith("is")) {
            if((types == null || types.length == 0) &&  !getters.containsKey(propertyName)) 
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
