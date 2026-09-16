package com.thelearnhub.commercehub.user.controller;

import com.thelearnhub.commercehub.user.dto.AddressResponse;
import com.thelearnhub.commercehub.user.dto.CreateAddressRequest;
import com.thelearnhub.commercehub.user.dto.UpdateAddressRequest;
import com.thelearnhub.commercehub.user.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/me/addresses")
@Tag(name = "Addresses", description = "Manage the authenticated user's shipping and billing addresses.")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    @Operation(summary = "List all addresses for the authenticated user")
    public ResponseEntity<List<AddressResponse>> listAddresses(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(addressService.listAddresses(email));
    }

    @PostMapping
    @Operation(summary = "Add a new address")
    public ResponseEntity<AddressResponse> addAddress(
            Authentication authentication,
            @Valid @RequestBody CreateAddressRequest request
    ) {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.addAddress(email, request));
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Update an existing address (ownership enforced)")
    public ResponseEntity<AddressResponse> updateAddress(
            Authentication authentication,
            @PathVariable UUID addressId,
            @Valid @RequestBody UpdateAddressRequest request
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(addressService.updateAddress(email, addressId, request));
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "Delete an address (ownership enforced)")
    public ResponseEntity<Void> deleteAddress(
            Authentication authentication,
            @PathVariable UUID addressId
    ) {
        String email = authentication.getName();
        addressService.deleteAddress(email, addressId);
        return ResponseEntity.noContent().build();
    }
}
