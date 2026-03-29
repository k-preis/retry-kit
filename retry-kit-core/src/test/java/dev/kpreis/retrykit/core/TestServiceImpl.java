package dev.kpreis.retrykit.core;

public class TestServiceImpl implements TestService {

    private int callCount = 0;

    @Override
    public void alwaysSucceeds() {
        callCount++;
    }

    @Override
    @Retryable(maxAttempts = 3, delay = 10)
    public void alwaysFails() {
        callCount++;
        throw new RuntimeException("Always fails");
    }

    @Override
    @Retryable(maxAttempts = 3, delay = 10)
    public void failsTwiceThenSucceeds() {
        callCount++;
        if (callCount < 3) {
            throw new RuntimeException("Temporary failure");
        }
    }

    @Override
    @Retryable(maxAttempts = 3, delay = 10, retryOn = {IllegalArgumentException.class})
    public void failsWithSpecificException() {
        callCount++;
        throw new IllegalArgumentException("Specific exception");
    }

    @Override
    @Retryable(maxAttempts = 3, delay = 10, retryOn = {IllegalArgumentException.class})
    public void failsWithDifferentException() {
        callCount++;
        throw new RuntimeException("Different exception - should not retry");
    }

    public void resetCallCount() {
        callCount = 0;
    }

    public int getCallCount() {
        return callCount;
    }
}
