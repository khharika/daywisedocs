import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
public class TESTCASE1 {
    //setUp and tearDown demo
    public class DemoClass {
        String str;
        @Before
        public void setUp() { // initialization
            System.out.println("setUp method called");
            str = "initialise the setup";
        }
        @After
        public void tearDown() { // deinitialization
            System.out.println("tearDown method called");
            str = null;
        }
        public String getStr() {
            return str;
        }
    }
    @Test
    public void testStrinitialsetup() {
        DemoClass obj = new DemoClass();
        String res =  obj.getStr();
        assertEquals("initialise the setup", res);
    }
    @Test
    public void testStrCleanUp() {
        DemoClass obj = new DemoClass();
        String res = obj.getStr();
        assertNull(obj.getStr());
    }
    public static void main(String[] args) {
        Result res = JUnitCore.runClasses(TESTCASE1.class);
        if(res.getFailureCount()> 0){
            System.out.println("no of FAILURE cases are  "+ res );
            for(Failure failure : res.getFailures()) {
                System.out.println(failure.toString()); // list of 10 failure cases..
            }
        }
        else {
            System.out.println("all PASS"   );
        }
    }
}