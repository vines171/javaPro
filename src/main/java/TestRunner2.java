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
    List<Method> beforeSuiteMethods = getMethodsByType(methods, BeforeSuite.class);
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
            Object testInstance = testClass.getDeclaredConstructor().newInstance();

            List<Method> beforeEachMethods = getMethodsByType(methods, BeforeEach.class);
            List<Method> afterEachMethods = getMethodsByType(methods, AfterEach.class);
            List<Method> beforeSuiteMethods = getMethodsByType(methods, BeforeSuite.class);
            List<Method> afterSuiteMethods = getMethodsByType(methods, AfterSuite.class);
            List<Method> testList = getMethodsByType(methods, Test.class);


//            TestSuccess testSuccess1 = new TestSuccess();
//            TestSuccess testSuccess = (TestSuccess) Enhancer.create(TestSuccess.class, new TestRunner2(testSuccess1));

//            testSuccess.beforeAllTests();
//            testSuccess.test1();

            // Выполнение BeforeSuite методов
//            executeMethods(beforeSuiteMethods, null);

            for (Method testMethod : testList) {
                executeSingleTest(testMethod, testInstance, beforeEachMethods, afterEachMethods, results);
            }

            // Выполнение AfterSuite методов
//            executeMethods(afterSuiteMethods, null);
//            testSuccess.afterAllTests();

        } catch (Exception e) {
            throw new BadTestClassError("Failed to execute tests: " + e.getMessage());
        }

        return results;
    }

    private static void executeSingleTest(Method testMethod, Object testInstance,
                                          List<Method> beforeEachMethods, List<Method> afterEachMethods,
                                          Map<TestResult, List<TestInfo>> results) {

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
//             Выполнение BeforeEach методов
            System.out.println("++++++++++++++++++");
            executeMethods(beforeEachMethods, testInstance);

//             Выполнение теста

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
                executeMethods(afterEachMethods, testInstance);
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
    private static void executeMethods(List<Method> methods, Object instance) {
        for (Method method : methods) {
            method.setAccessible(true);
            if (Modifier.isStatic(method.getModifiers())) {
                method.invoke(null);
            } else {
                method.invoke(instance);
            }
        }
    }

//    @SneakyThrows
//    @Override
//    public Object invoke(Object proxy, Method method, Object[] args) {
//        return method.invoke(object, args);
//    }


    @SneakyThrows
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
//        Object obj = method.invoke(object, args);


        if(method.isAnnotationPresent(Test.class)) {
            System.out.println("Привет!");

//            method.invoke(beforeSuiteMethods, args);
            method.invoke(object, args);
//            method.invoke(afterSuiteMethods, args);

        }


        return null;



//        TestSuccess.beforeAllTests();
//        if (method.isAnnotationPresent(Test.class)) {
//            testSuccess.beforeEachTest();
//            method.invoke(object, args);
//            testSuccess.afterEachTest();
//        }
//        TestSuccess.afterAllTests();
//        return method.invoke(object, args);
//                method.invoke(object, args);
    }
}
