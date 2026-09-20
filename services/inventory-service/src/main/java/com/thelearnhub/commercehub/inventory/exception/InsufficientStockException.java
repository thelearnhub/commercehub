package com.thelearnhub.commercehub.inventory.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String sku, int requested, int available) {
        super("Insufficient available stock for SKU '" + sku + "'. Requested: " + requested + ", Available: " + available);
    }
}
