package dev.kpreis.retrykit.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retryable {

    int maxAttempts() default 3;
    long delay() default 1000; // in milliseconds
    Class<? extends Throwable>[] retryOn() default { Exception.class };
}
