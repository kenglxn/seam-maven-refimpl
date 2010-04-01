package no.knowit.crud;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import no.knowit.openejb.mock.OpenEjbTest;
import no.knowit.testsupport.model.ConcreteEntityPropertyAnnotated;
import no.knowit.testsupport.model.NamedEntity;
import no.knowit.testsupport.model.NotAnEntity;
import no.knowit.testsupport.model.SimpleEntityFieldAnnotated;

public class CrudUtilsTest  extends OpenEjbTest {
  private static Logger log = Logger.getLogger(CrudServiceUtils.class);

  @BeforeSuite
  public void beforeSuite() throws Exception {
    contextProperties.put("log4j.category.no.knowit.crud", "debug");
    contextProperties.put("log4j.category.no.knowit.testsupport", "debug");
    super.beforeSuite();
  }
  
  @Test
  public void shouldBeAnEntity() throws Exception {
    Assert.assertTrue(CrudServiceUtils.isEntity(SimpleEntityFieldAnnotated.class), "Expected class with @Entity annotation");
  }
  
  @Test
  public void shouldNotBeAnEntity() throws Exception {
    Assert.assertFalse(CrudServiceUtils.isEntity(NotAnEntity.class), "Expected class without @Entity annotation");
  }
  
  @Test
  public void shouldGetEntityName() throws Exception {
    final String expectedNameForNamedEntity = "aNamedEntity";
    Assert.assertEquals(CrudServiceUtils.getEntityName(NamedEntity.class), expectedNameForNamedEntity);
    
    final SimpleEntityFieldAnnotated unnamedEntity = new SimpleEntityFieldAnnotated();
    final String expectedNameForUnnamedEntity = unnamedEntity.getClass().getSimpleName();
    Assert.assertEquals(CrudServiceUtils.getEntityName(unnamedEntity.getClass()), expectedNameForUnnamedEntity);
  }
  
  @Test
  public void shouldGetIdentityPropertyName() throws Exception {
    final String expectedIdPropertyName_1 = "id";
    Assert.assertEquals(CrudServiceUtils.getIdentityPropertyName(SimpleEntityFieldAnnotated.class), expectedIdPropertyName_1);
    
    final String expectedIdPropertyName_2 = "identity";
    final ConcreteEntityPropertyAnnotated ce = new ConcreteEntityPropertyAnnotated();
    Assert.assertEquals(CrudServiceUtils.getIdentityPropertyName(ce.getClass()), expectedIdPropertyName_2);
  }
  
  @Test
  public void shouldGetIdentityValue() throws Exception {
//    SimpleEntity se = new SimpleEntity(100);
//    Assert.assertNull(CrudServiceUtils.getIdentityValue(se), "Identity should be null before persist");
//    
//    CrudService crudService = lookup(CrudService.NAME);
//    se = crudService.persist(se);
//    
//    log.debug(CrudServiceUtils.getIdentityValue(se));
//    
//    Assert.assertNotNull(CrudServiceUtils.getIdentityValue(se), "Identity should not be null after persist");
    
//    Movie movie = crudService.persist(new Movie("Alan Parker", "The Wall", 2000));
//    Assert.assertNotNull(movie, "crudService.persist: movie was null");
//    Assert.assertNotNull(CrudServiceUtils.getIdentityValue(movie), "Identity should not be null after persist");
    
    
    CrudService crudService = lookup(CrudService.NAME);
    SimpleEntityFieldAnnotated se = new SimpleEntityFieldAnnotated(100);
    se = crudService.persist(se);
    Assert.assertNotNull(se, "crudService.persist: movie was null");
//    Assert.assertNotNull(CrudServiceUtils.getIdentityValue(se), "Identity should not be null after persist");
  }
}
