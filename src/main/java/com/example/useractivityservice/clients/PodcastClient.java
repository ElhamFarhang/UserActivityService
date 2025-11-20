package com.example.useractivityservice.clients;

import com.example.useractivityservice.dto.MediaResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "podcastservice")
public class PodcastClient {
    @GetMapping("/episodes/by-url")
    MediaResponseDTO getEpisodeByUrl(@RequestParam("url") String url){
        return null;
    }
}
