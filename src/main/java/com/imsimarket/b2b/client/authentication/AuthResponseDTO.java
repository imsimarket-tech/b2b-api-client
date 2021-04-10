package com.imsimarket.b2b.client.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponseDTO {

    private final String tokenType;
    private final String accessToken;
    private final String refreshToken;
    private final long expiresIn;
    private final long refreshExpiresIn;

    public AuthResponseDTO(@JsonProperty(value = "token_type", required = true) String tokenType,
                           @JsonProperty(value = "access_token", required = true) String accessToken,
                           @JsonProperty(value = "refresh_token", required = true) String refreshToken,
                           @JsonProperty(value = "expires_in", required = true) long expiresIn,
                           @JsonProperty(value = "refresh_expires_in", required = true) long refreshExpiresIn) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.refreshExpiresIn = refreshExpiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public long getRefreshExpiresIn() {
        return refreshExpiresIn;
    }

    @Override
    public String toString() {
        return "token-type: " + tokenType + ", access_token: " + accessToken + " (" + expiresIn + "), refresh_token: " + refreshToken + " (" + refreshExpiresIn + ")";
    }
}
