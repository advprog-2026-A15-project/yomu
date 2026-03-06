package id.ac.ui.cs.advprog.yomu.forum.dto;

import java.util.UUID;

public class CreateCommentRequest {
    private String isiKomentar;
    private UUID bacaanId;

    public String getIsiKomentar() {
        return isiKomentar;
    }

    public void setIsiKomentar(String isiKomentar) {
        this.isiKomentar = isiKomentar;
    }

    public UUID getBacaanId() {
        return bacaanId;
    }

    public void setBacaanId(UUID bacaanId) {
        this.bacaanId = bacaanId;
    }
}

