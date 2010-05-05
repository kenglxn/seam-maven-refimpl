package no.knowit.javabin.model;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import no.knowit.crud.CrudService;
import no.knowit.openejb.mock.OpenEjbTest;

public class FakturaTest extends OpenEjbTest {
  
  private Logger log = Logger.getLogger(this.getClass());
  
  private Integer fakturaId;

  private CrudService lookupCrudService() throws Exception {
    return doJndiLookup(CrudService.NAME);
  }

  @Override
  @BeforeSuite
  public void beforeSuite() throws Exception {
    contextProperties.put("log4j.category.no.knowit.crud", "debug");
    contextProperties.put("log4j.category.no.knowit.javabin", "debug");
    super.beforeSuite();
  }
  
  @Override
  @BeforeClass
  public void setupClass() throws Exception {
    super.setupClass();
    
    CrudService crudService = lookupCrudService();
    Faktura faktura = new Faktura("Første faktura");
    faktura.leggTilFakturalinje(new Fakturalinje(faktura, "Linje 1"));
    faktura.leggTilFakturalinje(new Fakturalinje(faktura, "Linje 2"));
    faktura = crudService.persist(faktura);
    fakturaId = faktura.getId();
    
    log.debug("****** Persisterte faktura med Id: " + fakturaId);
  }
  
  
  @Test
  public void fakturaSkalHaToFakturalinjer() throws Exception {
    CrudService crudService = lookupCrudService();
    
    Faktura faktura = crudService.find(Faktura.class, fakturaId);
    Assert.assertNotNull(faktura, "crudService.find: Fant ikke faktura med id: " + fakturaId);
    
    Collection<Fakturalinje> fakturalinjer = crudService.find(Fakturalinje.class, 
//    "select l " +
//    "from   Faktura f, Fakturalinje l " +
//    "where  f = l.faktura " +
//    "and    f.id=" + fakturaId);
    
    "select l " +
    "from   Faktura f JOIN f.fakturalinjer l " +
    "where  f.id=" + fakturaId);
    
//    "select l " +
//    "from   Faktura f, IN(f.fakturalinjer) l " +
//    "where  f.id=" + fakturaId);
    
    Assert.assertEquals(fakturalinjer.size(), 2, "fakturalinjer().size()");
  }
  
  @Test(dependsOnMethods={ "fakturaSkalHaToFakturalinjer" })
  public void fakturalinjerSkalIkkeEksistereEtterSlettingAvFaktura() throws Exception {
    CrudService crudService = lookupCrudService();
    Faktura faktura = crudService.find(Faktura.class, fakturaId);

    crudService.remove(faktura);
    Collection<Fakturalinje> fakturalinjer = crudService.find(Fakturalinje.class, 
      "select l " +
      "from   Faktura f JOIN f.fakturalinjer l " +
      "where  f.id=" + fakturaId);
    Assert.assertEquals(fakturalinjer.size(), 0, "fakturalinjer().size()");
  }
  

}
