package com.thelearnhub.commercehub.payment.exception;

public class InvalidPaymentProviderException extends RuntimeException {
    public InvalidPaymentProviderException(String message) {
        super(message);
    }
}
