package com.thelearnhub.commercehub.auth.service;

import com.thelearnhub.commercehub.auth.dto.AuthResponse;
import com.thelearnhub.commercehub.auth.dto.LoginRequest;
import com.thelearnhub.commercehub.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(String refreshToken);

    AuthResponse loginWithGoogle(String googleIdToken);
}
