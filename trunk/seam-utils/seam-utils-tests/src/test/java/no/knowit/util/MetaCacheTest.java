package no.knowit.util;

import no.knowit.seam.framework.SeamFrameworkTest;
import no.knowit.testsupport.bean.NestingBean;
import no.knowit.testsupport.bean.SimpleBean;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class MetaCacheTest {
  private static final LogProvider log = Logging.getLogProvider(SeamFrameworkTest.class);

  @BeforeSuite
  public void beforeSuite() throws Exception {
    
    //System.out.println("******* " + this.getClass().getSimpleName() + ".beforeSuite()");
    
  }
  
  @Test
  public void should() throws Exception {
    SimpleBean simpleBean = new SimpleBean();
    MetaCache.set("id" ,   simpleBean, 2);
    MetaCache.set("foo",   simpleBean, 100);
    MetaCache.set("bar",   simpleBean, "Hello \"BAR\"!");
    MetaCache.set("baz",   simpleBean, "Hello BAZ!");
    MetaCache.set("color", simpleBean, SimpleBean.Color.RED);
//    System.out.println(MetaCache.objectToString(simpleBean));
    
    
    SimpleBean sb1 = new SimpleBean();
    MetaCache.set("id" , sb1, 10001);
    MetaCache.set("bar", sb1, "Instance One");
    
    SimpleBean sb2 = new SimpleBean();
    MetaCache.set("id" , sb2, 10002);
    MetaCache.set("bar", sb2, "Instance Two");
    
    NestingBean nestingBean = new NestingBean(99, simpleBean);
    MetaCache.set("intArray",    nestingBean, new int[]{1,2,3});
    MetaCache.set("stringArray", nestingBean, new String[]{"I am", "a", "string", "array"});
    MetaCache.set("beanArray",   nestingBean, new SimpleBean[]{sb1, sb2});
    
    
    System.out.println(MetaCache.objectToString(nestingBean));
  }
  
}
