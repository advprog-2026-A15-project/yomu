package id.ac.ui.cs.advprog.yomu.authentication.dto.response;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String displayName;
    private String email;
    private String phoneNumber;
    private String role;
}