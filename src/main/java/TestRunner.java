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
import java.util.*;

import static java.util.stream.Collectors.toList;

public class TestRunner implements InvocationHandler {
    public TestRunner(Object object) {
        this.object = object;
    }

    static Method[] methods = TestSuccess.class.getDeclaredMethods();
    static Object object;

    private static String getTestName(Method method) {
        String nameValue = method.getDeclaredAnnotation(Test.class).name();
        if (nameValue.isBlank()) return nameValue;
        return method.getName();
    }

    private static List<Method> getMethodsByType(Method[] methods) {
        return Arrays.stream(methods)
                .filter(x -> x.isAnnotationPresent(Test.class))
                .sorted(Comparator.comparingInt((Method m) -> m.isAnnotationPresent(Order.class) ?
                                m.getAnnotation(Order.class).value() : 5)
                        .thenComparing(TestRunner::getTestName))
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
            Method beforeSuiteMethod = getMethodByType(BeforeSuite.class);
            Method afterSuiteMethod = getMethodByType(AfterSuite.class);
            List<Method> testList = getMethodsByType(methods);

            beforeSuiteMethod.invoke(object);

            for (Method testMethod : testList) {
                executeTest(testMethod, results);
            }
            afterSuiteMethod.invoke(object);

        } catch (Exception e) {
            throw new BadTestClassError("Failed to execute tests: " + e.getMessage());
        }
        return results;
    }

    private static void executeTest(Method testMethod, Map<TestResult, List<TestInfo>> results) {
        TestSuccess testSuccess = new TestSuccess();
        TestSuccess testSuccessProxy = (TestSuccess) Enhancer.create(TestSuccess.class, new TestRunner(testSuccess));

        String testName = getTestName(testMethod);
        TestResult testResult = TestResult.SUCCESS;
        Throwable testException = null;

        if (testMethod.isAnnotationPresent(Disabled.class)) {
            results.get(TestResult.SKIPPED).add(new TestInfo(TestResult.SKIPPED, testName, null));
            return;
        }

        try {
            testMethod.setAccessible(true);
            testMethod.invoke(testSuccessProxy);


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
        }
        results.get(testResult).add(new TestInfo(testResult, testName, testException));
    }

    @SneakyThrows
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Method beforeEachMethod = getMethodByType(BeforeEach.class);
        Method afterEachMethod = getMethodByType(AfterEach.class);

        if (method.isAnnotationPresent(Test.class)) {
            try {
                beforeEachMethod.invoke(object);
                method.invoke(object, args);
            } finally {
                try {
                    afterEachMethod.invoke(object);
                } catch (Exception e) {
                    System.err.println("Error in AfterEach method: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static Method getMethodByType(Class<? extends Annotation> annotation) {
        for (Method method : methods) {
            if (method.isAnnotationPresent(annotation)) {
                return method;
            }
        }
        throw new IllegalStateException("Method not found");
    }
}