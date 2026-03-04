package id.ac.ui.cs.advprog.yomu.authentication.dto.request;

import lombok.Data;

@Data
public class UpdateRequest {
    private String username;
    private String displayName;
    private String email;
    private String phoneNumber;
    private String password; // password baru, jika ingin ganti
}