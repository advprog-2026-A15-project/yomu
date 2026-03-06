package id.ac.ui.cs.advprog.yomu.authentication.dto.request;

import lombok.Data;

@Data
public class GoogleAuthRequest {
    private String idToken; // token dari Google
}