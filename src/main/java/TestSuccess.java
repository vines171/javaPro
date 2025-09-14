import annotation.*;
import exceptions.TestAssertionError;

public class TestSuccess {
    @BeforeSuite
    public static void
    beforeAllTests() {
//        System.out.println("Before all tests");
        System.out.println("Выполнение перед всеми тестами");
    }

    @AfterSuite
    public static void afterAllTests() {
//        System.out.println("After all tests");
        System.out.println("Выполнение после всех тестов");
    }

    @BeforeEach
    public void beforeEachTest() {
        System.out.println("Before each test");
    }

    @AfterEach
    public void afterEachTest() {
        System.out.println("After each test");
    }

//    @Test(name = "Successful test", priority = 10)
//    public void successfulTest() {
//        System.out.println("Running successful test");
//    }
//
//    @Test(name = "Failed test", priority = 5)
//    public void failedTest() {
//        System.out.println("Running failed test");
//        throw new TestAssertionError("Test condition failed");
//    }

    @Test(name = "Test 1", priority = 1)
    public void test1(){
        System.out.println("Первый тест");
    }

//    @Test(name = "Test TestAssertionError", priority = 2)
//    @Order(value = 1)
//    public void testAssertionError() {
//        System.out.println("run testAssertionError");
//        throw new TestAssertionError("Ошибка");
//    }
}
