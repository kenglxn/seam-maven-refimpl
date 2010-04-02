package no.knowit.crud;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import no.knowit.openejb.mock.OpenEjbTest;
import no.knowit.testsupport.bean.SimpleBean;
import no.knowit.testsupport.model.ConcreteEntityFieldAnnotated;
import no.knowit.testsupport.model.ConcreteEntityPropertyAnnotated;
import no.knowit.testsupport.model.NamedEntity;
import no.knowit.testsupport.model.FieldAnnotatedEntity;
import no.knowit.testsupport.model.PropertyAnnotatedEntity;
import no.knowit.util.ReflectionUtils;

public class CrudServiceUtilsTest  extends OpenEjbTest {
  private static Logger log = Logger.getLogger(CrudServiceUtils.class);

  @BeforeSuite
  public void beforeSuite() throws Exception {
    contextProperties.put("log4j.category.no.knowit.crud", "debug");
    contextProperties.put("log4j.category.no.knowit.testsupport", "debug");
    super.beforeSuite();
  }
  
  @Test
  public void shouldBeAnEntity() throws Exception {
    Assert.assertTrue(CrudServiceUtils.isEntity(FieldAnnotatedEntity.class), 
        "Expected class with @Entity annotation");
  }
  
  @Test
  public void shouldNotBeAnEntity() throws Exception {
    Assert.assertFalse(CrudServiceUtils.isEntity(SimpleBean.class), 
        "Expected class without @Entity annotation");
  }
  
  @Test
  public void shouldGetEntityName() throws Exception {
    final String expectedNameForNamedEntity = "aNamedEntity";
    Assert.assertEquals(
        CrudServiceUtils.getEntityName(NamedEntity.class), expectedNameForNamedEntity);
    
    final FieldAnnotatedEntity unnamedEntity = new FieldAnnotatedEntity();
    final String expectedNameForUnnamedEntity = unnamedEntity.getClass().getSimpleName();
    Assert.assertEquals(
        CrudServiceUtils.getEntityName(unnamedEntity.getClass()), expectedNameForUnnamedEntity);
  }
  
  @Test
  public void shouldGetIdAnnotationFromField() throws Exception {
    // @Id annotated on field id
    final String expectedIdAttributeName = "id";
    List<Member> id = CrudServiceUtils.getIdAnnotations(FieldAnnotatedEntity.class);
    Assert.assertTrue(id.size() > 0, "No @Id annotation");
    Assert.assertTrue(id.get(0) instanceof Field, "@Id annotation is not on a field");
    Assert.assertEquals(ReflectionUtils.getAttributeName(id.get(0)), expectedIdAttributeName);
  }

  @Test
  public void shouldGetIdAnnotationFromMethod() throws Exception {
    // @Id annotated on method getIdentity 
    final String expectedIdAttributeName = "identity";
    List<Member> id = CrudServiceUtils.getIdAnnotations(ConcreteEntityPropertyAnnotated.class);
    Assert.assertTrue(id.size() > 0, "No @Id annotation");
    Assert.assertTrue(id.get(0) instanceof Method, "@Id annotation is not on a method");
    Assert.assertEquals(ReflectionUtils.getAttributeName(id.get(0)), expectedIdAttributeName);
  }
  
  @Test
  public void shouldFindQueriableAttributes() throws Exception {
    final String message = "Queryable attributes: %s did not match expected: %s";
    
    List<String> expectedAttributes = Arrays.asList("id", "bar", "foo");
    
    Map<String, Member> actualAttributes = CrudServiceUtils.findQueryableAttributes(FieldAnnotatedEntity.class);
    Assert.assertTrue(actualAttributes.keySet().containsAll(expectedAttributes),
        String.format(message, actualAttributes.keySet(), expectedAttributes));
    
    actualAttributes = CrudServiceUtils.findQueryableAttributes(PropertyAnnotatedEntity.class);
    Assert.assertEqualsNoOrder(actualAttributes.keySet().toArray(), expectedAttributes.toArray(),
        String.format(message, actualAttributes.keySet(), expectedAttributes));

    
    expectedAttributes = Arrays.asList("identity", "name", "dateOfBirth");
    
    actualAttributes = CrudServiceUtils.findQueryableAttributes(ConcreteEntityFieldAnnotated.class);
    Assert.assertTrue(actualAttributes.keySet().containsAll(expectedAttributes),
        String.format(message, actualAttributes.keySet(), expectedAttributes));
    
    actualAttributes = CrudServiceUtils.findQueryableAttributes(ConcreteEntityPropertyAnnotated.class);
    Assert.assertEqualsNoOrder(actualAttributes.keySet().toArray(), expectedAttributes.toArray(),
        String.format(message, actualAttributes.keySet(), expectedAttributes));
  }

  @Test
  public void idShouldNotBeNullAfterPersist() throws Exception {
    CrudService crudService = lookup(CrudService.NAME);
    
    FieldAnnotatedEntity fe = new FieldAnnotatedEntity(100);
    fe = crudService.persist(fe);
    Assert.assertNotNull(fe, "Entity was null after persist");
    List<Object> id = CrudServiceUtils.getIdValues(fe);
    Assert.assertNotNull(id.get(0), "Identity value should not be null after persist");
    
    PropertyAnnotatedEntity pe = new PropertyAnnotatedEntity(100);
    pe = crudService.persist(pe);
    Assert.assertNotNull(pe, "Entity was null after persist");
    id = CrudServiceUtils.getIdValues(pe);
    Assert.assertNotNull(id.get(0), "Identity value should not be null after persist");
  }
  
}
