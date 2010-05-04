package no.knowit.javabin.model;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import no.knowit.crud.CrudService;
import no.knowit.openejb.mock.OpenEjbTest;

public class MovieTest extends OpenEjbTest {

  private CrudService lookupCrudService() throws Exception {
    return doJndiLookup(CrudService.NAME);
  }

  @Override
  @BeforeSuite
  public void beforeSuite() throws Exception {
    contextProperties.put("log4j.category.no.knowit.javabin", "debug");
    super.beforeSuite();
  }

  @Override
  @AfterClass
  public void cleanupClass() throws Exception {
    CrudService crudService = lookupCrudService();
    crudService.remove(Movie.class);
    assert crudService.find(Movie.class).size() == 0 : "Expected Movie list size 0 after cleanup"; 
    super.cleanupClass();
  }

  @Test
  public void create() throws Exception {
    CrudService crudService = lookupCrudService();
    Movie movie = crudService.persist(new Movie("Alan Parker", "The Wall", 2000));
    Assert.assertNotNull(movie, "crudService.persist: movie was null");
    Assert.assertNotNull(movie.getId(), "movie.getId: movie.id was null");
  }

}
