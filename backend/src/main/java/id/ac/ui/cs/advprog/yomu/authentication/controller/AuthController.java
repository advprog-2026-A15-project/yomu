package id.ac.ui.cs.advprog.yomu.authentication.controller;

import id.ac.ui.cs.advprog.yomu.authentication.dto.request.*;
import id.ac.ui.cs.advprog.yomu.authentication.dto.response.AuthResponse;
import id.ac.ui.cs.advprog.yomu.authentication.dto.response.UserResponse;
import id.ac.ui.cs.advprog.yomu.authentication.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/oauth2/google")
    public ResponseEntity<AuthResponse> googleAuth(@RequestBody GoogleAuthRequest request) {
        return ResponseEntity.ok(authService.googleAuth(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUserProfile());
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateRequest request) {
        return ResponseEntity.ok(authService.updateCurrentUser(request));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser() {
        authService.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }
}