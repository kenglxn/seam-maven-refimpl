package no.knowit.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Version;

import org.testng.Assert;
import org.testng.annotations.Test;

import no.knowit.testsupport.model.ConcreteEntityFieldAnnotated;
import no.knowit.testsupport.model.ConcreteEntityPropertyAnnotated;
import no.knowit.testsupport.model.SimpleEntityFieldAnnotated;
import no.knowit.testsupport.model.SimpleEntityPropertyAnnotated;

public class ReflectionUtilsTest {
  
  @Test
  public void testSearchFieldForAnnotation() throws Exception {
    Field field = ReflectionUtils.searchFieldForAnnotation(
        SimpleEntityFieldAnnotated.class, Id.class);
    Assert.assertNotNull(field);
  }

  @Test
  public void testSearchMethodForAnnotation() {
    Method method = ReflectionUtils.searchMethodForAnnotation(
        SimpleEntityPropertyAnnotated.class, Id.class);
    Assert.assertNotNull(method);
  }

  @Test
  public void testSearchForAnnotation() {
    AccessibleObject accessibleObject = ReflectionUtils.searchForAnnotation(
        ConcreteEntityPropertyAnnotated.class, Version.class);
    Assert.assertNotNull(accessibleObject);
    
    accessibleObject = ReflectionUtils.searchForAnnotation(
        ConcreteEntityFieldAnnotated.class, Id.class);
    Assert.assertNotNull(accessibleObject);
  }
  
  @Test
  public void testSearchFieldsForAnnotation() throws Exception {
    List<Field> fields = ReflectionUtils.searchFieldsForAnnotation(
        SimpleEntityFieldAnnotated.class, Id.class);
    Assert.assertTrue(fields.size() > 0);
  }

  @Test
  public void testSearchMethodsForAnnotation() {
    List<Method> methods = ReflectionUtils.searchMethodsForAnnotation(
        SimpleEntityPropertyAnnotated.class, Id.class);
    Assert.assertTrue(methods.size() > 0);
  }

  @Test
  public void testSearchForAnnotations() {
    List<AccessibleObject> accessibleObjects = ReflectionUtils.searchForAnnotations(
        ConcreteEntityPropertyAnnotated.class, Version.class);
    Assert.assertTrue(accessibleObjects.size() > 0);
    
    accessibleObjects = ReflectionUtils.searchForAnnotations(
        ConcreteEntityFieldAnnotated.class, Id.class);
    Assert.assertTrue(accessibleObjects.size() > 0);
  }

}
