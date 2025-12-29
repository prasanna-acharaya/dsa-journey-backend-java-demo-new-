package com.bom.dsa.service;

import com.bom.dsa.dto.request.LoginRequest;
import com.bom.dsa.dto.response.LoginResponse;
import com.bom.dsa.entity.User;
import com.bom.dsa.exception.CustomExceptions;
import com.bom.dsa.repository.UserRepository;
import com.bom.dsa.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Service for authentication operations.
 * Handles user login, token generation, and user management.
 */
@Service
public class AuthService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ReactiveAuthenticationManager authenticationManager;
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    /**
     * Constructor with @Lazy on authenticationManager to break circular dependency.
     * The cycle is: AuthService (ReactiveUserDetailsService) ->
     * ReactiveAuthenticationManager -> ReactiveUserDetailsService
     */
    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            @Lazy ReactiveAuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Find user by username (DSA unique code) for Spring Security.
     * 
     * @param username the DSA unique code
     * @return Mono containing UserDetails
     */
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        log.debug("Finding user by username: {}", username);

        return Mono.fromCallable(() -> {
            try {
                User user = userRepository.findByDsaUniqueCode(username)
                        .or(() -> userRepository.findByEmail(username))
                        .orElseThrow(() -> {
                            log.warn("User not found with dsaUniqueCode or email: {}", username);
                            return new CustomExceptions.ResourceNotFoundException("User", "username", username);
                        });
                log.debug("User found: {}", user.getDsaUniqueCode());
                return user;
            } catch (CustomExceptions.ResourceNotFoundException e) {
                throw e;
            } catch (Exception e) {
                log.error("Error finding user by username: {}", username, e);
                throw new CustomExceptions.BusinessException("Failed to find user: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic()).cast(UserDetails.class);
    }

    /**
     * Authenticate user and return JWT token.
     * 
     * @param request the login request
     * @return Mono containing login response with JWT token
     */
    public Mono<LoginResponse> login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        return authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()))
                .flatMap(authentication -> Mono.fromCallable(() -> {
                    try {
                        User user = userRepository.findByDsaUniqueCode(request.getUsername())
                                .or(() -> userRepository.findByEmail(request.getUsername()))
                                .orElseThrow(() -> {
                                    log.warn("User not found after authentication: {}", request.getUsername());
                                    return new CustomExceptions.ResourceNotFoundException("User", "username",
                                            request.getUsername());
                                });

                        // Account locking check removed as per user request

                        // Record successful login
                        user.recordSuccessfulLogin();
                        userRepository.save(user);
                        log.info("Successful login for user: {}", user.getDsaUniqueCode());

                        String token = jwtTokenProvider.generateToken(authentication);
                        log.debug("JWT token generated for user: {}", user.getDsaUniqueCode());

                        return LoginResponse.builder()
                                .accessToken(token)
                                .tokenType("Bearer")
                                .expiresIn(jwtTokenProvider.getExpirationTime())
                                .userId(user.getId())
                                .username(user.getUsername())
                                .dsaUniqueCode(user.getDsaUniqueCode())
                                .fullName(user.getFullName())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .lastLoginAt(user.getLastLoginAt())
                                .build();

                    } catch (CustomExceptions.ResourceNotFoundException | CustomExceptions.UnauthorizedException e) {
                        throw e;
                    } catch (Exception e) {
                        log.error("Error during login process for user: {}", request.getUsername(), e);
                        throw new CustomExceptions.BusinessException("Login failed: " + e.getMessage());
                    }
                }).subscribeOn(Schedulers.boundedElastic()))
                .onErrorResume(ex -> {
                    if (ex instanceof CustomExceptions.UnauthorizedException ||
                            ex instanceof CustomExceptions.ResourceNotFoundException) {
                        return Mono.error(ex);
                    }

                    log.error("Authentication failed for user: {}. Exception: {}", request.getUsername(),
                            ex.getClass().getName(), ex);
                    // Record failed login attempt
                    return Mono.<LoginResponse>fromCallable(() -> {
                        userRepository.findByDsaUniqueCode(request.getUsername())
                                .or(() -> userRepository.findByEmail(request.getUsername()))
                                .ifPresentOrElse(user -> {
                                    user.recordFailedLogin();
                                    userRepository.save(user);
                                    log.info("Recorded failed login attempt for user: {}, attempts: {}",
                                            user.getDsaUniqueCode(), user.getFailedLoginAttempts());
                                }, () -> log.warn("User not found during login failure handling: {}",
                                        request.getUsername()));
                        throw new CustomExceptions.UnauthorizedException("Invalid credentials");
                    }).subscribeOn(Schedulers.boundedElastic());
                });
    }

    /**
     * Create a new user.
     * 
     * @param user the user to create
     * @return Mono containing the created user
     */
    @Transactional
    public Mono<User> createUser(User user) {
        log.info("Creating new user with dsaUniqueCode: {}", user.getDsaUniqueCode());

        return Mono.fromCallable(() -> {
            try {
                // Check for duplicate DSA code
                if (userRepository.existsByDsaUniqueCode(user.getDsaUniqueCode())) {
                    log.warn("Duplicate DSA unique code: {}", user.getDsaUniqueCode());
                    throw new CustomExceptions.DuplicateResourceException("User", "dsaUniqueCode",
                            user.getDsaUniqueCode());
                }

                // Check for duplicate email
                if (userRepository.existsByEmail(user.getEmail())) {
                    log.warn("Duplicate email: {}", user.getEmail());
                    throw new CustomExceptions.DuplicateResourceException("User", "email", user.getEmail());
                }

                // Encode password
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                log.debug("Password encoded for user: {}", user.getDsaUniqueCode());

                User savedUser = userRepository.save(user);
                log.info("Successfully created user: {}", savedUser.getDsaUniqueCode());

                return savedUser;

            } catch (CustomExceptions.DuplicateResourceException e) {
                throw e;
            } catch (Exception e) {
                log.error("Error creating user: {}", user.getDsaUniqueCode(), e);
                throw new CustomExceptions.BusinessException("Failed to create user: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Find user by DSA unique code.
     * 
     * @param dsaUniqueCode the DSA unique code
     * @return Mono containing the user
     */
    public Mono<User> findUserByDsaUniqueCode(String dsaUniqueCode) {
        log.debug("Finding user by dsaUniqueCode: {}", dsaUniqueCode);

        return Mono.fromCallable(() -> {
            try {
                return userRepository.findByDsaUniqueCode(dsaUniqueCode)
                        .orElseThrow(() -> {
                            log.warn("User not found with dsaUniqueCode: {}", dsaUniqueCode);
                            return new CustomExceptions.ResourceNotFoundException("User", "dsaUniqueCode",
                                    dsaUniqueCode);
                        });
            } catch (CustomExceptions.ResourceNotFoundException e) {
                throw e;
            } catch (Exception e) {
                log.error("Error finding user by dsaUniqueCode: {}", dsaUniqueCode, e);
                throw new CustomExceptions.BusinessException("Failed to find user: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Logout user by invalidating token (placeholder for future implementation).
     * 
     * @param token the JWT token to invalidate
     * @return Mono<Void>
     */
    public Mono<Void> logout(String token) {
        log.info("Logout requested");
        // In a real implementation, you would add the token to a blacklist
        // For now, we just log the logout
        return Mono.fromRunnable(() -> {
            log.debug("Token invalidation would happen here (not implemented)");
        }).then();
    }
}
