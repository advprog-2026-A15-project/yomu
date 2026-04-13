package id.ac.ui.cs.advprog.yomu.forum.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateCommentRequest {
    private String isiKomentar;
    private UUID bacaanId;
    private UUID parentCommentId;
}

