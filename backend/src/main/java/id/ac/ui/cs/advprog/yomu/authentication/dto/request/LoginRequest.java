package id.ac.ui.cs.advprog.yomu.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String identifier; // bisa username, email, atau nomor hp

    @NotBlank
    private String password;
}