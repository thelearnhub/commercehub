package com.thelearnhub.commercehub.user.service;

import com.thelearnhub.commercehub.user.domain.UserProfile;
import com.thelearnhub.commercehub.user.dto.CreateProfileRequest;
import com.thelearnhub.commercehub.user.dto.ProfileResponse;
import com.thelearnhub.commercehub.user.dto.UpdateProfileRequest;
import com.thelearnhub.commercehub.user.exception.ProfileAlreadyExistsException;
import com.thelearnhub.commercehub.user.exception.ProfileNotFoundException;
import com.thelearnhub.commercehub.user.mapper.ProfileMapper;
import com.thelearnhub.commercehub.user.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserProfileRepository userProfileRepository;

    public ProfileServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    @Transactional
    public ProfileResponse createProfile(String email, CreateProfileRequest request) {
        if (userProfileRepository.existsByEmail(email)) {
            throw new ProfileAlreadyExistsException(email);
        }

        UserProfile profile = new UserProfile(email, request.firstName(), request.lastName());
        profile.setPhone(request.phone());
        profile.setAvatarUrl(request.avatarUrl());

        userProfileRepository.save(profile);
        return ProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String email) {
        UserProfile profile = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new ProfileNotFoundException(email));
        return ProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        UserProfile profile = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new ProfileNotFoundException(email));

        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setPhone(request.phone());
        profile.setAvatarUrl(request.avatarUrl());

        userProfileRepository.save(profile);
        return ProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfileById(UUID id) {
        UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(id.toString()));
        return ProfileMapper.toResponse(profile);
    }
}
