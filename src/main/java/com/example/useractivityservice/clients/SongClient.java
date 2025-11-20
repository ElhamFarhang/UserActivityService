package com.example.useractivityservice.clients;

import com.example.useractivityservice.dto.MediaResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "songservice")
public class SongClient {
    @GetMapping("/songs/by-url")
    MediaResponseDTO getSongByUrl(@RequestParam("url") String url){
        return null;
    }
}
