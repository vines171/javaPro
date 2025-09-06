import enums.TestResult;
import lombok.SneakyThrows;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

public class ModuleApplication {
    @SneakyThrows
    public static void main(String[] args) {
//        Map<TestResult, List<TestInfo>> resultListMap = TestRunner.runTests(TestSuccess.class);
        TestSuccess testSuccess1 = new TestSuccess();
//        TestRunner testSuccess = (TestRunner) Proxy.newProxyInstance(
//                testSuccess1.getClass().getClassLoader(),
//                TestSuccess.class.getInterfaces(),
//                new TestRunner(testSuccess1));

//        TestSuccess testSuccess = (TestSuccess) Enhancer.create((Class) TestSuccess.class, new Helloer(testSuccess1));

//        testSuccess.test1();
    }

}
