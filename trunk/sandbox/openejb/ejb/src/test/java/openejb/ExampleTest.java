package openejb;

import org.testng.annotations.Test;
import org.testng.Assert;


@Test(groups = "whitebox")
public class ExampleTest
        extends ContainerWhiteBoxTest {

    // save --------------------------------------------------------------------------------------
    public void should () throws Exception {

        new DBComponentTest() {

            @Override
            protected void testDBComponents () throws Exception {
                Assert.assertTrue(false, "SEAM TEST RUN, WILL FAIL DUE TO ASSERT TRUE ON FALSE");
            }
        }.run ();
    }
}