package com.thelearnhub.commercehub.user.mapper;

import com.thelearnhub.commercehub.user.domain.Address;
import com.thelearnhub.commercehub.user.dto.AddressResponse;

/**
 * Explicit DTO ↔ entity mapping for addresses.
 */
public final class AddressMapper {

    private AddressMapper() {
    }

    public static AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getLabel(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry(),
                address.isDefault(),
                address.getCreatedAt(),
                address.getUpdatedAt()
        );
    }
}
