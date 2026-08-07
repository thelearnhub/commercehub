package com.thelearnhub.commercehub.auth.domain;

/**
 * RBAC roles. Kept intentionally small for Phase 1 — extend as new
 * business capabilities (seller onboarding, support staff, etc.) arrive.
 */
public enum Role {
    CUSTOMER,
    SELLER,
    ADMIN
}
