package id.ac.ui.cs.advprog.yomu.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCommentRequest {
    @NotBlank(message = "Isi komentar tidak boleh kosong")
    @Size(max = 1000, message = "Isi komentar maksimal 1000 karakter")
    private String isiKomentar;
}

