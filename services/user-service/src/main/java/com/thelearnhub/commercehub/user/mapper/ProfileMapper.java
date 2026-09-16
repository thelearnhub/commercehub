package com.thelearnhub.commercehub.user.mapper;

import com.thelearnhub.commercehub.user.domain.UserProfile;
import com.thelearnhub.commercehub.user.dto.ProfileResponse;

/**
 * Explicit DTO ↔ entity mapping — no MapStruct, no reflection. Keeps the
 * "DTO mapping" pattern from the master plan visible and interview-discussable.
 */
public final class ProfileMapper {

    private ProfileMapper() {
    }

    public static ProfileResponse toResponse(UserProfile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getEmail(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getPhone(),
                profile.getAvatarUrl(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
