package no.knowit.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {

  public static AccessibleObject searchForAnnotation(final Class<?> target, 
      final Class<? extends Annotation> annotation) {
    
    if(target == null || annotation == null) return null;
    AccessibleObject result = searchFieldForAnnotation(target, annotation);
    if(result == null) result = searchMethodForAnnotation(target, annotation);
    return result;
  }

  public static List<AccessibleObject> searchForAnnotations(final Class<?> target, 
      final Class<? extends Annotation> annotation) {
    
    if(target == null || annotation == null) return null;
    List<AccessibleObject> result = new ArrayList<AccessibleObject>();
    result.addAll(searchFieldsForAnnotation(target, annotation));
    result.addAll(searchMethodsForAnnotation(target, annotation));
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

  public static List<Field> searchFieldsForAnnotation(final Class<?> target, 
      final Class<? extends Annotation> annotation) {

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

  public static List<Method> searchMethodsForAnnotation(final Class<?> target, 
      final Class<? extends Annotation> annotation) {
    
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
}
