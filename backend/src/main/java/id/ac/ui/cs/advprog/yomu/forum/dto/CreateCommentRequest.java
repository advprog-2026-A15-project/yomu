package id.ac.ui.cs.advprog.yomu.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateCommentRequest {
    @NotBlank(message = "Isi komentar tidak boleh kosong")
    @Size(max = 1000, message = "Isi komentar maksimal 1000 karakter")
    private String isiKomentar;

    @NotNull(message = "Bacaan wajib dipilih")
    private UUID bacaanId;

    private UUID parentCommentId;
}

