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

public class TestRunner2 implements InvocationHandler  {
    static Object object;

    public TestRunner2(Object object) {
        this.object = object;
    }

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

    public static Map<TestResult, List<TestInfo>> runTests(Class<?> testClass) {
        Method[] methods = testClass.getDeclaredMethods();
        Map<TestResult, List<TestInfo>> results = new EnumMap<>(TestResult.class);
        for (TestResult result : TestResult.values()) {
            results.put(result, new ArrayList<>());
        }

        try {
            // Проверка валидности класса тестов
//            validateTestClass(c);

            // Создание экземпляра тестового класса
            Object testInstance = testClass.getDeclaredConstructor().newInstance();

            // Получение методов
            List<Method> beforeEachMethods = getMethodsByType(methods, BeforeEach.class);
            List<Method> afterEachMethods = getMethodsByType(methods, AfterEach.class);
            List<Method> beforeSuiteMethods = getMethodsByType(methods, BeforeSuite.class);
            List<Method> afterSuiteMethods = getMethodsByType(methods, AfterSuite.class);
            List<Method> testList = getMethodsByType(methods, Test.class);

            // Выполнение BeforeSuite методов
//            executeMethods(beforeSuiteMethods, null, true);

            // Выполнение тестов

            TestSuccess.beforeAllTests();
            for (Method testMethod : testList) {
                executeSingleTest(testMethod, testInstance, beforeEachMethods, afterEachMethods, results);
            }
            TestSuccess.afterAllTests();

            // Выполнение AfterSuite методов
//            executeMethods(afterSuiteMethods, null, true);

        } catch (Exception e) {
            throw new BadTestClassError("Failed to execute tests: " + e.getMessage());
        }

        return results;
    }

    private static void executeSingleTest(Method testMethod, Object testInstance,
                                          List<Method> beforeEachMethods, List<Method> afterEachMethods,
                                          Map<TestResult, List<TestInfo>> results) {

        String testName = getTestName(testMethod);

        // Проверка на отключенный тест
        if (testMethod.isAnnotationPresent(Disabled.class)) {
            results.get(TestResult.SKIPPED).add(new TestInfo(TestResult.SKIPPED, testName, null));
            return;
        }

        try {
            // Выполнение BeforeEach методов
//            executeMethods(beforeEachMethods, testInstance, false);

            // Выполнение теста
//            testMethod.setAccessible(true);

            TestSuccess testSuccess1 = new TestSuccess();
            TestSuccess testSuccess = (TestSuccess) Enhancer.create(TestSuccess.class, new TestRunner2(testSuccess1));

//            TestSuccess.beforeAllTests();
            if (testMethod.isAnnotationPresent(Test.class)) {
                testSuccess.beforeEachTest();
                testMethod.invoke(testInstance);
                testSuccess.afterEachTest();
            }
//            TestSuccess.afterAllTests();


//            testMethod.invoke(testInstance);

            // Успешное выполнение
            results.get(TestResult.SUCCESS).add(new TestInfo(TestResult.SUCCESS, testName, null));

        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof TestAssertionError) {
                // Тест провален
                results.get(TestResult.FAILED).add(new TestInfo(TestResult.FAILED, testName, cause));
            } else {
                // Ошибка выполнения
                results.get(TestResult.ERROR).add(new TestInfo(TestResult.ERROR, testName, cause));
            }
        } catch (Exception e) {
            results.get(TestResult.ERROR).add(new TestInfo(TestResult.ERROR, testName, e));
        } finally {
            try {
                // Выполнение AfterEach методов
//                executeMethods(afterEachMethods, testInstance, false);
            } catch (Exception e) {
                // Ошибка в AfterEach не должна влиять на результат теста
//                System.err.println("Error in AfterEach method: " + e.getMessage());
            }
        }
    }

    @SneakyThrows
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        TestSuccess testSuccess1 = new TestSuccess();
        TestSuccess testSuccess = (TestSuccess) Enhancer.create(TestSuccess.class, new TestRunner2(testSuccess1));

//        TestSuccess.beforeAllTests();
        System.out.println("Привет!");
        if (method.isAnnotationPresent(Test.class)) {
            System.out.println("Привет2");
//            testSuccess.beforeEachTest();
            method.invoke(object, args);
            testSuccess.afterEachTest();
        }
        System.out.println("Пока!");
//        TestSuccess.afterAllTests();
        return null;
    }
}
