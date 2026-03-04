package id.ac.ui.cs.advprog.yomu.authentication.service;

import id.ac.ui.cs.advprog.yomu.authentication.dto.request.*;
import id.ac.ui.cs.advprog.yomu.authentication.dto.response.AuthResponse;
import id.ac.ui.cs.advprog.yomu.authentication.dto.response.UserResponse;
import id.ac.ui.cs.advprog.yomu.authentication.model.Role;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validasi unik
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setDisplayName(request.getDisplayName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.PELAJAR); // default
        user.setProvider("local");

        user = userRepository.save(user);

        String token = jwtService.generateToken(user);
        return mapToAuthResponse(token, user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getIdentifier())
                .or(() -> userRepository.findByEmail(request.getIdentifier()))
                .or(() -> userRepository.findByPhoneNumber(request.getIdentifier()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(user);
        return mapToAuthResponse(token, user);
    }

    @Transactional
    public AuthResponse googleAuth(GoogleAuthRequest request) {
        // Implementasi verifikasi token Google (dummy di sini, harus diganti dengan verifikasi sesungguhnya)
        GoogleUserInfo googleUser = verifyGoogleToken(request.getIdToken());
        if (googleUser == null) {
            throw new RuntimeException("Invalid Google token");
        }

        User user = userRepository.findByProviderAndProviderId("google", googleUser.getSub())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(googleUser.getEmail()); // sementara email sebagai username (bisa diganti nanti)
                    newUser.setDisplayName(googleUser.getName());
                    newUser.setEmail(googleUser.getEmail());
                    newUser.setProvider("google");
                    newUser.setProviderId(googleUser.getSub());
                    newUser.setRole(Role.PELAJAR);
                    newUser.setPassword(null);
                    return userRepository.save(newUser);
                });

        String token = jwtService.generateToken(user);
        return mapToAuthResponse(token, user);
    }

    @Transactional
    public UserResponse updateCurrentUser(UpdateRequest request) {
        User currentUser = getCurrentUser();

        if (request.getUsername() != null && !request.getUsername().equals(currentUser.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username already taken");
            }
            currentUser.setUsername(request.getUsername());
        }
        if (request.getDisplayName() != null) {
            currentUser.setDisplayName(request.getDisplayName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(currentUser.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            currentUser.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(currentUser.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new RuntimeException("Phone number already in use");
            }
            currentUser.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getPassword() != null) {
            currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(currentUser);
        return mapToUserResponse(currentUser);
    }

    @Transactional
    public void deleteCurrentUser() {
        User currentUser = getCurrentUser();
        userRepository.delete(currentUser);
    }

    public UserResponse getCurrentUserProfile() {
        return mapToUserResponse(getCurrentUser());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private AuthResponse mapToAuthResponse(String token, User user) {
        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole().name()
        );
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setDisplayName(user.getDisplayName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole().name());
        return response;
    }

    // Simulasi verifikasi Google token (untuk production, gunakan GoogleTokenVerifier)
    private GoogleUserInfo verifyGoogleToken(String idToken) {
        // TODO: Implementasi verifikasi sesungguhnya
        // Contoh sederhana (hanya untuk demonstrasi):
        if (idToken == null || idToken.isEmpty()) return null;
        // Asumsikan token valid dan kita ekstrak informasi
        GoogleUserInfo info = new GoogleUserInfo();
        info.setSub("dummy-google-sub-12345");
        info.setEmail("user@gmail.com");
        info.setName("Google User");
        return info;
    }

    // Inner class untuk menyimpan info dari Google
    private static class GoogleUserInfo {
        private String sub;
        private String email;
        private String name;

        public String getSub() { return sub; }
        public void setSub(String sub) { this.sub = sub; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}