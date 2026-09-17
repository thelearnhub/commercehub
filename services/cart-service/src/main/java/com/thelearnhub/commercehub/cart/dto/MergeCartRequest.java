package com.thelearnhub.commercehub.cart.dto;

import jakarta.validation.constraints.NotBlank;

public record MergeCartRequest(
        @NotBlank String guestCartId
) {
}
