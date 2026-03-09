package id.ac.ui.cs.advprog.yomu.authentication.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import id.ac.ui.cs.advprog.yomu.authentication.dto.request.*;
import id.ac.ui.cs.advprog.yomu.authentication.dto.response.AuthResponse;
import id.ac.ui.cs.advprog.yomu.authentication.dto.response.UserResponse;
import id.ac.ui.cs.advprog.yomu.authentication.model.Role;
import id.ac.ui.cs.advprog.yomu.authentication.model.User;
import id.ac.ui.cs.advprog.yomu.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${google.client-id:dummy-google-client-id}")
    private String clientId;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
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
        user.setRole(Role.PELAJAR);
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
        String idTokenString = request.getIdToken();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new RuntimeException("Invalid Google token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify Google token", e);
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String userId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        User user = userRepository.findByProviderAndProviderId("google", userId)
                .orElseGet(() -> userRepository.findByEmail(email)
                        .map(existingUser -> {
                            existingUser.setProvider("google");
                            existingUser.setProviderId(userId);
                            return userRepository.save(existingUser);
                        })
                        .orElseGet(() -> {
                            User newUser = new User();
                            String username = email.split("@")[0];
                            int suffix = 1;
                            String baseUsername = username;
                            while (userRepository.existsByUsername(username)) {
                                username = baseUsername + suffix;
                                suffix++;
                            }
                            newUser.setUsername(username);
                            newUser.setDisplayName(name != null ? name : email);
                            newUser.setEmail(email);
                            newUser.setProvider("google");
                            newUser.setProviderId(userId);
                            newUser.setRole(Role.PELAJAR);
                            return userRepository.save(newUser);
                        }));

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
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User belum login");
        }
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
}
