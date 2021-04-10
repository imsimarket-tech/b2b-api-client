package com.imsimarket.b2b.client.okhttp;

import com.imsimarket.b2b.client.authentication.TokenProvider;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AccessTokenInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(AccessTokenInterceptor.class);
    private final TokenProvider tokenProvider;

    public AccessTokenInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        String accessToken = tokenProvider.getAccessToken();
        if (accessToken.isEmpty()) {
            synchronized (this) {
                log.debug("AuthToken is empty, trying to fetch valid token");

                //check if another thread has updated the token
                String newAccessToken = tokenProvider.getAccessToken();
                if (!accessToken.equals(newAccessToken)) {
                    return chain.proceed(newRequestWithAccessToken(chain.request(), newAccessToken));
                }

                //refresh the token
                String updatedAccessToken = tokenProvider.refreshAccessToken();
                return chain.proceed(newRequestWithAccessToken(chain.request(), updatedAccessToken));
            }
        }

        return chain.proceed(newRequestWithAccessToken(chain.request(), accessToken));
    }

    @NotNull
    private Request newRequestWithAccessToken(@NotNull Request request, String accessToken) {
        return request.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }
}
