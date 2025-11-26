package com.example.useractivityservice.mapper;

import com.example.useractivityservice.dto.HistoryDTO;
import com.example.useractivityservice.entities.UserActivity;
import org.springframework.stereotype.Component;


@Component
public class DtoConverter {

    public HistoryDTO makeHistoryDTO(UserActivity userActivity) {
        HistoryDTO dto = new HistoryDTO();
        dto.setMediaId(userActivity.getMediaId());
        dto.setMediaType(userActivity.getMediaType());
        dto.setPlayedAt(userActivity.getPlayedAt());
        dto.setGenreName(userActivity.getGenreName());
        return dto;
    }

}
