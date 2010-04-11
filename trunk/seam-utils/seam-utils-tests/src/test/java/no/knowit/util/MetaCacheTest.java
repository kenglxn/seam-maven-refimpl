package no.knowit.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import no.knowit.testsupport.bean.SimpleBean;
import no.knowit.util.MetaCache.Meta;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class MetaCacheTest {
  //private static Logger log = Logger.getLogger(MetaCacheTest.class);

  private static final Integer EXPECTED_ID = 2;
  private static final int EXPECTED_FOO = 200; 
  private static final String EXPECTED_BAR = "setBar -> Hello \"BAR\"!"; 
  private static final String EXPECTED_BAZ = "Hello BAZ!"; 
  private static final SimpleBean.Color EXPECTED_COLOR = SimpleBean.Color.RED;
  
  private static final String ASSERT_MESSAGE_FORMAT = "Actual: [%s]. Expected: [%s].";
  private final DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private Date expectedDate;
  
  
  @BeforeSuite
  public void beforeSuite() throws Exception {
    //System.out.println("******* " + this.getClass().getSimpleName() + ".beforeSuite()");
    
    dateformat.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
    expectedDate = dateformat.parse("2010-04-11 15:11:28");
  }
  
  @Test
  public void shouldSetAttributesByReflection() throws Exception {
    SimpleBean simpleBean = createSimpleBean();
    
    assert (Integer)MetaCache.get("id", simpleBean) == EXPECTED_ID 
      : String.format(ASSERT_MESSAGE_FORMAT, MetaCache.get("id", simpleBean), EXPECTED_ID);
    
    assert (Integer)MetaCache.get("foo", simpleBean) == EXPECTED_FOO 
      : String.format(ASSERT_MESSAGE_FORMAT, MetaCache.get("foo", simpleBean), EXPECTED_FOO);
    
    assert ((String)MetaCache.get("bar", simpleBean)).equals(EXPECTED_BAR) 
      : String.format(ASSERT_MESSAGE_FORMAT, MetaCache.get("bar", simpleBean), EXPECTED_BAR);
    
    assert ((String)MetaCache.get("baz", simpleBean)).equals(EXPECTED_BAZ) 
      : String.format(ASSERT_MESSAGE_FORMAT, MetaCache.get("baz", simpleBean), EXPECTED_BAZ);
    
    assert ((SimpleBean.Color)MetaCache.get("color", simpleBean)).equals(EXPECTED_COLOR) 
      : String.format(ASSERT_MESSAGE_FORMAT, MetaCache.get("color", simpleBean), EXPECTED_COLOR);

    Date actual = (Date)MetaCache.get("someDate", simpleBean);
    assert actual.equals(expectedDate)
      : String.format(ASSERT_MESSAGE_FORMAT, actual, expectedDate);
  }
  
  @Test(dependsOnMethods={ "shouldSetAttributesByReflection" })
  public void shouldGetClassFromCache() throws Exception {
    Meta meta = MetaCache.getMeta(SimpleBean.class);
    assert meta != null;
  }
  
  private SimpleBean createSimpleBean() {
    SimpleBean simpleBean = new SimpleBean();
    MetaCache.set("id" ,   simpleBean, 2);
    MetaCache.set("foo",   simpleBean, 100);
    MetaCache.set("bar",   simpleBean, "Hello \"BAR\"!");
    MetaCache.set("baz",   simpleBean, "Hello BAZ!");
    MetaCache.set("color", simpleBean, SimpleBean.Color.RED);
    MetaCache.set("someDate", simpleBean, expectedDate);

    return simpleBean;
  }
}
