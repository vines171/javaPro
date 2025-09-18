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

public class TestRunner2
        implements InvocationHandler {
    static Object object;

    public TestRunner2(Object object) {
        this.object = object;
    }
    static Method[] methods = TestSuccess.class.getDeclaredMethods();


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
            Method beforeSuiteMethod = getMethodByType(BeforeSuite.class);
            Method afterSuiteMethod = getMethodByType(AfterSuite.class);
            List<Method> testList = getMethodsByType(methods, Test.class);

            beforeSuiteMethod.invoke(object);
            System.out.println("++++++++++++++++++");

            for (Method testMethod : testList) {
                executeSingleTest(testMethod, results);
            }
            System.out.println("++++++++++++++++++");
            afterSuiteMethod.invoke(object);

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


//catch (InvocationTargetException e) {
//                exception = e.getTargetException();
//                if (exception instanceof TestAssertionError) {
//                    result = TestResult.FAILED;
//                } else {
//                    result = TestResult.ERROR;


        } catch (InvocationTargetException e) {
            Throwable cause = null;
//                    = e.getTargetException();
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

        // Добавляем результат теста
        results.get(testResult).add(new TestInfo(testResult, testName, testException));
    }



    @SneakyThrows
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Method beforeEachMethod = getMethodByType(BeforeEach.class);
        Method afterEachMethod = getMethodByType(AfterEach.class);

        if (method.isAnnotationPresent(Test.class)) {
            try {
            System.out.println("--------------");
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
            System.out.println("+++++++++++++++");
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
