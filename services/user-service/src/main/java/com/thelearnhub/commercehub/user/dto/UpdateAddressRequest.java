package com.thelearnhub.commercehub.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAddressRequest(
        @NotBlank @Size(max = 50) String label,
        @NotBlank @Size(max = 255) String street,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Size(max = 100) String state,
        @NotBlank @Size(max = 20) String zipCode,
        @NotBlank @Size(max = 100) String country,
        boolean isDefault
) {
}
