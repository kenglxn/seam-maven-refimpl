package no.knowit.crud;

import java.lang.reflect.Member;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import no.knowit.openejb.mock.OpenEjbTest;
import no.knowit.testsupport.bean.SimpleBean;
import no.knowit.testsupport.model.ConcreteEntityPropertyAnnotated;
import no.knowit.testsupport.model.NamedEntity;
import no.knowit.testsupport.model.SimpleEntityFieldAnnotated;
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
    Assert.assertTrue(CrudServiceUtils.isEntity(SimpleEntityFieldAnnotated.class), 
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
    
    final SimpleEntityFieldAnnotated unnamedEntity = new SimpleEntityFieldAnnotated();
    final String expectedNameForUnnamedEntity = unnamedEntity.getClass().getSimpleName();
    Assert.assertEquals(
        CrudServiceUtils.getEntityName(unnamedEntity.getClass()), expectedNameForUnnamedEntity);
  }
  
  @Test
  public void shouldHaveIdAnnotation() throws Exception {
    
    // @Id annotated on field id
    final String expectedIdPropertyName_1 = "id";
    List<Member> id = CrudServiceUtils.getIdAnnotations(SimpleEntityFieldAnnotated.class);
    Assert.assertTrue(id.size() > 0, "No @Id annotation");
    Assert.assertEquals(ReflectionUtils.getAttributeName(id.get(0)), expectedIdPropertyName_1);
    
    // @Id annotated on method getIdentity 
    final String expectedIdPropertyName_2 = "identity";
    id = CrudServiceUtils.getIdAnnotations(ConcreteEntityPropertyAnnotated.class);
    Assert.assertTrue(id.size() > 0, "No @Id annotation");
    Assert.assertEquals(ReflectionUtils.getAttributeName(id.get(0)), expectedIdPropertyName_2);
  }
  
  @Test
  public void shouldHaveIdentityValueAfterPersist() throws Exception {
    CrudService crudService = lookup(CrudService.NAME);
    
    SimpleEntityFieldAnnotated se = new SimpleEntityFieldAnnotated(100);
    se = crudService.persist(se);
    Assert.assertNotNull(se, "Entity was null after persist");

    List<Object> id = CrudServiceUtils.getIdValues(se);
    Assert.assertNotNull(id.get(0), "Identity value should not be null after persist");
  }
}
