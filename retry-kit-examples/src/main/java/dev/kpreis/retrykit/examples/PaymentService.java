package dev.kpreis.retrykit.examples;

import dev.kpreis.retrykit.core.Retryable;

public class PaymentService {

    private int counter = 0;

    @Retryable(maxAttempts = 3, delay = 1000)
    public void processPayment() {
        System.out.println("Processing payment...");
        counter++;

        if (counter < 3) {
            throw new RuntimeException("Temporary failure");
        }
        System.out.println("Payment succeeded!");
    }
}
