package com.thelearnhub.commercehub.auth.service;

import com.thelearnhub.commercehub.auth.domain.Role;
import com.thelearnhub.commercehub.auth.domain.User;
import com.thelearnhub.commercehub.auth.dto.AuthResponse;
import com.thelearnhub.commercehub.auth.dto.LoginRequest;
import com.thelearnhub.commercehub.auth.dto.RegisterRequest;
import com.thelearnhub.commercehub.auth.exception.EmailAlreadyExistsException;
import com.thelearnhub.commercehub.auth.exception.InvalidCredentialsException;
import com.thelearnhub.commercehub.auth.exception.InvalidRefreshTokenException;
import com.thelearnhub.commercehub.auth.repository.UserRepository;
import com.thelearnhub.commercehub.auth.security.GoogleTokenVerifier;
import com.thelearnhub.commercehub.auth.security.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final GoogleTokenVerifier googleTokenVerifier;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            GoogleTokenVerifier googleTokenVerifier
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.googleTokenVerifier = googleTokenVerifier;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = new User(request.email(), passwordEncoder.encode(request.password()), Role.CUSTOMER);
        userRepository.save(user);

        return issueTokens(user);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        // Google-authenticated accounts have no local password to check
        // against — sending them through the password flow would otherwise
        // NPE on a null passwordHash.
        if (user.getPassword() == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        user.recordLogin();
        userRepository.save(user);

        return issueTokens(user);
    }

    @Override
    @Transactional
    public AuthResponse loginWithGoogle(String googleIdToken) {
        GoogleTokenVerifier.GoogleUserInfo googleUser = googleTokenVerifier.verify(googleIdToken);

        User user = userRepository.findByEmail(googleUser.email())
                .orElseGet(() -> userRepository.save(User.createGoogleUser(googleUser.email())));

        user.recordLogin();
        userRepository.save(user);

        return issueTokens(user);
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        Claims claims = jwtService.parseClaims(refreshToken)
                .filter(jwtService::isRefreshToken)
                .orElseThrow(InvalidRefreshTokenException::new);

        User user = userRepository.findByEmail(claims.getSubject())
                .orElseThrow(InvalidRefreshTokenException::new);

        return issueTokens(user);
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken, jwtService.accessTokenTtlSeconds(), user.getRole().name());
    }
}
