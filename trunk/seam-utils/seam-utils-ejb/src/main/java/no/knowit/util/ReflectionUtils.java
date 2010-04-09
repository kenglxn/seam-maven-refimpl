package no.knowit.util;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A lot of this code is copied/modified from 
 * <tt>org.jboss.seam.util.Reflections.java</tt>, 
 * <tt>org.jboss.seam.persistence.ManagedEntityWrapper</tt> and 
 * <tt>org.crank.core.AnnotationUtils.java</tt>
 */
public class ReflectionUtils {
  
//  public static final List<String> OBJECT_PRIMITIVES = Arrays.asList(
//      "java.lang.String",    "java.lang.Boolean",  "java.lang.Byte",
//      "java.lang.Character", "java.lang.Double",   "java.lang.Float",
//      "java.lang.Integer",   "java.lang.Long",     "java.lang.Number",
//      "java.lang.Short",     "java.util.Currency", "java.util.Date",
//      "java.sql.Date",       "java.sql.Time",      "java.sql.Timestamp" 
//  );

  public static final List<Class<? extends Serializable>> OBJECT_PRIMITIVES = Arrays.asList(
      java.lang.String.class,    java.lang.Boolean.class,  java.lang.Byte.class,
      java.lang.Character.class, java.lang.Double.class,   java.lang.Float.class,
      java.lang.Integer.class,   java.lang.Long.class,     java.lang.Number.class,
      java.lang.Short.class,     java.util.Currency.class, java.util.Date.class,
      java.sql.Date.class,       java.sql.Time.class,      java.sql.Timestamp.class
  );
  
  private ReflectionUtils() {
    ;
  }
  
  public static Object get(final Member member, final Object target) {
    return member instanceof Field ? get((Field)member, target) : get((Method)member, target);
  }
  
  public static Object get(final Field field, final Object target) {
    if(field == null || target == null) { 
      return null;
    }
    
    boolean accessible = field.isAccessible();
    try {
      field.setAccessible(true);
      return field.get(target);
    } 
    catch (Exception e) {
      throw new IllegalArgumentException(
        "Could not get field value by reflection: " + ReflectionUtils.fieldToString(field), e);
    } 
    finally {
      field.setAccessible(accessible);
    }
  }

  public static Object get(final Method method, final Object target) {
    
    if(method == null || target == null) { 
      return null;
    }
    
    //final Object[] noargs = (Object[]) null;
    boolean accessible = method.isAccessible();
    try {
      method.setAccessible(true);
      return method.invoke(target, (Object[])null);
    }
    catch (Exception e) {
      throw new IllegalArgumentException(
        "Not a valid getter method: %s" + ReflectionUtils.methodToString(method), e);
    }
    finally {
      method.setAccessible(accessible);
    }
  }

  public static void set(final Member member, final Object target, final Object value) {
    if(member instanceof Field){ 
      set((Field)member, target, value);
    }
    else {
      set((Method)member, target, value); 
    }
  }
  
  public static void set(final Field field, final Object target, final Object value) {
    
    if(field == null || target == null) {
      throw new IllegalArgumentException(
        String.format("%s can not be null!", field==null ? "field" : "target"));
    }
    
    boolean accessible = field.isAccessible();
    try {
      field.setAccessible(true);
      field.set(target, value);
    } 
    catch (Exception e) {
      throw new IllegalArgumentException(
        "Could not set field value by reflection: " + ReflectionUtils.fieldToString(field), e);
    } 
    finally {
      field.setAccessible(accessible);
    }
  }

  public static void set(final Method method, final Object target, final Object value) {
    if(method == null || target == null) {
      throw new IllegalArgumentException(
        String.format("%s can not be null!", method==null ? "method" : "target"));
    }
    
    boolean accessible = method.isAccessible();
    try {
      method.setAccessible(true);
      method.invoke(target, value);
    }
    catch (Exception e) {
      throw new IllegalArgumentException(
          "Not a valid setter method: " + ReflectionUtils.methodToString(method), e);
    }
    finally {
      method.setAccessible(accessible);
    }
  }

  public static List<Member> searchMembersForAnnotation(
      final Class<? extends Annotation> annotation, final Class<?> target) {
    
    List<Member> result = new ArrayList<Member>();
    if(target != null && annotation != null) {
      result.addAll(searchFieldsForAnnotation(annotation, target));
      result.addAll(searchMethodsForAnnotation(annotation, target));
    }
    return result;
  }

  public static Member searchMembersForFirstAnnotation(
      final Class<? extends Annotation> annotation, final Class<?> target) {
    
    Member result = null;
    if(target != null && annotation != null) {
      result = searcFieldsForFirstAnnotation(annotation, target);
      if(result == null)
        result = searcMethodsForFirstAnnotation(annotation, target);
    }
    return result;
  }

  public static List<Field> searchFieldsForAnnotation( 
      final Class<? extends Annotation> annotation, final Class<?> target) {

    List<Field> fields = new ArrayList<Field>();
    if(target != null && annotation != null) {
      for (Class<?> clazz = target; clazz != Object.class; clazz = clazz.getSuperclass()) {
        for (Field field : clazz.getDeclaredFields()) {
          if(field.isAnnotationPresent(annotation)) fields.add(field);
        }
      }
    }
    return fields;
  }
  
  public static Field searcFieldsForFirstAnnotation(
      final Class<? extends Annotation> annotation, final Class<?> target) {

    if(target != null && annotation != null) {
      for (Class<?> clazz = target; clazz != Object.class; clazz = clazz.getSuperclass()) {
        for (Field field : clazz.getDeclaredFields()) {
          if(field.isAnnotationPresent(annotation)) return field;
        }
      }
    }
    return null;
  }

  public static List<Method> searchMethodsForAnnotation( 
      final Class<? extends Annotation> annotation, final Class<?> target) {
    
    List<Method> methods = new ArrayList<Method>();
    if(target != null && annotation != null) {
      for (Class<?> clazz = target; clazz != Object.class; clazz = clazz.getSuperclass()) {
        for (Method method : clazz.getDeclaredMethods()) {
          if(method.isAnnotationPresent(annotation)) methods.add(method);
        }
      }
    }
    return methods;
  }
  
  public static Method searcMethodsForFirstAnnotation(
      final Class<? extends Annotation> annotation, final Class<?> target) {
 
    if(target != null && annotation != null) {
      for (Class<?> clazz = target; clazz != Object.class; clazz = clazz.getSuperclass()) {
        for (Method method : clazz.getDeclaredMethods()) {
          if(method.isAnnotationPresent(annotation)) return method;
        }
      }
    }
    return null;
  }

  public static String memberToString(Member member) {
    return member instanceof Field ? fieldToString((Field)member) : 
      member instanceof Method ? methodToString((Method)member) : null;
  }
  
  public static String fieldToString(Field field) {
    return field.getDeclaringClass().getName() + '.' + field.getName(); 
  }
  
  public static String methodToString(Method method) {
    return method.getDeclaringClass().getName() + '.' + method.getName() + "(" + 
      method.getParameterTypes().toString() + ")"; 
  }

  public static String getMemberName(final Member member) {
    if(member == null) return null;
    return member instanceof Field ? getFieldName((Field)member) : 
      member instanceof Method ? getPropertyName((Method)member) : null;
  }
  
  public static String getFieldName(final Field field) {
    return field == null ? null : field.getName();
  }

  public static String getPropertyName(final Method method) {
    if(method == null) return null;
    String methodName = method.getName();
    if(methodName.startsWith("get") || methodName.startsWith("set")) {
      return Introspector.decapitalize(methodName.substring(3));
    }
    if(methodName.startsWith("is")) {
      return Introspector.decapitalize(methodName.substring(2));
    }
    return null;
  }

  public static Method getMethod(final String methodName, final Class<?> clazz) {
    for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
      try {
        return c.getDeclaredMethod(methodName);
      } 
      catch (NoSuchMethodException nsme) {
      }
    }
    throw new IllegalArgumentException("No such method: " + clazz.getName() + '.' + methodName);
  }
  
}
