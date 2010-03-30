package no.knowit.crud;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import no.knowit.openejb.mock.OpenEjbTest;
import no.knowit.testsupport.model.Movie;
import no.knowit.testsupport.model.NotAnEntity;

public class CrudUtilsTest extends OpenEjbTest {
  private static Logger log = Logger.getLogger(CrudServiceUtils.class);

  @Override
  @BeforeSuite
  public void beforeSuite() throws Exception {
    contextProperties.put("log4j.category.no.knowit.crud", "debug");
    super.beforeSuite();
  }
  
  @Test
  public void shouldBeAnEntity() throws Exception {
    Movie movie = new Movie();
    Assert.assertTrue(CrudServiceUtils.isEntity(movie), "Expected class with @Entity annotation");
  }
  
  @Test
  public void shouldNotBeAnEntity() throws Exception {
    NotAnEntity nae = new NotAnEntity();
    Assert.assertFalse(CrudServiceUtils.isEntity(nae), "Expected class without @Entity annotation");
  }
}
