package org.beethoven.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.beethoven.lib.exception.AuthenticationException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * Client for the GitHub OAuth and user APIs.
 */
@Slf4j
@Component
public class GitHubOAuthClient {

    private static final String USER_AGENT = "Beethoven-Music";
    private static final MediaType GITHUB_JSON = MediaType.parseMediaType("application/vnd.github+json");
    private static final ParameterizedTypeReference<List<GitHubEmail>> EMAIL_LIST_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient oauthClient;
    private final RestClient apiClient;

    public GitHubOAuthClient(RestClient.Builder restClientBuilder) {
        this.oauthClient = restClientBuilder.clone()
                .baseUrl("https://github.com")
                .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT)
                .build();
        this.apiClient = restClientBuilder.clone()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT)
                .defaultHeader(HttpHeaders.ACCEPT, GITHUB_JSON.toString())
                .build();
    }

    public String exchangeAccessToken(String clientId, String clientSecret, String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("code", code);

        try {
            GitHubAccessTokenResponse response = oauthClient.post()
                    .uri("/login/oauth/access_token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(form)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, clientResponse) -> {
                        throw new AuthenticationException("Get access token error!");
                    })
                    .body(GitHubAccessTokenResponse.class);
            if (response == null || !StringUtils.hasText(response.accessToken())) {
                throw new AuthenticationException("Get access token error!");
            }
            return response.accessToken();
        } catch (AuthenticationException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("fetch access token error", e);
            throw new AuthenticationException("Fetch access token error!");
        }
    }

    public GitHubUser getUser(String accessToken) {
        try {
            GitHubUser response = apiClient.get()
                    .uri("/user")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, clientResponse) -> {
                        throw new AuthenticationException("Get user info error!");
                    })
                    .body(GitHubUser.class);
            if (response == null) {
                throw new AuthenticationException("Get user info error: empty response body!");
            }
            return response;
        } catch (AuthenticationException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("fetch user info error", e);
            throw new AuthenticationException("Fetch user info error!");
        }
    }

    public List<GitHubEmail> getEmails(String accessToken) {
        try {
            List<GitHubEmail> response = apiClient.get()
                    .uri("/user/emails")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, clientResponse) -> {
                        throw new AuthenticationException("Can't fetch your email address!");
                    })
                    .body(EMAIL_LIST_TYPE);
            if (response == null) {
                throw new AuthenticationException("Can't fetch your email address: empty response body!");
            }
            return response;
        } catch (AuthenticationException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("fetch email address error", e);
            throw new AuthenticationException("Can't fetch your email address!");
        }
    }

    public record GitHubAccessTokenResponse(
            @JsonProperty("access_token") String accessToken
    ) {
    }

    public record GitHubUser(
            String name,
            String email,
            @JsonProperty("avatar_url") String avatarUrl
    ) {
    }

    public record GitHubEmail(
            String email,
            boolean primary
    ) {
    }
}
