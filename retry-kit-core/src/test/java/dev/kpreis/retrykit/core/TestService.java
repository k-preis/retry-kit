package dev.kpreis.retrykit.core;

public interface TestService {
    void alwaysSucceeds();
    void alwaysFails();
    void failsTwiceThenSucceeds();
    void failsWithSpecificException();
    void failsWithDifferentException();
}
