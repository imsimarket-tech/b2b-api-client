package com.imsimarket.b2b.client.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imsimarket.b2b.client.exceptions.HttpNetworkException;
import com.imsimarket.b2b.client.exceptions.JsonException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class TokenProviderImpl implements TokenProvider {

    private static final Logger log = LoggerFactory.getLogger(TokenProviderImpl.class);

    private final String apiEndpoint;
    private final String username;
    private final String password;

    private String accessToken = "";
    private String refreshToken = "";

    private final OkHttpClient httpClient;

    public TokenProviderImpl(OkHttpClient httpClient, String authApiEndpoint, String username, String password) {
        this.httpClient = httpClient;
        this.apiEndpoint = authApiEndpoint;
        this.username = username;
        this.password = password;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String refreshAccessToken() {
        AuthResponseDTO auth = requestTokensRefresh();
        accessToken = auth.getAccessToken();
        refreshToken = auth.getRefreshToken();

        return accessToken;
    }

    private AuthResponseDTO requestTokensRefresh() {
        log.debug("requestTokenRefresh(): try to refresh accessToken");
        if (refreshToken.isEmpty()) {
            log.debug("requestTokenRefresh(): refresh token not found, requesting new token pair");
            return requestNewTokens();
        }

        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .build();

        Request request = new Request.Builder()
                .url(apiEndpoint)
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.debug("requestTokenRefresh(): auth server returned error " + response);
                log.debug("requestTokenRefresh(): requesting new token pair");
                //response.close();
                return requestNewTokens();
            }

            String responseString = Objects.requireNonNull(response.body()).string();
            //response.close();

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(responseString, AuthResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new JsonException(e);
        } catch (IOException e) {
            throw new HttpNetworkException(e);
        }
    }

    private AuthResponseDTO requestNewTokens() {
        log.debug("requestNewTokens(): requesting new token pair");
        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "password")
                .add("username", username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(apiEndpoint)
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response);
            }

            String responseString = Objects.requireNonNull(response.body()).string();
            //response.close();

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(responseString, AuthResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new JsonException(e);
        } catch (IOException e) {
            throw new HttpNetworkException(e);
        }
    }
}
