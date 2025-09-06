package exceptions;

public class TestAssertionError extends AssertionError {
    public TestAssertionError(String message) {
        super(message);
    }

    public TestAssertionError(String message, Throwable cause) {
        super(message, cause);
    }
}
