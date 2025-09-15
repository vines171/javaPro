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

    private static void validateAnnotations(Method[] methods) {
        for (Method method : methods) {
            // Проверяем, что @Test, @BeforeEach, @AfterEach не на статических методах
            if ((method.isAnnotationPresent(Test.class) ||
                    method.isAnnotationPresent(BeforeEach.class) ||
                    method.isAnnotationPresent(AfterEach.class)) &&
                    Modifier.isStatic(method.getModifiers())) {
                throw new BadTestClassError("Method " + method.getName() + " cannot be static");
            }
        }
    }

    private static void invokeStaticMethods(List<Method> methods) {
        for (Method method : methods) {
            try {
                method.setAccessible(true);
                method.invoke(null);
            } catch (Exception e) {
                throw new BadTestClassError("Failed to invoke static method " + method.getName() + ": " + e.getMessage());
            }
        }
    }

    public static Map<TestResult, List<TestInfo>> runTests(Class<?> testClass) {
        Method[] methods = testClass.getDeclaredMethods();
        Map<TestResult, List<TestInfo>> results = new EnumMap<>(TestResult.class);
        for (TestResult result : TestResult.values()) {
            results.put(result, new ArrayList<>());
        }

        try {
            // Проверка валидности класса тестов
            validateAnnotations(methods);

            // Получение методов
            List<Method> beforeEachMethods = getMethodsByType(methods, BeforeEach.class);
            List<Method> afterEachMethods = getMethodsByType(methods, AfterEach.class);
            List<Method> beforeSuiteMethods = getMethodsByType(methods, BeforeSuite.class);
            List<Method> afterSuiteMethods = getMethodsByType(methods, AfterSuite.class);
            List<Method> testList = getMethodsByType(methods, Test.class);

            invokeStaticMethods(beforeSuiteMethods);


            // Создание экземпляра тестового класса
            Object testInstance = testClass.getDeclaredConstructor().newInstance();

//            // Получение методов
//            List<Method> beforeEachMethods = getMethodsByType(methods, BeforeEach.class);
//            List<Method> afterEachMethods = getMethodsByType(methods, AfterEach.class);
//            List<Method> beforeSuiteMethods = getMethodsByType(methods, BeforeSuite.class);
//            List<Method> afterSuiteMethods = getMethodsByType(methods, AfterSuite.class);
//            List<Method> testList = getMethodsByType(methods, Test.class);

            // Выполнение BeforeSuite методов
            executeMethods(beforeSuiteMethods, null, true);

            // Выполнение тестов

            for (Method testMethod : testList) {
                executeSingleTest(testMethod, testMethod, beforeEachMethods, afterEachMethods, results);
            }

            // Выполняем AfterSuite методы
            invokeStaticMethods(afterSuiteMethods);

        } catch (Exception e) {
            throw new BadTestClassError("Test execution failed: " + e.getMessage());
        }

        return results;
    }
    private static void invokeInstanceMethods(Object instance, List<Method> methods) {
        for (Method method : methods) {
            try {
                method.setAccessible(true);
                method.invoke(instance);
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke method " + method.getName(), e);
            }
        }
    }

    private static void executeSingleTest(Object testInstance, Method testMethod,
                                          List<Method> beforeEachMethods, List<Method> afterEachMethods,
                                          Map<TestResult, List<TestInfo>> results) {
        String testName = getTestName(testMethod);
        Throwable testException = null;
        TestResult testResult = TestResult.SUCCESS;

        try {
            // Выполняем BeforeEach методы
            invokeInstanceMethods(testInstance, beforeEachMethods);

            // Выполняем тест
            testMethod.setAccessible(true);
            testMethod.invoke(testInstance);

            results.get(TestResult.SUCCESS).add(new TestInfo(TestResult.SUCCESS, testName, null));

        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof TestAssertionError) {
                results.get(TestResult.FAILED).add(new TestInfo(TestResult.FAILED, testName, cause));
            } else {
                results.get(TestResult.ERROR).add(new TestInfo(TestResult.ERROR, testName, cause));
            }
        } catch (Exception e) {
            results.get(TestResult.ERROR).add(new TestInfo(TestResult.ERROR, testName, e));
        } finally {
            // Выполняем AfterEach методы
            try {
//                invokeInstanceMethods(testInstance, afterEachMethods);
            } catch (Exception e) {
                // Игнорируем ошибки в AfterEach, но логируем
                System.err.println("Error in AfterEach method: " + e.getMessage());
            }
        }
    }

        private static void executeMethods(List<Method> methods, Object object, boolean isStatic) throws Exception {
        for (Method method : methods) {
            method.setAccessible(true);
            if (isStatic) {
                method.invoke(null);
            } else {
                method.invoke(object);
            }
        }
    }

    @SneakyThrows
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {

//        method.invoke(object, args);
//        return null;

            // Делегируем вызов целевому объекту
            return method.invoke(object, args);
        }
    }
