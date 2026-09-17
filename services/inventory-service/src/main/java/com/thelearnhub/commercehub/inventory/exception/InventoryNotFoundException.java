package com.thelearnhub.commercehub.inventory.exception;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(String sku) {
        super("Inventory record not found for SKU '" + sku + "'");
    }
}
