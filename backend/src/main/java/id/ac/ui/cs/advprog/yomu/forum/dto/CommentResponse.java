package id.ac.ui.cs.advprog.yomu.forum.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CommentResponse {
    private UUID id;
    private String isiKomentar;
    private UUID bacaanId;
    private String username;
    private LocalDateTime createdAt;
    private UUID parentId;
    private List<CommentResponse> replies = new ArrayList<>();
}

