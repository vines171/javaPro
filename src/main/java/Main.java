import enums.TestResult;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args)  {
        Map<TestResult, List<TestInfo>> results = TestRunner2.runTests(TestSuccess.class);

        System.out.println(results);
    }
}
