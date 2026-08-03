package org.andre.beethoven.service;

import org.beethoven.lib.exception.AuthenticationException;
import org.beethoven.service.GitHubOAuthClient;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Tests for the GitHub REST client without using the network or application infrastructure.
 */
public class AuthTest {

    @Test
    public void exchangeAccessTokenUsesJsonAndFormHeaders() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://github.com/login/oauth/access_token"))
                .andExpect(method(POST))
                .andExpect(header(HttpHeaders.USER_AGENT, "Beethoven-Music"))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .andExpect(content().formData(formData("client_id", "client", "client_secret", "secret", "code", "code")))
                .andRespond(withSuccess("{\"access_token\":\"token\"}", MediaType.APPLICATION_JSON));

        GitHubOAuthClient client = new GitHubOAuthClient(builder);

        assertEquals("token", client.exchangeAccessToken("client", "secret", "code"));
        server.verify();
    }

    @Test
    public void rejectsAccessTokenResponseWithoutToken() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://github.com/login/oauth/access_token"))
                .andRespond(withSuccess("{\"scope\":\"read:user\"}", MediaType.APPLICATION_JSON));

        GitHubOAuthClient client = new GitHubOAuthClient(builder);

        assertThrows(AuthenticationException.class,
                () -> client.exchangeAccessToken("client", "secret", "code"));
        server.verify();
    }

    @Test
    public void getUserAndEmailsDecodeGitHubPayloads() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://api.github.com/user"))
                .andExpect(method(GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(header(HttpHeaders.USER_AGENT, "Beethoven-Music"))
                .andRespond(withSuccess(
                        "{\"name\":\"Andre\",\"email\":\"user@example.com\",\"avatar_url\":\"https://avatar\"}",
                        MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://api.github.com/user/emails"))
                .andExpect(method(GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andRespond(withSuccess(
                        "[{\"email\":\"user@example.com\",\"primary\":true}]",
                        MediaType.APPLICATION_JSON));

        GitHubOAuthClient client = new GitHubOAuthClient(builder);

        GitHubOAuthClient.GitHubUser user = client.getUser("token");
        List<GitHubOAuthClient.GitHubEmail> emails = client.getEmails("token");

        assertEquals("Andre", user.name());
        assertEquals("https://avatar", user.avatarUrl());
        assertEquals(1, emails.size());
        assertEquals("user@example.com", emails.get(0).email());
        assertTrue(emails.get(0).primary());
        server.verify();
    }

    @Test
    public void mapsGitHubHttpErrorsToAuthenticationException() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://github.com/login/oauth/access_token"))
                .andRespond(withStatus(BAD_REQUEST));

        GitHubOAuthClient client = new GitHubOAuthClient(builder);

        assertThrows(AuthenticationException.class,
                () -> client.exchangeAccessToken("client", "secret", "code"));
        server.verify();
    }

    private static MultiValueMap<String, String> formData(String... values) {
        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        for (int i = 0; i < values.length; i += 2) {
            form.add(values[i], values[i + 1]);
        }
        return form;
    }
}
