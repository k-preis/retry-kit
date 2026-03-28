package dev.kpreis.retrykit.examples;

import dev.kpreis.retrykit.core.RetryProxy;

/**
 * Example demonstrating automatic retry with @Retryable annotation
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        System.out.println( "Starting payment service with automatic retry..." );

        // Create a proxy that automatically handles retries for @Retryable methods
        IPaymentService paymentService = RetryProxy.create(new PaymentService());
        
        // Call the method - retries are handled automatically by the proxy
        paymentService.processPayment();
        
        System.out.println( "Payment processing completed!" );
    }
}
