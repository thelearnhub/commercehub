package com.thelearnhub.commercehub.user.controller;

import com.thelearnhub.commercehub.user.dto.CreateProfileRequest;
import com.thelearnhub.commercehub.user.dto.ProfileResponse;
import com.thelearnhub.commercehub.user.dto.UpdateProfileRequest;
import com.thelearnhub.commercehub.user.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "Profiles", description = "User profile management. All /me endpoints use the JWT subject to identify the caller.")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/profile")
    @Operation(summary = "Create a profile for the authenticated user (call once after registration)")
    public ResponseEntity<ProfileResponse> createProfile(
            Authentication authentication,
            @Valid @RequestBody CreateProfileRequest request
    ) {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profileService.createProfile(email, request));
    }

    @GetMapping("/me")
    @Operation(summary = "Get the authenticated user's own profile")
    public ResponseEntity<ProfileResponse> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(profileService.getProfile(email));
    }

    @PutMapping("/me")
    @Operation(summary = "Update the authenticated user's own profile")
    public ResponseEntity<ProfileResponse> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(profileService.updateProfile(email, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get any user's profile by ID (admin / inter-service only)")
    public ResponseEntity<ProfileResponse> getProfileById(@PathVariable UUID id) {
        return ResponseEntity.ok(profileService.getProfileById(id));
    }
}
