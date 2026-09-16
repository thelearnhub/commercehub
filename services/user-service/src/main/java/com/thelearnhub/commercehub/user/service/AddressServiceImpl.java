package com.thelearnhub.commercehub.user.service;

import com.thelearnhub.commercehub.user.domain.Address;
import com.thelearnhub.commercehub.user.domain.UserProfile;
import com.thelearnhub.commercehub.user.dto.AddressResponse;
import com.thelearnhub.commercehub.user.dto.CreateAddressRequest;
import com.thelearnhub.commercehub.user.dto.UpdateAddressRequest;
import com.thelearnhub.commercehub.user.exception.AddressNotFoundException;
import com.thelearnhub.commercehub.user.exception.ProfileNotFoundException;
import com.thelearnhub.commercehub.user.mapper.AddressMapper;
import com.thelearnhub.commercehub.user.repository.AddressRepository;
import com.thelearnhub.commercehub.user.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserProfileRepository userProfileRepository;

    public AddressServiceImpl(AddressRepository addressRepository,
                              UserProfileRepository userProfileRepository) {
        this.addressRepository = addressRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> listAddresses(String email) {
        UserProfile profile = findProfileByEmail(email);
        return addressRepository.findByUserProfileId(profile.getId()).stream()
                .map(AddressMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AddressResponse addAddress(String email, CreateAddressRequest request) {
        UserProfile profile = findProfileByEmail(email);

        if (request.isDefault()) {
            addressRepository.clearDefaultForUser(profile.getId());
        }

        Address address = new Address(
                profile, request.label(), request.street(),
                request.city(), request.state(), request.zipCode(), request.country()
        );
        address.setDefault(request.isDefault());

        addressRepository.save(address);
        return AddressMapper.toResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(String email, UUID addressId, UpdateAddressRequest request) {
        UserProfile profile = findProfileByEmail(email);

        Address address = addressRepository.findByIdAndUserProfileId(addressId, profile.getId())
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        if (request.isDefault()) {
            addressRepository.clearDefaultForUser(profile.getId());
        }

        address.setLabel(request.label());
        address.setStreet(request.street());
        address.setCity(request.city());
        address.setState(request.state());
        address.setZipCode(request.zipCode());
        address.setCountry(request.country());
        address.setDefault(request.isDefault());

        addressRepository.save(address);
        return AddressMapper.toResponse(address);
    }

    @Override
    @Transactional
    public void deleteAddress(String email, UUID addressId) {
        UserProfile profile = findProfileByEmail(email);

        Address address = addressRepository.findByIdAndUserProfileId(addressId, profile.getId())
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        addressRepository.delete(address);
    }

    private UserProfile findProfileByEmail(String email) {
        return userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new ProfileNotFoundException(email));
    }
}
