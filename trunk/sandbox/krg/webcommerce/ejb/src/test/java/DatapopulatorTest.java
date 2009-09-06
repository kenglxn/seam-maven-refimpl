import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class DatapopulatorTest {

    public void testIteration() {
        for (int c = 0; c < 5; c++) {
            for (int i = 0; i < 60; i++) {
//                System.out.println("i="+i);
                int iter = i + 1;
                if (iter > 45) iter -= 45;
                if (iter <= 45 && iter > 30) iter -= 30;
                if (iter <= 30 && iter > 15) iter -= 15;
//                System.out.println(iter);
                Assert.assertTrue(iter < 16);
                Assert.assertTrue(iter > 0);
            }
        }
    }


}
