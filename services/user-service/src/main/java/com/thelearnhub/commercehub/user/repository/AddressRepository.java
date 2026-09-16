package com.thelearnhub.commercehub.user.repository;

import com.thelearnhub.commercehub.user.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {

    List<Address> findByUserProfileId(UUID userProfileId);

    Optional<Address> findByIdAndUserProfileId(UUID id, UUID userProfileId);

    /** Clear the default flag on all addresses for a given user — called before
     *  setting a new default so at most one address is ever marked default. */
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.userProfile.id = :userProfileId AND a.isDefault = true")
    void clearDefaultForUser(UUID userProfileId);
}
