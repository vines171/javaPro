import enums.TestResult;

import java.util.Objects;

public class TestInfo {
    private TestResult result;
    private String testName;
    private Throwable exception;

    public TestInfo(TestResult result, String testName, Throwable exception) {
        this.result = result;
        this.testName = this.testName;
        this.exception = exception;
    }

    public TestResult getResult() {
        return result;
    }

    public String getName() {
        return testName;
    }

    public Throwable getException() {
        return exception;
    }

    @Override
    public String toString() {
        return "Test{" +
                "result=" + result +
                ", name='" + testName + '\'' +
//                ", exception=" + (exception != null ? exception.getMessage() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestInfo test = (TestInfo) o;
        return result == test.result &&
                Objects.equals(testName, test.testName);
//                Objects.equals(exception, test.exception);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, testName, exception);
    }
}
