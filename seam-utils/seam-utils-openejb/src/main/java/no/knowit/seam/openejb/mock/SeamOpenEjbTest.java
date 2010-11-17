package no.knowit.seam.openejb.mock;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * Provides Base Seam test functionality for TestNG integration tests running in the OpenEJB 
 * embedded container.
 * <b>Note:</b>This is a modification of the {@link org.jboss.seam.mock.SeamTest} class.
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @author Mike Youngstrom
 * @author LeifOO
 */
public class SeamOpenEjbTest extends AbstractSeamOpenEjbTest {
	
	@BeforeMethod
	@Override
	public void begin() {
		super.begin();
	}

	@AfterMethod
	@Override
	public void end() {
		super.end();
	}

	@Override
	@BeforeClass
	public void setupClass() throws Exception {
		super.setupClass();
	}

	@Override
	@AfterClass
	public void cleanupClass() throws Exception {
		super.cleanupClass();
	}

	@BeforeSuite
	public void beforeSuite() throws Exception {
		// needs a WEB-INF/web.xml in resources!!!
		startSeam(); 
	}

	@AfterSuite
	protected void afterSuite() throws Exception {
		stopSeam();
		// Do we need this??
		//container.close();
	}
}
