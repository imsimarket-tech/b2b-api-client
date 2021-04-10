package com.imsimarket.b2b.client.okhttp;

import com.imsimarket.b2b.client.authentication.TokenProvider;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class HttpAuthenticator implements Authenticator {

    private final TokenProvider tokenProvider;

    public HttpAuthenticator(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
        String accessToken = tokenProvider.getAccessToken();
        if (!isRequestWithAccessToken(response) || accessToken == null) {
            return null;
        }

        synchronized (this) {
            synchronized (this) {
                //check if another thread has updated the token
                String newAccessToken = tokenProvider.getAccessToken();
                if (!accessToken.equals(newAccessToken)) {
                    return newRequestWithAccessToken(response.request(), newAccessToken);
                }

                //refresh the token
                String updatedAccessToken = tokenProvider.refreshAccessToken();
                return newRequestWithAccessToken(response.request(), updatedAccessToken);
            }
        }
    }

    @NotNull
    private Request newRequestWithAccessToken(@NotNull Request request, String accessToken) {
        return request.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }

    private boolean isRequestWithAccessToken(@NotNull Response response) {
        String header = response.request().header("Authorization");
        return header != null && header.startsWith("Bearer");
    }
}
