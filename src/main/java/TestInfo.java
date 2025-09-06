import enums.TestResult;

import java.util.Objects;

public class TestInfo {
    private TestResult result;
    private String name;
    private Throwable exception;

    public TestInfo(TestResult result, String name, Throwable exception) {
        this.result = result;
        this.name = name;
        this.exception = exception;
    }

    public TestResult getResult() {
        return result;
    }

    public String getName() {
        return name;
    }

    public Throwable getException() {
        return exception;
    }

    @Override
    public String toString() {
        return "Test{" +
                "result=" + result +
                ", name='" + name + '\'' +
                ", exception=" + (exception != null ? exception.getMessage() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestInfo test = (TestInfo) o;
        return result == test.result &&
                Objects.equals(name, test.name) &&
                Objects.equals(exception, test.exception);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, name, exception);
    }
}
