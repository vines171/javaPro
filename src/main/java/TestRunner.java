//import annotation.*;
//import enums.TestResult;
//import exceptions.TestAssertionError;
//import lombok.SneakyThrows;
//import org.springframework.cglib.proxy.Enhancer;
//import org.springframework.cglib.proxy.InvocationHandler;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.*;
//
//import static java.util.stream.Collectors.toList;
//
//public class TestRunner implements InvocationHandler {
//    static Object object;
//    public TestRunner(Object object) {
//        this.object = object;
//    }
//    private static String getTestName(Method method) {
//        String nameValue = method.getDeclaredAnnotation(Test.class).name();
//        if (nameValue.isBlank()) return nameValue;
//        return method.getName();
//    }
//    private static List<Method> getMethodsByType(Method[] methods, Class<? extends Annotation> annotation) {
//        return Arrays.stream(methods)
//                .filter(x -> x.isAnnotationPresent(annotation))
//                .sorted(Comparator.comparingInt((Method m) -> m.isAnnotationPresent(Order.class) ?
//                                m.getAnnotation(Order.class).value() : 5)
//                        .thenComparing(TestRunner::getTestName))
//                .collect(toList());
//    }
//
//
////    System.out.println("Before all tests");
//    //Подготовка тестов для классов
//
////    List<Method> beforeEachMethods = getMethodsByType(methods, BeforeEach.class);
////    List<Method> afterEachMethods = getMethodsByType(methods, AfterEach.class);
////    List<Method> beforeSuiteMethods = getMethodsByType(methods, BeforeSuite.class);
////    List<Method> afterSuiteMethods = getMethodsByType(methods, AfterSuite.class);
////    List<Method> testMethods = getMethodsByType(methods, Test.class);
////    List<Method> disabledMethods = getMethodsByType(methods, Disabled.class);
//
//
////    public static Map<TestResult, List<TestInfo>> runTests(Class c) {
//////        validateTestClass(c);
//////        Method[] methods = c.getDeclaredMethods();
////
////        Map<TestResult, List<TestInfo>> results = new EnumMap<>(TestResult.class);
////        for (TestResult result : TestResult.values()) {
////            results.put(result, new ArrayList<>());
////        }
////        return results;
////    }
//
//    @SneakyThrows
//    public static Map<TestResult, List<TestInfo>> runTests(Class<?> testClass) {
//        Method[] methods = testClass.getDeclaredMethods();
//        Map<TestResult, List<TestInfo>> results = new EnumMap<>(TestResult.class);
//
//        List<Method> beforeEachMethods = getMethodsByType(methods, BeforeEach.class);
//        List<Method> afterEachMethods = getMethodsByType(methods, AfterEach.class);
//        List<Method> beforeSuiteMethods = getMethodsByType(methods, BeforeSuite.class);
//        List<Method> afterSuiteMethods = getMethodsByType(methods, AfterSuite.class);
//        List<Method> testList = getMethodsByType(methods, Test.class);
//
////        testList.add(TestSuccess.class.getDeclaredMethods());
//
//
//        for (Method m : methods) {
//
//                System.out.println("Тест не выполнен");
//
////
////            m.invoke(testList);
//
//            // Выполнение теста
////            m.setAccessible(true);
////            m.invoke(methods);
//
//
//
////                List<TestInfo> currentExecuteList =
////                        results.getOrDefault(TestResult.SUCCESS, new ArrayList<>());
////                currentExecuteList.add(new TestInfo(TestResult.SUCCESS));
////                results.put(TestResult.SUCCESS, currentExecuteList);
////    // Инициализируем все возможные результаты
////        for (TestResult result : TestResult.values()) {
////        results.put(result, new ArrayList<>());
////    }
////
////        try {
////
////        // Создание экземпляра тестового класса
////        Object testInstance = testClass.getDeclaredConstructor().newInstance();
//
//
//                System.out.printf("Подготовка тестов для класса %s завершена", testClass.getName());
////        System.out.printf(results.toString());
////            }
////            return results;
//
//
//        }
//        return results;
//    }
//
//
//
//
//
//
//
//
//
//
//    private static void executeSingleTest(Method testMethod, Object testInstance,
//                                          List<Method> beforeEachMethods, List<Method> afterEachMethods,
//                                          Map<TestResult, List<TestInfo>> results) {
//
//        String testName = getTestName(testMethod);
//
//        // Проверка на отключенный тест
//        if (testMethod.isAnnotationPresent(Disabled.class)) {
//            results.get(TestResult.SKIPPED).add(new TestInfo(TestResult.SKIPPED, testName, null));
//            return;
//        }
//
//        try {
//            // Выполнение BeforeEach методов
////            executeMethods(beforeEachMethods, testInstance, false);
//
//            // Выполнение теста
//            testMethod.setAccessible(true);
//            testMethod.invoke(testInstance);
//
//            // Успешное выполнение
//            results.get(TestResult.SUCCESS).add(new TestInfo(TestResult.SUCCESS, testName, null));
//
//        } catch (InvocationTargetException e) {
//            Throwable cause = e.getCause();
//            if (cause instanceof TestAssertionError) {
//                // Тест провален
//                results.get(TestResult.FAILED).add(new TestInfo(TestResult.FAILED, testName, cause));
//            } else {
//                // Ошибка выполнения
//                results.get(TestResult.ERROR).add(new TestInfo(TestResult.ERROR, testName, cause));
//            }
//        } catch (Exception e) {
//            results.get(TestResult.ERROR).add(new TestInfo(TestResult.ERROR, testName, e));
//        } finally {
//            try {
//                // Выполнение AfterEach методов
////                executeMethods(afterEachMethods, testInstance, false);
//            } catch (Exception e) {
//                // Ошибка в AfterEach не должна влиять на результат теста
////                System.err.println("Error in AfterEach method: " + e.getMessage());
//            }
//        }
//    }
//
//
//    private static void executeMethods(List<Method> methods, Object instance, boolean isStatic) throws Exception {
//        for (Method method : methods) {
//            method.setAccessible(true);
//            if (isStatic) {
//                method.invoke(null);
//            } else {
//                method.invoke(instance);
//            }
//        }
//    }
//
//
//    @Override
//    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        TestSuccess testSuccess1 = new TestSuccess();
//        TestSuccess testSuccess = (TestSuccess) Enhancer.create(TestSuccess.class, new TestRunner(testSuccess1));
//
//        TestSuccess.beforeAllTests();
//        if(method.isAnnotationPresent(Test.class)) {
//            testSuccess.beforeEachTest();
//            method.invoke(object, args);
//            testSuccess.afterEachTest();
//        }
//        TestSuccess.afterAllTests();
//        return method.invoke(object, args);
////                method.invoke(object, args);
//    }
//
//
//}
