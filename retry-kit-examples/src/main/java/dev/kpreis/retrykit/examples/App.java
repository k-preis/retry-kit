package dev.kpreis.retrykit.examples;

import dev.kpreis.retrykit.core.RetryExecutor;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        System.out.println( "Hello World!" );

        RetryExecutor.execute(new PaymentService(), "processPayment");
    }
}
