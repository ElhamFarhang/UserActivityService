package com.example.useractivityservice.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserApiClient {


    private final RestClient restClient;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Autowired
    public UserApiClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }


    public List<UUID> getLikedOrDislikedMediaList(String userId, String mediaType, Boolean liked) {
        String uri = "";
        if (liked) {
            uri = userServiceUrl + "/user/likes/userlikes/map/" + userId;
        }
        if (!liked) {
            uri = userServiceUrl + "/user/dislikes/userdislikes/map/" + userId;
        }

        List<UUID> likedOrDislikedMediaList = getMediaMap(uri).get(mediaType);

        return likedOrDislikedMediaList;
    }


    public Map<String, List<UUID>> getMediaMap(String uri) {
        try {
            ResponseEntity<Map<String, List<UUID>>> response = restClient.put()
                    .uri(uri)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, List<UUID>>>() {});
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
            throw new IllegalStateException("Unexpected response from user service: " + response.getStatusCode());
        } catch (HttpClientErrorException e) {
            HttpStatusCode status = e.getStatusCode();
            String body = e.getResponseBodyAsString();
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(body);

                String message = json.path("message").asText();
                String path = json.path("path").asText();

                throw new IllegalStateException(
                        String.format("Failed to retrieve the users like/dislike information. Status %s, %s, Path:%s",
                                status, message, path), e);
            } catch (IOException parseEx) {
                throw new IllegalStateException("Failed to retrieve user like/dislike information. Status=" + status + " body=" + body, e);
            }
        } catch (ResourceAccessException ex) {
            throw new IllegalStateException("Could not connect to user service: " + ex.getMessage(), ex);
        } catch (RestClientException ex) {
            throw new IllegalStateException("Unexpected error calling user service", ex);
        }
    }

}
