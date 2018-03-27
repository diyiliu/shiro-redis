import org.junit.Test;

import java.net.InetAddress;

/**
 * Description: TestMain
 * Author: DIYILIU
 * Update: 2018-03-27 14:15
 */
public class TestMain {


    @Test
    public void test() throws Exception{
        String addr = InetAddress.getLocalHost().getHostAddress();

        System.out.println(addr);
    }
}
