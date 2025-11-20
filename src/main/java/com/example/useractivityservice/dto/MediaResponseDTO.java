package com.example.useractivityservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class MediaResponseDTO {
    private UUID mediaId;
    private String genreName;
}
