package dev.kpreis.retrykit.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RetryExecutor {

    public static void execute(Object target, String methodName, Object... args) throws Exception {
        Method method = target.getClass().getMethod(methodName);
        Retryable retryable = method.getAnnotation(Retryable.class);

        int attempts = 0;
        int maxAttempts = retryable != null ? retryable.maxAttempts() : 1;
        long delay = retryable != null ? retryable.delay() : 0;

        while (true) {
            try {
                method.invoke(target, args);
                break;
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                attempts++;

                if (retryable == null || attempts >= maxAttempts) {
                    throw new RuntimeException(cause);
                }
                System.out.printf("Retry attempt %d/%d after failure: %s%n",
                        attempts, maxAttempts, cause.getMessage());
                Thread.sleep(delay);
            }
        }
    }
}
