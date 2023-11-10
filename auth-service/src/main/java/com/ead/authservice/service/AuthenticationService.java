package com.ead.authservice.service;

import com.ead.authservice.dto.AuthenticationRequestDTO;
import com.ead.authservice.dto.AuthenticationResponseDTO;
import com.ead.authservice.dto.RegistrationRequestDTO;
import com.ead.authservice.enums.RoleType;
import com.ead.authservice.enums.TokenType;
import com.ead.authservice.model.Token;
import com.ead.authservice.model.User;
import com.ead.authservice.repository.TokenRepository;
import com.ead.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponseDTO register(RegistrationRequestDTO request) {
        var role = request.getRole();

        if(!(role.equals(RoleType.CUSTOMER) || role.equals(RoleType.DELIVERY_PERSON))) {
           throw new RuntimeException("Invalid role");
        }

        var currentUser = userRepository.findByEmailAndRole(request.getEmail(), role);

        if (currentUser.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        var user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .role(request.getRole())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponseDTO.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        var authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        if(!authenticate.isAuthenticated()) {
            throw new RuntimeException("Invalid credentials");
        }

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponseDTO.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Boolean validateToken(String token) {
        var user = tokenRepository.findUserByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if(user == null) throw new RuntimeException("Invalid token");

        return jwtService.isTokenValid(token, user);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

}
