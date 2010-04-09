package no.knowit.util;

import no.knowit.testsupport.bean.SimpleBean;
import no.knowit.testsupport.model.inheritance.ContractEmployee;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class MetaCacheTest {

  @BeforeSuite
  public void beforeSuite() throws Exception {
    
    //System.out.println("******* " + this.getClass().getSimpleName() + ".beforeSuite()");
    
  }
  
  @Test
  public void should() throws Exception {
    SimpleBean simpleBean = new SimpleBean();
    MetaCache.set("id" , simpleBean, 2);
    MetaCache.set("foo", simpleBean, 1000);
    MetaCache.set("bar", simpleBean, "Hello \"BAR\"!");
    MetaCache.set("baz", simpleBean, "Hello BAZ!");
    System.out.println(MetaCache.objectToString(simpleBean));
    
    ContractEmployee contractEmployee = new ContractEmployee();
    System.out.println(MetaCache.objectToString(contractEmployee));
  }
  
}
