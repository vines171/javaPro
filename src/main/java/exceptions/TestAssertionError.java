package exceptions;

public class TestAssertionError extends AssertionError {
    public TestAssertionError(String message) {
        super(message);
    }
}
