import enums.TestResult;
import tests.TestSuccess;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<TestResult, List<TestInfo>> results = TestRunner.runTests(TestSuccess.class);
        System.out.println(results);
    }
}
