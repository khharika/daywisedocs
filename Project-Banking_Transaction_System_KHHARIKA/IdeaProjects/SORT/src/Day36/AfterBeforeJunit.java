package Day36;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class DemoTest008AfterBeforeJunit5 {


    private int val1;
    private int val2;


    @Test
    void test2() {
        System.out.println("sample test method");


    }

    @BeforeAll
    static void setupBeforeAll() {
        System.out.println("run it b4 all tests");
    }


    @AfterAll
    static void cleanupAfterAll() {
        System.out.println("run it after all tests");
    }


    @BeforeEach
    void setupBeforeEach() {
        System.out.println("run it b4 each test");
        val1 = 10;
        val2 = 5;
    }


    @AfterEach
    void cleanupAfterEach() {
        System.out.println("run it after each test");
    }


    @Test
    void test() {
        System.out.println("product of two nos");
        int result = val1 + val2;
        assertEquals(5, result, "actual product should be 50");
    }