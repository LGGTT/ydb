package MyTest;

import org.junit.Test;

public class RandomNumTest {

    @Test
    public void testRandom() {
        double random = (Math.random()*1000000);
        int num =(int) random;
        System.out.println(random);
    }
}
