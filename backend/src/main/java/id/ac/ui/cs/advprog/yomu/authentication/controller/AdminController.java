package id.ac.ui.cs.advprog.yomu.authentication.controller;

import id.ac.ui.cs.advprog.yomu.authentication.dto.request.LoginRequest;
import id.ac.ui.cs.advprog.yomu.authentication.dto.response.AuthResponse;
import id.ac.ui.cs.advprog.yomu.authentication.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> adminLogin(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        // Pastikan role adalah ADMIN
        if (!"ADMIN".equals(response.getRole())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(response);
    }

    // Endpoint admin lainnya dapat ditambahkan di sini
}