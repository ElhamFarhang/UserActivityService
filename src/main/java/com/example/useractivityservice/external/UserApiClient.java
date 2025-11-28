package com.example.useractivityservice.external;

import com.example.useractivityservice.configs.UserInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private final UserInfo userInfo;
    private static final Logger FUNCTIONALITY_LOGGER = LogManager.getLogger("functionality");

    @Autowired
    public UserApiClient(RestClient.Builder restClientBuilder, UserInfo userInfo) {
        this.restClient = restClientBuilder.build();
        this.userInfo = userInfo;
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

        FUNCTIONALITY_LOGGER.info("{} retrieved like/dislikes for userId: {} from userService", userInfo.getRole(), userInfo.getUserId());
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
            FUNCTIONALITY_LOGGER.warn("{} unexpected failure to retrieve like/dislikes for userId: {} from userService", userInfo.getRole(), userInfo.getUserId());
            throw new IllegalStateException("Unexpected response from user service: " + response.getStatusCode());
        } catch (HttpClientErrorException e) {
            HttpStatusCode status = e.getStatusCode();
            String body = e.getResponseBodyAsString();
            FUNCTIONALITY_LOGGER.warn("{} failed to retrieve like/dislikes for userId: {} from userService", userInfo.getRole(), userInfo.getUserId());
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
            FUNCTIONALITY_LOGGER.warn("{} failed to connect to userService", userInfo.getRole());
            throw new IllegalStateException("Could not connect to user service: " + ex.getMessage(), ex);
        } catch (RestClientException ex) {
            FUNCTIONALITY_LOGGER.warn("{} encountered an error trying to connect to userService", userInfo.getRole());
            throw new IllegalStateException("Unexpected error calling user service", ex);
        }
    }

}
