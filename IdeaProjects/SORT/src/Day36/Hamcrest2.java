package Day36;

import org.junit.jupiter.api.Test;//collection Matcher
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;


public class DemoTest04Hamcrest {
    @Test
    public void method1() {
        List<String> custList = new ArrayList<>();
        assertThat(custList, empty());
    }
}