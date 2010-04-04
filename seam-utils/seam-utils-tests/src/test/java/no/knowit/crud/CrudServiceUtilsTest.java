package no.knowit.crud;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import no.knowit.openejb.mock.OpenEjbTest;
import no.knowit.testsupport.bean.SimpleBean;
import no.knowit.testsupport.model.ConcreteEntityFieldAnnotated;
import no.knowit.testsupport.model.ConcreteEntityPropertyAnnotated;
import no.knowit.testsupport.model.NamedEntity;
import no.knowit.testsupport.model.FieldAnnotatedEntity;
import no.knowit.testsupport.model.PropertyAnnotatedEntity;
import no.knowit.testsupport.model.inheritance.ContractEmployee;
import no.knowit.testsupport.model.inheritance.FullTimeEmployee;
import no.knowit.testsupport.model.inheritance.PartTimeEmployee;
import no.knowit.util.ReflectionUtils;

public class CrudServiceUtilsTest  extends OpenEjbTest {
//  private static Logger log = Logger.getLogger(CrudServiceUtils.class);
//
//  @BeforeSuite
//  public void beforeSuite() throws Exception {
//    contextProperties.put("log4j.category.no.knowit.crud", "debug");
//    contextProperties.put("log4j.category.no.knowit.testsupport", "debug");
//    super.beforeSuite();
//  }
  
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
  public void shouldHaveIdAnnotation() throws Exception {
    Assert.assertTrue(CrudServiceUtils.hasId(ConcreteEntityFieldAnnotated.class));
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
    List<String> expectedAttributes = Arrays.asList("id", "foo", "baz");
    Map<String, Member> actualAttributes;
    
    actualAttributes= CrudServiceUtils.findQueryableAttributes(FieldAnnotatedEntity.class);
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
    
    
    expectedAttributes = Arrays.asList("id", "startDate", "term", "name", "dailyRate");
    actualAttributes = CrudServiceUtils.findQueryableAttributes(ContractEmployee.class);
    Assert.assertTrue(actualAttributes.keySet().containsAll(expectedAttributes),
        String.format(message, actualAttributes.keySet(), expectedAttributes));

    expectedAttributes = Arrays.asList("id", "startDate", "hourlyRate", "name", "vacation");
    actualAttributes = CrudServiceUtils.findQueryableAttributes(PartTimeEmployee.class);
    Assert.assertTrue(actualAttributes.keySet().containsAll(expectedAttributes),
        String.format(message, actualAttributes.keySet(), expectedAttributes));

    expectedAttributes = Arrays.asList("id", "startDate", "name", "vacation", "salary", "pension");
    actualAttributes = CrudServiceUtils.findQueryableAttributes(FullTimeEmployee.class);
    Assert.assertTrue(actualAttributes.keySet().containsAll(expectedAttributes),
        String.format(message, actualAttributes.keySet(), expectedAttributes));
  }

  @Test
  public void shouldCreateJpqlWithAndOperator() throws Exception {
    final String expectedSelect = "SELECT e FROM %s e";
    
    FieldAnnotatedEntity fe = new FieldAnnotatedEntity();
    String jpql = CrudServiceUtils.createJpql(fe, true, false, false);
    String expectedJPQL = String.format(expectedSelect, fe.getClass().getName());
    Assert.assertEquals(jpql, expectedJPQL);

    PropertyAnnotatedEntity pe = new PropertyAnnotatedEntity();
    jpql = CrudServiceUtils.createJpql(pe, true, false, false);
    expectedJPQL = String.format(expectedSelect, pe.getClass().getName());
    Assert.assertEquals(jpql, expectedJPQL);
    
    fe = new FieldAnnotatedEntity(100);
    jpql = CrudServiceUtils.createJpql(fe, true, false, false);
    expectedJPQL = String.format(expectedSelect, fe.getClass().getName()) + " WHERE e.foo = :foo";
    Assert.assertEquals(jpql, expectedJPQL);

    fe = new FieldAnnotatedEntity(100, "HELLO");
    jpql = CrudServiceUtils.createJpql(fe, true, false, false);
    expectedJPQL = String.format(expectedSelect, fe.getClass().getName());
    boolean expected = jpql.startsWith(expectedJPQL) && 
      jpql.contains("e.baz = :baz") && jpql.contains("AND") && jpql.contains("e.foo = :foo");
    
    Assert.assertTrue(expected, "Actual JPQL was not expected: [" + jpql + "]");
  }

  @Test
  public void shouldCreateJpqlWithOrOperator() throws Exception {
    final String expectedSelect = "SELECT e FROM %s e";

    FieldAnnotatedEntity fe = new FieldAnnotatedEntity(100, "HELLO");
    String jpql = CrudServiceUtils.createJpql(fe, true, false, true);
    String expectedJPQL = String.format(expectedSelect, fe.getClass().getName());
    boolean expected = jpql.startsWith(expectedJPQL) && jpql.contains("WHERE") &&
      jpql.contains("e.baz = :baz") && jpql.contains("OR") && jpql.contains("e.foo = :foo");
    
    Assert.assertTrue(expected, "Actual JPQL was not expected: [" + jpql + "]");
  }
  
  @Test
  public void shouldCreateDistinctJpql() throws Exception {
    final String expectedSelect = "SELECT DISTINCT e FROM %s e";

    FieldAnnotatedEntity fe = new FieldAnnotatedEntity(100, "HELLO");
    String jpql = CrudServiceUtils.createJpql(fe, true, true, true);
    String expectedJPQL = String.format(expectedSelect, fe.getClass().getName());
    boolean expected = jpql.startsWith(expectedJPQL) && jpql.contains("WHERE") && 
      jpql.contains("e.baz = :baz") && jpql.contains("OR") && jpql.contains("e.foo = :foo");
    
    Assert.assertTrue(expected, "Actual JPQL was not expected: [" + jpql + "]");
  }

  @Test
  public void shouldCreateWildcardJpql() throws Exception {
    final String expectedSelect = "SELECT e FROM %s e";

    FieldAnnotatedEntity fe = new FieldAnnotatedEntity(100, "HELL%");
    String jpql = CrudServiceUtils.createJpql(fe, true, false, true);
    String expectedJPQL = String.format(expectedSelect, fe.getClass().getName());
    boolean expected = jpql.startsWith(expectedJPQL) && jpql.contains("WHERE") && 
      jpql.contains("e.baz LIKE :baz") && jpql.contains("OR") && jpql.contains("e.foo = :foo");
    
    Assert.assertTrue(expected, "Actual JPQL was not expected: [" + jpql + "]");
    
    PropertyAnnotatedEntity pe = new PropertyAnnotatedEntity(100, "HELL%");
    jpql = CrudServiceUtils.createJpql(pe, true, false, true);
    expectedJPQL = String.format(expectedSelect, pe.getClass().getName());
    expected = jpql.startsWith(expectedJPQL) && jpql.contains("WHERE") && 
      jpql.contains("e.baz LIKE :baz") && jpql.contains("OR") && jpql.contains("e.foo = :foo");
    
    Assert.assertTrue(expected, "Actual JPQL was not expected: [" + jpql + "]");
  }
  
  @Test
  public void shouldCreateDeleteJpql() throws Exception {
    final String expectedDelete = "DELETE FROM %s e";

    FieldAnnotatedEntity fe = new FieldAnnotatedEntity(100, "HELLO");
    String jpql = CrudServiceUtils.createJpql(fe, false, false, true);
    String expectedJPQL = String.format(expectedDelete, fe.getClass().getName());
    boolean expected = jpql.startsWith(expectedJPQL) && 
      jpql.contains("e.baz = :baz") && jpql.contains("OR") && jpql.contains("e.foo = :foo");
    
    Assert.assertTrue(expected, "Actual JPQL was not expected: [" + jpql + "]");

    
    PropertyAnnotatedEntity pe = new PropertyAnnotatedEntity(100, "HELL%");
    jpql = CrudServiceUtils.createJpql(pe, false, false, true);
    expectedJPQL = String.format(expectedDelete, pe.getClass().getName());
    expected = jpql.startsWith(expectedJPQL) && 
      jpql.contains("e.baz LIKE :baz") && jpql.contains("OR") && jpql.contains("e.foo = :foo");
    
    Assert.assertTrue(expected, "Actual JPQL was not expected: [" + jpql + "]");
  }
  
}
