package no.knowit.crud;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import no.knowit.testsupport.bean.BeanWithPrimitives;
import no.knowit.testsupport.model.ConcreteEntityFieldAnnotated;
import no.knowit.testsupport.model.ConcreteEntityPropertyAnnotated;
import no.knowit.testsupport.model.FieldAnnotatedEntity;
import no.knowit.testsupport.model.Movie;
import no.knowit.testsupport.model.NamedEntity;
import no.knowit.testsupport.model.PropertyAnnotatedEntity;
import no.knowit.testsupport.model.inheritance.ContractEmployee;
import no.knowit.testsupport.model.inheritance.FullTimeEmployee;
import no.knowit.testsupport.model.inheritance.PartTimeEmployee;
import no.knowit.util.ReflectionUtils;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CrudServiceUtilsTest {

  @Test
  public void shouldBeAnEntity() throws Exception {
    Assert.assertTrue(CrudServiceUtils.isEntity(FieldAnnotatedEntity.class), 
        "Expected class with @Entity annotation");
  }
  
  @Test
  public void shouldNotBeAnEntity() throws Exception {
    Assert.assertFalse(CrudServiceUtils.isEntity(BeanWithPrimitives.class), 
        "Expected class without @Entity annotation");
  }
  
  @Test
  public void shouldGetEntityName() throws Exception {
    final String expectedNameForNamedEntity = "ANamedEntity";
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
    Assert.assertEquals(ReflectionUtils.getMemberName(id.get(0)), expectedIdAttributeName);
  }

  @Test
  public void shouldGetIdAnnotationFromMethod() throws Exception {
    // @Id annotated on method getIdentity 
    final String expectedIdAttributeName = "identity";
    List<Member> id = CrudServiceUtils.getIdAnnotations(ConcreteEntityPropertyAnnotated.class);
    Assert.assertTrue(id.size() > 0, "No @Id annotation");
    Assert.assertTrue(id.get(0) instanceof Method, "@Id annotation is not on a method");
    Assert.assertEquals(ReflectionUtils.getMemberName(id.get(0)), expectedIdAttributeName);
  }
  
  @Test
  public void shouldFindQueriableAttributes() throws Exception {
    final String message = "Queryable attributes: %s did not match expected attributes: %s";
    List<String> expectedAttributes = Arrays.asList("id", "foo", "baz", "color");
    Map<String, Member> actualAttributes;
    
    actualAttributes= CrudServiceUtils.findQueryableAttributes(FieldAnnotatedEntity.class);
    Assert.assertTrue(actualAttributes.keySet().containsAll(expectedAttributes),
        String.format(message, actualAttributes.keySet(), expectedAttributes));
    
    actualAttributes = CrudServiceUtils.findQueryableAttributes(PropertyAnnotatedEntity.class);
    Assert.assertEqualsNoOrder(actualAttributes.keySet().toArray(), expectedAttributes.toArray(),
        String.format(message, actualAttributes.keySet(), expectedAttributes));

    
    expectedAttributes = Arrays.asList("identity", "name", "dateOfBirth", "color");
    
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
  public void shouldCreateSelectJpql() throws Exception {
    final String expectedSelect = "SELECT e FROM %s e";
    
    FieldAnnotatedEntity fe = new FieldAnnotatedEntity();
    String jpql = CrudServiceUtils.createJpql(fe, true, false, false);
    String expectedJPQL = String.format(expectedSelect, CrudServiceUtils.getEntityName(fe.getClass()));
    Assert.assertEquals(jpql, expectedJPQL);

    PropertyAnnotatedEntity pe = new PropertyAnnotatedEntity();
    jpql = CrudServiceUtils.createJpql(pe, true, false, false);
    expectedJPQL = String.format(expectedSelect, CrudServiceUtils.getEntityName(pe.getClass()));
    Assert.assertEquals(jpql, expectedJPQL);
  }
  
  @Test
  public void shouldCreateSelectJpqlWithWhereClause() throws Exception {
    final String expectedSelect = "SELECT e FROM %s e WHERE e.foo = :foo";
    
    FieldAnnotatedEntity fe = new FieldAnnotatedEntity(100);
    String jpql = CrudServiceUtils.createJpql(fe, true, false, false);
    String expectedJPQL = String.format(expectedSelect, CrudServiceUtils.getEntityName(fe.getClass()));
    Assert.assertEquals(jpql, expectedJPQL);
  }

  @Test
  public void shouldCreateSelectJpqlWithEnum() throws Exception {
    final String expectedSelect = "SELECT e FROM %s e WHERE e.color = :color";

    FieldAnnotatedEntity fe = new FieldAnnotatedEntity(FieldAnnotatedEntity.Color.GREEN);
    String jpql = CrudServiceUtils.createJpql(fe, true, false, false);
    String expectedJPQL = String.format(expectedSelect, CrudServiceUtils.getEntityName(fe.getClass()));
    Assert.assertEquals(jpql, expectedJPQL);
    
    PropertyAnnotatedEntity pe = new PropertyAnnotatedEntity(PropertyAnnotatedEntity.Color.GREEN);
    jpql = CrudServiceUtils.createJpql(pe, true, false, false);
    expectedJPQL = String.format(expectedSelect, CrudServiceUtils.getEntityName(pe.getClass()));
    Assert.assertEquals(jpql, expectedJPQL);
  }
  
  @Test
  public void shouldCreateSelectJpqlWithAndOperator() throws Exception {
    final String expectedSelect = "SELECT e FROM %s e";
    
    FieldAnnotatedEntity fe = new FieldAnnotatedEntity(100, "HELLO");
    String jpql = CrudServiceUtils.createJpql(fe, true, false, false);
    String expectedJPQL = String.format(expectedSelect, CrudServiceUtils.getEntityName(fe.getClass()));
    boolean expected = jpql.startsWith(expectedJPQL) && 
      jpql.contains("e.baz = :baz") && jpql.contains("AND") && jpql.contains("e.foo = :foo");
    
    Assert.assertTrue(expected, "Actual JPQL was not expected JPQL: [" + jpql + "]");
  }

  @Test
  public void shouldCreateSelectJpqlWithOrOperator() throws Exception {
    final String expectedSelect = "SELECT e FROM %s e";

    FieldAnnotatedEntity fe = new FieldAnnotatedEntity(100, "HELLO");
    String jpql = CrudServiceUtils.createJpql(fe, true, false, true);
    String expectedJPQL = String.format(expectedSelect, CrudServiceUtils.getEntityName(fe.getClass()));
    boolean expected = jpql.startsWith(expectedJPQL) && jpql.contains("WHERE") &&
      jpql.contains("e.baz = :baz") && jpql.contains("OR") && jpql.contains("e.foo = :foo");
    
    Assert.assertTrue(expected, "Actual JPQL was not expected JPQL: [" + jpql + "]");
  }
  
  @Test
  public void shouldCreateSelectDistinctJpql() throws Exception {
    final String expectedSelect = "SELECT DISTINCT e FROM %s e";

    FieldAnnotatedEntity fe = new FieldAnnotatedEntity(100, "HELLO");
    String jpql = CrudServiceUtils.createJpql(fe, true, true, true);
    String expectedJPQL = String.format(expectedSelect, CrudServiceUtils.getEntityName(fe.getClass()));
    boolean expected = jpql.startsWith(expectedJPQL) && jpql.contains("WHERE") && 
      jpql.contains("e.baz = :baz") && jpql.contains("OR") && jpql.contains("e.foo = :foo");
    
    Assert.assertTrue(expected, "Actual JPQL was not expected JPQL: [" + jpql + "]");
  }

  @Test
  public void shouldCreateSelectJpqlWithWildcard() throws Exception {
    final String expectedSelect = "SELECT e FROM %s e";

    FieldAnnotatedEntity fe = new FieldAnnotatedEntity(100, "HELL%");
    String jpql = CrudServiceUtils.createJpql(fe, true, false, true);
    String expectedJPQL = String.format(expectedSelect, CrudServiceUtils.getEntityName(fe.getClass()));
    boolean expected = jpql.startsWith(expectedJPQL) && jpql.contains("WHERE") && 
      jpql.contains("e.baz LIKE :baz") && jpql.contains("OR") && jpql.contains("e.foo = :foo");
    
    Assert.assertTrue(expected, "Actual JPQL was not expected JPQL: [" + jpql + "]");
    
    PropertyAnnotatedEntity pe = new PropertyAnnotatedEntity(100, "HELL%");
    jpql = CrudServiceUtils.createJpql(pe, true, false, true);
    expectedJPQL = String.format(expectedSelect, CrudServiceUtils.getEntityName(pe.getClass()));
    expected = jpql.startsWith(expectedJPQL) && jpql.contains("WHERE") && 
      jpql.contains("e.baz LIKE :baz") && jpql.contains("OR") && jpql.contains("e.foo = :foo");
    
    Assert.assertTrue(expected, "Actual JPQL was not expected JPQL: [" + jpql + "]");
  }
  
  @Test
  public void shouldCreateDeleteJpql() throws Exception {
    final String expectedDelete = "DELETE FROM %s e";

    FieldAnnotatedEntity fe = new FieldAnnotatedEntity(100, "HELLO");
    String jpql = CrudServiceUtils.createJpql(fe, false, false, true);
    String expectedJPQL = String.format(expectedDelete, CrudServiceUtils.getEntityName(fe.getClass()));
    boolean expected = jpql.startsWith(expectedJPQL) && 
      jpql.contains("e.baz = :baz") && jpql.contains("OR") && jpql.contains("e.foo = :foo");
    
    Assert.assertTrue(expected, "Actual JPQL was not expected JPQL: [" + jpql + "]");

    
    PropertyAnnotatedEntity pe = new PropertyAnnotatedEntity(100, "HELL%");
    jpql = CrudServiceUtils.createJpql(pe, false, false, true);
    expectedJPQL = String.format(expectedDelete, CrudServiceUtils.getEntityName(pe.getClass()));
    expected = jpql.startsWith(expectedJPQL) && 
      jpql.contains("e.baz LIKE :baz") && jpql.contains("OR") && jpql.contains("e.foo = :foo");
    
    Assert.assertTrue(expected, "Actual JPQL was not expected JPQL: [" + jpql + "]");
  }
  
  @Test
  public void shouldCreateCountJpql() throws Exception {
    final String expected = "SELECT count(*) FROM %s";
    String countJpql = CrudServiceUtils.createCountJpql(Movie.class);
    Assert.assertEquals(countJpql, String.format(expected, "Movie"));
    
    countJpql = CrudServiceUtils.createCountJpql(NamedEntity.class);
    Assert.assertEquals(countJpql, String.format(expected, "ANamedEntity"));
  }
  
}
