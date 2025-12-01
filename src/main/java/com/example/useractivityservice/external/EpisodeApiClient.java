package com.example.useractivityservice.external;

import com.example.useractivityservice.configs.UserInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Service
public class EpisodeApiClient {

    private final RestClient restClient;
    @Value("${episodeExists.api.url}")
    private String episodeExistsApiUrl;
    @Value("${episodeAdd.api.url}")
    private String episodeAddApiUrl;
    @Value("${episodeRemove.api.url}")
    private String episodeRemoveApiUrl;
    private final UserInfo userInfo;
    private static final Logger F_LOG = LogManager.getLogger("functionality");

    @Autowired
    public EpisodeApiClient(RestClient.Builder restClientBuilder, UserInfo userInfo) {
        this.restClient = restClientBuilder.build();
        this.userInfo = userInfo;
    }

    public Boolean getEpisode(UUID episodeId) {
        String role = userInfo.getRole();
        try {
            ResponseEntity<Boolean> episodeExistsResponse = restClient.get()
                    .uri(episodeExistsApiUrl, episodeId)
                    .retrieve()
                    .toEntity(Boolean.class);
            if (episodeExistsResponse.getStatusCode().is2xxSuccessful() && episodeExistsResponse.getBody() != null) {
                F_LOG.info("{} successfully checked if episode exists.", role);
                return episodeExistsResponse.getBody();
            } else {
                F_LOG.warn("{}: Episod exist check failed: {}", role, episodeExistsResponse.getStatusCode());
                throw new IllegalStateException(
                        episodeExistsResponse.getStatusCode().toString());
            }
        } catch (RestClientException e) {
            F_LOG.warn("{}: Episod exist check failed: {}", role, e.getMessage());
            throw new IllegalStateException("Failed to check episode " + episodeId, e);
        }
    }


}
