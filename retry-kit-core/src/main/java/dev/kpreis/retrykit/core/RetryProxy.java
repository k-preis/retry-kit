package dev.kpreis.retrykit.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RetryProxy {

    /**
     * Creates a proxy for the target object that automatically retries methods
     * annotated with @Retryable.
     *
     * @param target the object to wrap with retry logic
     * @param <T>    the type of the object
     * @return a proxy that applies retry logic automatically
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(T target) {
        Class<?> targetClass = target.getClass();
        
        return (T) Proxy.newProxyInstance(
                targetClass.getClassLoader(),
                targetClass.getInterfaces(),
                new RetryInvocationHandler(target)
        );
    }

    private static class RetryInvocationHandler implements InvocationHandler {
        private final Object target;

        RetryInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Try to get annotation from implementation method first, then from interface method
            Retryable retryable = method.getAnnotation(Retryable.class);
            if (retryable == null) {
                try {
                    Method implementationMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
                    retryable = implementationMethod.getAnnotation(Retryable.class);
                } catch (NoSuchMethodException e) {
                    // Method not found in implementation, continue without retry
                }
            }

            if (retryable == null) {
                // No retry annotation, just call the method
                return method.invoke(target, args);
            }

            int attempts = 0;
            int maxAttempts = retryable.maxAttempts();
            long delay = retryable.delay();
            Class<? extends Throwable>[] retryOnExceptions = retryable.retryOn();

            while (true) {
                try {
                    return method.invoke(target, args);
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    attempts++;

                    // Check if this exception type should be retried
                    boolean shouldRetry = false;
                    for (Class<? extends Throwable> exceptionType : retryOnExceptions) {
                        if (exceptionType.isInstance(cause)) {
                            shouldRetry = true;
                            break;
                        }
                    }

                    if (!shouldRetry || attempts >= maxAttempts) {
                        throw cause;
                    }

                    System.out.printf("[RETRY] Attempt %d/%d failed: %s. Retrying in %dms...%n",
                            attempts, maxAttempts, cause.getMessage(), delay);
                    
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw cause;
                    }
                }
            }
        }
    }
}




