package no.knowit.util;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {

  public static List<Member> searchMembersForAnnotation(
      final Class<? extends Annotation> annotation, final Class<?> target) {
    
    if(target == null || annotation == null) return null;
    List<Member> result = new ArrayList<Member>();
    result.addAll(searchFieldsForAnnotation(annotation, target));
    result.addAll(searchMethodsForAnnotation(annotation, target));
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
  
  public static String getAttributeName(final Member attribute) {
    if(attribute == null) return null;
    return attribute instanceof Field ? getFieldName((Field)attribute) : 
      attribute instanceof Method ? getPropertyName((Method)attribute) : null;
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
  
  public static Object getAttributeValue(final Member attribute, final Object target) {
    return attribute instanceof Field ? getFieldValue((Field)attribute, target) : 
      attribute instanceof Method ? getPropertyValue((Method)attribute, target) : null;
  }
  
  /**
   * Copied/Modified from org.jboss.seam.util.Reflections
   * @param field
   * @param target
   * @return
   */
  public static Object getFieldValue(final Field field, final Object target) {
    if(field == null || target == null) return null;
    
    boolean accessible = field.isAccessible();
    try {
      field.setAccessible(true);
      return field.get(target);
    } 
    catch (Exception e) {
      String message = "Could not get field value by reflection: " + fieldToString(field) + " on: "
          + target.getClass().getName();
      throw new IllegalArgumentException(message, e);
    } 
    finally {
      field.setAccessible(accessible);
    }
  }

  public static Object getPropertyValue(final Method method, final Object target) {
    if(method == null || target == null) return null;
    final String message = "Not a valid getter method: %s on: %s"; 

    String methodName = method.getName();
    if(!(methodName.startsWith("get") || methodName.startsWith("is"))) {
      throw new IllegalArgumentException(
          String.format(message, methodToString(method), target.getClass().getName()));
    }
    if(method.getParameterTypes().length > 0) {
      throw new IllegalArgumentException(message);
    }
    try {
      return method.invoke(target, (Object[])null);
    }
    catch (Exception e) {
      throw new IllegalArgumentException(
          String.format(message, methodToString(method), target.getClass().getName()), e);
    }
  }
  
  
  private static String fieldToString(Field field) {
    return field.getDeclaringClass().getName() + '.' + field.getName(); 
  }
  
  private static String methodToString(Method method) {
    return method.getDeclaringClass().getName() + '.' + method.getName() + "(" + 
      method.getParameterTypes().toString() + ")"; 
  }

  
  
  /*
   * Don't think we need the singular versions but keep them to decide later
   *  
  public static AccessibleObject searchForAnnotation(final Class<?> target, 
      final Class<? extends Annotation> annotation) {
    
    if(target == null || annotation == null) return null;
    AccessibleObject result = searchFieldForAnnotation(target, annotation);
    if(result == null) result = searchMethodForAnnotation(target, annotation);
    return result;
  }

  public static Field searchFieldForAnnotation(final Class<?> target, 
      final Class<? extends Annotation> annotation) {

    if(target != null && annotation != null) {
      for (Class<?> clazz = target; clazz != Object.class; clazz = clazz.getSuperclass()) {
        for (Field field : clazz.getDeclaredFields()) {
          if(field.isAnnotationPresent(annotation)) return field;
        }
      }
    }
    return null;
  }

  public static Method searchMethodForAnnotation(final Class<?> target, 
      final Class<? extends Annotation> annotation) {
    
    if(target != null && annotation != null) {
      for (Class<?> clazz = target; clazz != Object.class; clazz = clazz.getSuperclass()) {
        for (Method method : clazz.getDeclaredMethods()) {
          if(method.isAnnotationPresent(annotation)) return method;
        }
      }
    }
    return null;
  }
   */
}
