package dev.kpreis.retrykit.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RetryProxy
 */
public class RetryProxyTest {

    private TestServiceImpl testService;
    private TestService proxy;

    @BeforeEach
    public void setUp() {
        testService = new TestServiceImpl();
        proxy = RetryProxy.create(testService);
    }

    @AfterEach
    public void tearDown() {
        testService.resetCallCount();
    }

    /**
     * Test that methods without @Retryable annotation work normally
     */
    @Test
    public void testNoRetryAnnotation() {
        // This should work without any retries
        proxy.alwaysSucceeds();
        assertEquals(1, testService.getCallCount(), "Method should be called once");
    }

    /**
     * Test that methods with @Retryable retry on failure and eventually succeed
     */
    @Test
    public void testRetryUntilSuccess() {
        long startTime = System.currentTimeMillis();

        proxy.failsTwiceThenSucceeds();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Should be called 3 times (2 failures + 1 success)
        assertEquals(3, testService.getCallCount(), "Method should be called 3 times");

        // Should have taken at least 20ms (2 delays of 10ms each)
        assertTrue(duration >= 20, "Should have delays between retries");
    }

    /**
     * Test that methods with @Retryable eventually fail after max attempts
     */
    @Test
    public void testRetryMaxAttemptsExceeded() {
        long startTime = System.currentTimeMillis();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            proxy.alwaysFails();
        });
        assertEquals("Always fails", exception.getMessage());

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Should be called 3 times (maxAttempts)
        assertEquals(3, testService.getCallCount(), "Method should be called maxAttempts times");

        // Should have taken at least 20ms (2 delays of 10ms each)
        assertTrue(duration >= 20, "Should have delays between retries");
    }

    /**
     * Test that only specified exceptions trigger retries
     */
    @Test
    public void testRetryOnlyOnSpecifiedExceptions() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            proxy.failsWithSpecificException();
        });
        assertEquals("Specific exception", exception.getMessage());

        // Should be called 3 times (maxAttempts) because IllegalArgumentException is in retryOn
        assertEquals(3, testService.getCallCount(), "Should retry on specified exception");
    }

    /**
     * Test that non-specified exceptions do not trigger retries
     */
    @Test
    public void testNoRetryOnUnspecifiedExceptions() {
        testService.resetCallCount();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            proxy.failsWithDifferentException();
        });
        assertEquals("Different exception - should not retry", exception.getMessage());

        // Should be called only once because RuntimeException is not in retryOn
        assertEquals(1, testService.getCallCount(), "Should not retry on unspecified exception");
    }

    /**
     * Test that proxy maintains object state across retries
     */
    @Test
    public void testStateMaintenance() {
        // Call failsTwiceThenSucceeds twice
        proxy.failsTwiceThenSucceeds();
        int firstCallCount = testService.getCallCount();

        // Reset for second call
        testService.resetCallCount();
        proxy.failsTwiceThenSucceeds();
        int secondCallCount = testService.getCallCount();

        // Each call should have been called 3 times
        assertEquals(3, firstCallCount, "First call should be called 3 times");
        assertEquals(3, secondCallCount, "Second call should be called 3 times");
        assertEquals(6, firstCallCount + secondCallCount, "Total calls should be 6");
    }

    /**
     * Test that proxy works with different configurations
     */
    @Test
    public void testDifferentConfigurations() {
        // Test with different delay and maxAttempts
        // We can't easily test this with our current test setup since the annotation is fixed
        // But we can verify the proxy creation works
        TestService anotherProxy = RetryProxy.create(new TestServiceImpl());
        assertNotNull(anotherProxy, "Proxy should be created");
    }
}
