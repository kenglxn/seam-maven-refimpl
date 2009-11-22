package no.knowit.seam.mock;

import org.jboss.seam.web.Session;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * Provides BaseSeamTest functionality for TestNG integration tests.
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @author Mike Youngstrom
 */
public class SeamOpenEjbTest extends AbstractSeamOpenEjbTest {

  @BeforeMethod
  @Override
  public void begin()
  {
     super.begin();
  }

  @AfterMethod
  @Override
  public void end()
  {
  	try {
    	Session.instance().invalidate();
      super.end();
  	}
  	catch (java.lang.IllegalStateException e) {
  		// TODO: LOO-20091122: Find out how to end HttpSession. 
  		// Following exception occurs, even if I call Session.instance().invalidate():
  		//   FAILED CONFIGURATION: @AfterMethod end
  		//   java.lang.IllegalStateException: Please end the HttpSession via org.jboss.seam.web.Session.instance().invalidate()
  		;
  	}
  }
  
  @Override
  @BeforeClass
  public void setupClass() throws Exception
  {
     super.setupClass();
  }
  
  @Override
  @AfterClass
  public void cleanupClass() throws Exception
  {
     super.cleanupClass();
  }
  
  @Override
  @BeforeSuite
  public void startSeam() throws Exception
  {
     super.startSeam(); // needs a WEB-INF/web.xml in resources!!!
  }
  
  @Override
  @AfterSuite
  protected void stopSeam() throws Exception
  {
     super.stopSeam();
  }

}
