package com.thelearnhub.commercehub.user.mapper;

import com.thelearnhub.commercehub.user.domain.UserProfile;
import com.thelearnhub.commercehub.user.dto.ProfileResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileMapperTest {

    @Test
    void mapsAllFieldsFromEntityToResponse() {
        UserProfile profile = new UserProfile("jane@example.com", "Jane", "Doe");
        profile.setPhone("+1-555-0100");
        profile.setAvatarUrl("https://example.com/avatar.jpg");

        ProfileResponse response = ProfileMapper.toResponse(profile);

        assertThat(response.email()).isEqualTo("jane@example.com");
        assertThat(response.firstName()).isEqualTo("Jane");
        assertThat(response.lastName()).isEqualTo("Doe");
        assertThat(response.phone()).isEqualTo("+1-555-0100");
        assertThat(response.avatarUrl()).isEqualTo("https://example.com/avatar.jpg");
        assertThat(response.createdAt()).isNotNull();
        assertThat(response.updatedAt()).isNotNull();
    }

    @Test
    void handlesNullOptionalFields() {
        UserProfile profile = new UserProfile("jane@example.com", "Jane", "Doe");

        ProfileResponse response = ProfileMapper.toResponse(profile);

        assertThat(response.phone()).isNull();
        assertThat(response.avatarUrl()).isNull();
    }
}
