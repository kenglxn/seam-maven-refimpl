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
                Assert.fail();
            }
        }.run ();
    }
}