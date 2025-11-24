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


    public List<UUID> getLikedOrDislikedMediaList(String userId, String mediaType, Boolean liked) {         //TODO ändra till uuid?
        String uri;
        String likedOrDisliked = " ";
        if (liked) {
            likedOrDisliked = " Liked";
        }
        if (!liked) {
            likedOrDisliked = " Disliked";
        }

        switch (mediaType) {                                                             //TODO rätt adresser
            case "song":
                uri = userServiceUrl + "/dislikedMediaList/" + userId + "/" + mediaType;
                break;
            case "video":
                uri = userServiceUrl + "/dislikedMediaList/" + userId + "/" + mediaType;
                break;
            case "podcast":
                uri = userServiceUrl + "/dislikedMediaList/" + userId + "/" + mediaType;
                break;
            default:
                throw new IllegalArgumentException("Unsupported media type: " + mediaType);
        }

        return getMediaList(uri);
    }


    public List<UUID> getMediaList(String uri) {
        try {
            ResponseEntity<List<UUID>> response = restClient.put()
                    .uri(uri)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<UUID>>() {});
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
