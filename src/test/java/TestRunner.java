//import annotation.*;
//import enums.TestResult;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.*;
//
//public class TestRunner implements InvocationHandler {
//    Object object;
//    Method[] methods = getClass().getDeclaredMethods();
//
//
//    private static String getTestName(Method method){
//        String nameValue = method.getDeclaredAnnotation(Test.class).name();
//        if (nameValue.isBlank()) return nameValue;
//        return method.getName();
//    }
//    private static List<Method> getMethodsByType(Method[] methods, Class<? extends Annotation> annotation){
//       return Arrays.asList(methods)
//                .stream()
//                .filter(x -> x.isAnnotationPresent(annotation))
////               .sorted(Comparator.comparingInt(Method m) -> m.isAnnotationPresent(Order.class)?
////    m.getAnnotation(Order.class).value() : 5)
////        .te
////               .then
//                .toList();
//    }
//
//
//    List<Method> beforeEachMethods = getMethodsByType(methods, BeforeEach.class);
//    List<Method> afterEachMethods = getMethodsByType(methods, AfterEach.class);
//    List<Method> beforeSuiteMethods = getMethodsByType(methods, BeforeSuite.class);
//    List<Method> afterSuiteMethods = getMethodsByType(methods, AfterSuite.class);
//    List<Method> testMethods = getMethodsByType(methods, Test.class);
////    List<Method> disabledMethods = getMethodsByType(methods, Disabled.class);
//
//
//
//    public static Map<TestResult, List<TestInfo>> runTests(Class<?> c) {
////        validateTestClass(c);
//        Method[] methods = c.getDeclaredMethods();
//
//        Map<TestResult, List<TestInfo>> results = new EnumMap<>(TestResult.class);
//        for (TestResult result : TestResult.values()) {
//            results.put(result, new ArrayList<>());
//        }
//        return results;
//    }
//
////    befoeEachList.forEach(b ->
////
////    {
////        b.setAccessible(true);
////        try {
////            b.invoke(c);
////        } catch (IllegalAccessException | InvocationTargetException e) {
////            throw new RuntimeException(e.getCause());
////        }
////    });
////
////    for(Method m: testList){
////        beforeEachList.forEach(b -> {
////            b.setAccessible(true);
////            try {
////                b.invoke(c);
////            } catch (IllegalAccessException | InvocationTargetException e) {
////                throw new RuntimeException(e.getCause());
////            }
////        });
////
////        m.invoke()
////    }
//
////    efterEachList.forEach(b -> {
////        b.setAccessible(true);
////        try {
////            b.invoke(c);
////        } catch (IllegalAccessException | InvocationTargetException e) {
////            throw new RuntimeException(e.getCause());
////        }
////    }
//
//    @Override
//    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        if(method.isAnnotationPresent(Test.class)){
//            executeHelperMethods(beforeEachMethods, BeforeEach.class);
//            method.invoke(object, args);
//            executeHelperMethods(afterEachMethods, AfterEach.class);
//        }
//        Object obj = method.invoke(object, args);
//        return obj;
//    };
//
//    private void executeHelperMethods(List<Method> methods, Class<? extends Annotation> annotation) throws Exception {
//        for (Method helperMethod : methods) {
//            try {
//                helperMethod.setAccessible(true);
//                helperMethod.invoke(object);
//            } catch (Exception e) {
//                throw new RuntimeException("Error executing " + annotation + " method '" + helperMethod.getName() + "'", e);
//            }
//        }
//    }
//
//
//
////    private static Test executeTest(Object instance, Method testMethod,
////                                    List<Method> beforeEachMethods, List<Method> afterEachMethods) {
////        String testName = getTestName(testMethod);
////
////        try {
////            // Выполняем BeforeEach методы
////            executeHelperMethods(instance, beforeEachMethods, "BeforeEach");
////
////            // Выполняем тест
////            testMethod.setAccessible(true);
////            testMethod.invoke(instance);
////
////            // Выполняем AfterEach методы
////            executeHelperMethods(instance, afterEachMethods, "AfterEach");
////
////            return new Test(TestResult.SUCCESS, testName, null);
////
////        } catch (Exception e) {
////            Throwable cause = e.getCause();
////
////            // Выполняем AfterEach методы даже если тест упал
////            try {
////                executeHelperMethods(instance, afterEachMethods, "AfterEach");
////            } catch (Exception ex) {
////                System.err.println("Error executing AfterEach methods: " + ex.getMessage());
////            }
////
////            if (cause instanceof TestAssertionError) {
////                return new Test(TestResult.FAILED, testName, cause);
////            } else {
////                return new Test(TestResult.ERROR, testName, cause);
////            }
////        }
////    }
//
//}
