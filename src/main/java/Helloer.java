import annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Helloer implements InvocationHandler {
    Method[] methods = getClass().getDeclaredMethods();
    Object object;
    public Helloer(Object object){
        this.object = object;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.isAnnotationPresent(Test.class)){
            executeHelperMethods(beforeEachMethods, BeforeEach.class);
            method.invoke(object, args);
            executeHelperMethods(afterEachMethods, AfterEach.class);
        }
        return method.invoke(object, args);
    };
    private void executeHelperMethods(List<Method> methods, Class<? extends Annotation> annotation) throws Exception {
        for (Method helperMethod : methods) {
            try {
                helperMethod.setAccessible(true);
                helperMethod.invoke(object);
            } catch (Exception e) {
                throw new RuntimeException("Error executing " + annotation + " method '" + helperMethod.getName() + "'", e);
            }
        }
    }

    private static String getTestName(Method method){
        String nameValue = method.getDeclaredAnnotation(Test.class).name();
        if (nameValue.isBlank()) return nameValue;
        return method.getName();
    }
    private static List<Method> getMethodsByType(Method[] methods, Class<? extends Annotation> annotation){
        return Arrays.stream(methods)
                .filter(x -> x.isAnnotationPresent(annotation))
//                .sorted(Comparator.comparingInt((Method m) -> m.isAnnotationPresent(Order.class)?
//                                m.getAnnotation(Order.class).value() : 5)
//                        .thenComparing(TestRunner::getTestName))
                .collect(toList());
    }



    List<Method> beforeEachMethods = getMethodsByType(methods, BeforeEach.class);
    List<Method> afterEachMethods = getMethodsByType(methods, AfterEach.class);
    List<Method> beforeSuiteMethods = getMethodsByType(methods, BeforeSuite.class);
    List<Method> afterSuiteMethods = getMethodsByType(methods, AfterSuite.class);
    List<Method> testMethods = getMethodsByType(methods, Test.class);

}
