package id.ac.ui.cs.advprog.yomu.authentication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String displayName;
    private String email;
    private String phoneNumber;
    private String role;

    public AuthResponse(String token, Long id, String username, String displayName,
                        String email, String phoneNumber, String role) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }
}