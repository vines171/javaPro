import annotation.*;
import exceptions.TestAssertionError;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

public class TestSuccess {
    @BeforeSuite
    public static void beforeAllTests() {
        System.out.println("Before all tests");
    }

    @AfterSuite
    public static void afterAllTests() {
        System.out.println("After all tests");
    }

    @BeforeEach
    public void beforeEachTest() {
        System.out.println("Before each test");
    }

    @AfterEach
    public void afterEachTest() {
        System.out.println("After each test");
    }

    @Test(name = "Successful test", priority = 10)
    public void successfulTest() {
        System.out.println("Running successful test");
    }

    @Test(name = "Failed test", priority = 5)
    public void failedTest() {
        System.out.println("Running failed test");
        throw new TestAssertionError("Test condition failed");
    }

    @Test(name = "Test 1", priority = 1)
    public void test1(){
        System.out.println("Первый тест");
    }

}
