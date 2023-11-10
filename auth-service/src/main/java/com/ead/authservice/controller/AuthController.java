package com.ead.authservice.controller;

import com.ead.authservice.dto.AuthenticationRequestDTO;
import com.ead.authservice.dto.AuthenticationResponseDTO;
import com.ead.authservice.dto.RegistrationRequestDTO;
import com.ead.authservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(
            @RequestBody RegistrationRequestDTO request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(
            @RequestBody AuthenticationRequestDTO request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/validateToken")
    public ResponseEntity<Boolean> validateToken(
            @RequestParam String token
    ) {
        return ResponseEntity.ok(service.validateToken(token));
    }
}