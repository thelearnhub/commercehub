package com.thelearnhub.commercehub.user.service;

import com.thelearnhub.commercehub.user.dto.AddressResponse;
import com.thelearnhub.commercehub.user.dto.CreateAddressRequest;
import com.thelearnhub.commercehub.user.dto.UpdateAddressRequest;

import java.util.List;
import java.util.UUID;

public interface AddressService {

    List<AddressResponse> listAddresses(String email);

    AddressResponse addAddress(String email, CreateAddressRequest request);

    AddressResponse updateAddress(String email, UUID addressId, UpdateAddressRequest request);

    void deleteAddress(String email, UUID addressId);
}
