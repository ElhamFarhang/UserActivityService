package com.example.useractivityservice.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MediaResponseDTO {
    private UUID mediaId;
    private List<String> genres;
}
