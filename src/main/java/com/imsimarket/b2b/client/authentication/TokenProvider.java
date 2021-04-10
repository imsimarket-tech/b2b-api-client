package com.imsimarket.b2b.client.authentication;

public interface TokenProvider {

    String getAccessToken();
    String refreshAccessToken();
}
