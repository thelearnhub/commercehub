package com.thelearnhub.commercehub.user.service;

import com.thelearnhub.commercehub.user.dto.CreateProfileRequest;
import com.thelearnhub.commercehub.user.dto.ProfileResponse;
import com.thelearnhub.commercehub.user.dto.UpdateProfileRequest;

import java.util.UUID;

public interface ProfileService {

    ProfileResponse createProfile(String email, CreateProfileRequest request);

    ProfileResponse getProfile(String email);

    ProfileResponse updateProfile(String email, UpdateProfileRequest request);

    ProfileResponse getProfileById(UUID id);
}
