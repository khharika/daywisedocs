package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

public class TESTCASE3 {

    @Test
    @Tag("firstPriority")
    void testMethod01() {
        System.out.println("✅ testMethod01 - Tag: firstPriority");
    }

    @Test
    @Tag("firstPriority")
    void runTestcase02() {
        System.out.println("✅ runTestcase02 - Tag: firstPriority");
    }

    @Test
    @Tag("fastTag")
    void testMethod03() {
        System.out.println("✅ testMethod03 - Tag: fastTag");
    }

    @Test
    @Tag("slowTag")
    void runTestcase04() {
        System.out.println("❌ runTestcase04 - Tag: slowTag (should be excluded)");
    }
}
