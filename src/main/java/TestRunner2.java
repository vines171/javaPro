import annotation.*;
import enums.TestResult;
import exceptions.BadTestClassError;
import exceptions.TestAssertionError;
import lombok.SneakyThrows;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class TestRunner2
        implements InvocationHandler {
    static Object object;

    public TestRunner2(Object object) {
        this.object = object;
    }

    Method[] methods = TestSuccess.class.getDeclaredMethods();
    List<Method> beforeSuiteMethods ;
    List<Method> afterSuiteMethods = getMethodsByType(methods, AfterSuite.class);


    private static String getTestName(Method method) {
        String nameValue = method.getDeclaredAnnotation(Test.class).name();
        if (nameValue.isBlank()) return nameValue;
        return method.getName();
    }

    private static List<Method> getMethodsByType(Method[] methods, Class<? extends Annotation> annotation) {
        return Arrays.stream(methods)
                .filter(x -> x.isAnnotationPresent(annotation))
                .sorted(Comparator.comparingInt((Method m) -> m.isAnnotationPresent(Order.class) ?
                                m.getAnnotation(Order.class).value() : 5)
                        .thenComparing(TestRunner2::getTestName))
                .collect(toList());
    }

    @SneakyThrows
    public static Map<TestResult, List<TestInfo>> runTests(Class<?> testClass) {
        Method[] methods = testClass.getDeclaredMethods();
        Map<TestResult, List<TestInfo>> results = new EnumMap<>(TestResult.class);
        for (TestResult result : TestResult.values()) {
            results.put(result, new ArrayList<>());
        }

        try {
//            Object testInstance = testClass.getDeclaredConstructor().newInstance();

            List<Method> beforeEachMethods = getMethodsByType(methods, BeforeEach.class);
            List<Method> afterEachMethods = getMethodsByType(methods, AfterEach.class);
            List<Method> beforeSuiteMethods = getMethodsByType(methods, BeforeSuite.class);
            List<Method> afterSuiteMethods = getMethodsByType(methods, AfterSuite.class);
            List<Method> testList = getMethodsByType(methods, Test.class);

            beforeSuiteMethods.get(0).invoke(object);
            System.out.println("++++++++++++++++++");

            for (Method testMethod : testList) {
                executeSingleTest(testMethod, results);
            }
            System.out.println("++++++++++++++++++");
            afterSuiteMethods.get(0).invoke(object);

        } catch (Exception e) {
            throw new BadTestClassError("Failed to execute tests: " + e.getMessage());
        }

        return results;
    }

    private static void executeSingleTest(Method testMethod, Map<TestResult, List<TestInfo>> results) {

        TestSuccess testSuccess1 = new TestSuccess();
        TestSuccess testSuccess = (TestSuccess) Enhancer.create(TestSuccess.class, new TestRunner2(testSuccess1));

        String testName = getTestName(testMethod);
        TestResult testResult = TestResult.SUCCESS;
        Throwable testException = null;

        if (testMethod.isAnnotationPresent(Disabled.class)) {
            results.get(TestResult.SKIPPED).add(new TestInfo(TestResult.SKIPPED, testName, null));
            return;
        }

        try {
            testMethod.setAccessible(true);
            testMethod.invoke(testSuccess);

        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            testException = cause;
            if (cause instanceof TestAssertionError) {
                testResult = TestResult.FAILED;
            } else {
                testResult = TestResult.ERROR;
            }
        } catch (Exception e) {
            testException = e;
            testResult = TestResult.ERROR;
        } finally {
            // Выполняем AfterEach методы (даже если тест упал)
            try {
//                executeMethods(afterEachMethods, testInstance);
            } catch (Exception e) {
                // Если тест уже упал, сохраняем оригинальное исключение
                if (testException == null) {
                    testException = e;
                    testResult = TestResult.ERROR;
                }
            }

            // Добавляем результат теста
            results.get(testResult).add(new TestInfo(testResult, testName, testException));
        }
    }

    @SneakyThrows
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        List<Method> beforeEachMethods = getMethodsByType(methods, BeforeEach.class);
        List<Method> afterEachMethods = getMethodsByType(methods, AfterEach.class);

        if(method.isAnnotationPresent(Test.class)) {
            System.out.println("------------");
            beforeEachMethods.get(0).invoke(object);
            method.invoke(object, args);
            afterEachMethods.get(0).invoke(object);
            System.out.println("++++++++++++");
        }
        return null;
    }
}
