package tests;

import annotation.*;
import exceptions.TestAssertionError;

public class TestSuccess {
    @BeforeSuite
    static void
    beforeAllTests() {
        System.out.println("Before all tests");
    }

    @AfterSuite
    static void afterAllTests() {
        System.out.println("After all tests");
    }

    @BeforeEach
    void beforeEachTest() {
        System.out.println("Before each test");
    }

    @AfterEach
    void afterEachTest() {
        System.out.println("After each test");
    }

    @Test(name = "Successful test", priority = 10)
    void successfulTest() {
        System.out.println("Running successful test");
    }

    @Test(name = "Failed test", priority = 5)
    void failedTest() {
        System.out.println("Running failed test");
        throw new TestAssertionError("Test condition failed");
    }

    @Test(name = "Test 1", priority = 1)
    void test1() {
        System.out.println("Первый тест");
    }

    @Test(name = "Test TestAssertionError", priority = 2)
    @Order(value = 1)
    void testAssertionError() {
        System.out.println("run testAssertionError");
        throw new TestAssertionError("Ошибка");
    }

    @Test(name = "High priority test", priority = 10)
    void testHighPriority() {
        System.out.println("Running high priority test");
        if (1 != 1) {
            throw new TestAssertionError("1 should equal 1");
        }
    }

    @Test(name = "Test normal priority")
    @Order(1)
    void testNormalPriority() {
        System.out.println("Running normal priority test");
    }

    @Disabled
    @Test(name = "Disable test")
    void disabledTest() {
        System.out.println("This test should be skipped");
    }

    @Test(name = "Unexpected error")
    void testWithError() {
        throw new RuntimeException("Unexpected error");
    }
}