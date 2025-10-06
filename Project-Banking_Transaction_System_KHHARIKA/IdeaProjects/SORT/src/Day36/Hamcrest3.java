package Day36;

import org.junit.jupiter.api.Test;//collection Matcher


import java.util.Arrays;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;


public class DemoTest05Hamcrest {
    @Test
    public void method2() {
        List<String> custList = Arrays.asList("john", "Mary", "Sheik", "Singh");
        assertThat(custList, hasSize(4));
    }
}