package com.thelearnhub.commercehub.user.repository;

import com.thelearnhub.commercehub.user.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    Optional<UserProfile> findByEmail(String email);

    boolean existsByEmail(String email);
}
