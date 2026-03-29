# retry-kit

A lightweight, annotation-driven retry framework for Java that automatically retries failed method calls with configurable delays and exception handling.

## Features

- **Simple Annotation-Based**: Just add `@Retryable` to your methods
- **Automatic Retry Logic**: If an exception is thrown, retry automatically; if it succeeds, no retry overhead
- **Configurable Behavior**: Set max attempts, delays, and which exceptions to retry on
- **Zero Configuration**: Works with sensible defaults
- **Transparent Proxy**: Uses Java's dynamic proxy mechanism for transparent method interception

## Installation

Add the dependency to your project:

```xml
<dependency>
    <groupId>dev.kpreis.retrykit.core</groupId>
    <artifactId>retry-kit-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Usage

### 1. Define Your Service Interface

```java
public interface PaymentService {
    void processPayment();
}
```

### 2. Implement with @Retryable Annotation

```java
public class PaymentServiceImpl implements PaymentService {
    
    @Retryable(maxAttempts = 3, delay = 1000)
    public void processPayment() {
        // Your business logic here
        // If this throws an exception, it will be retried
        // If it succeeds, no retries occur
    }
}
```

### 3. Wrap with RetryProxy

```java
PaymentService service = RetryProxy.create(new PaymentServiceImpl());
service.processPayment();  // Automatic retry on failure
```

## Configuration

The `@Retryable` annotation supports the following parameters:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `maxAttempts` | int | 3 | Maximum number of attempts to make |
| `delay` | long | 1000 | Delay in milliseconds between retries |
| `retryOn` | Class<? extends Throwable>[] | Exception.class | Which exceptions trigger a retry |

### Examples

**Retry only on specific exceptions:**
```java
@Retryable(maxAttempts = 5, delay = 2000, retryOn = {IOException.class, TimeoutException.class})
public void callExternalAPI() { ... }
```

**Quick retries with no delay:**
```java
@Retryable(maxAttempts = 2, delay = 0)
public void fastOperation() { ... }
```

**Single attempt with default configuration:**
```java
@Retryable
public void someMethod() { ... }
```

## How It Works

1. **Proxy Creation**: `RetryProxy.create()` wraps your object with a Java dynamic proxy
2. **Method Interception**: When you call a method on the proxy, the invocation handler intercepts it
3. **Annotation Detection**: The handler checks for `@Retryable` annotation on the implementation method
4. **Retry Logic**: 
   - If no exception is thrown: Method returns normally
   - If the right exception type is thrown: Retry after delay
   - If max attempts reached: The exception is thrown to caller
5. **Transparent**: Your code doesn't know it's using a proxy

## Best Practices

- **Use Interfaces**: RetryProxy requires the implementation class to implement an interface
- **Idempotent Operations**: Retries work best with operations that are idempotent (safe to retry)
- **Meaningful Delays**: Use realistic delays for external API calls (network timeouts, etc.)
- **Specific Exception Handling**: Specify which exceptions should trigger retries to avoid masking logic errors

## Spring Boot Integration

For Spring Boot applications, you can use the `retry-kit-spring-boot-starter` module for automatic proxy creation.

## Examples

See the `retry-kit-examples` module for complete working examples.

## License

Licensed under the Apache License, Version 2.0. See LICENSE file for details.

