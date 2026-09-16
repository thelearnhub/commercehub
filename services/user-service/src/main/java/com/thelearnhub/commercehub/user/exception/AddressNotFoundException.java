package com.thelearnhub.commercehub.user.exception;

import java.util.UUID;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(UUID addressId) {
        super("Address not found: " + addressId);
    }
}
