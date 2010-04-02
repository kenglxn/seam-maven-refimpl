package no.knowit.util;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Version;

import org.testng.Assert;
import org.testng.annotations.Test;

import no.knowit.testsupport.model.ConcreteEntityFieldAnnotated;
import no.knowit.testsupport.model.ConcreteEntityPropertyAnnotated;
import no.knowit.testsupport.model.FieldAnnotatedEntity;
import no.knowit.testsupport.model.PropertyAnnotatedEntity;

public class ReflectionUtilsTest {
  
  @Test
  public void testSearchFieldsForAnnotation() throws Exception {
    List<Field> fields = ReflectionUtils.searchFieldsForAnnotation(
        Id.class, FieldAnnotatedEntity.class);
    Assert.assertTrue(fields.size() > 0);
  }

  @Test
  public void testSearchMethodsForAnnotation() throws Exception {
    List<Method> methods = ReflectionUtils.searchMethodsForAnnotation(
        Id.class, PropertyAnnotatedEntity.class);
    Assert.assertTrue(methods.size() > 0);
  }

  @Test
  public void testSearchForAnnotations() throws Exception {
    List<Member> accessibleObjects = ReflectionUtils.searchMembersForAnnotation(
        Version.class, ConcreteEntityPropertyAnnotated.class);
    Assert.assertTrue(accessibleObjects.size() > 0);
    
    accessibleObjects = ReflectionUtils.searchMembersForAnnotation(
        Id.class, ConcreteEntityFieldAnnotated.class);
    Assert.assertTrue(accessibleObjects.size() > 0);
  }
  
}
