package id.ac.ui.cs.advprog.yomu.league.dto;

import lombok.Data;

@Data
public class CreateClanRequest {
    private String name;
    private String description; // optional
}

