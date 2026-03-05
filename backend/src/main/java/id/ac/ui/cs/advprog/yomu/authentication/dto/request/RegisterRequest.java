package id.ac.ui.cs.advprog.yomu.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    private String displayName;

    private String email;      // optional, bisa null
    private String phoneNumber; // optional, bisa null

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;
}