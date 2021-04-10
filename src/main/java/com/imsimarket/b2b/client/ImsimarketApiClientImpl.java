package com.imsimarket.b2b.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imsimarket.b2b.client.authentication.TokenProvider;
import com.imsimarket.b2b.client.authentication.TokenProviderImpl;
import com.imsimarket.b2b.client.dto.AddBalanceDTO;
import com.imsimarket.b2b.client.dto.ApiVersionDTO;
import com.imsimarket.b2b.client.dto.SimDTO;
import com.imsimarket.b2b.client.exceptions.HttpNetworkException;
import com.imsimarket.b2b.client.exceptions.JsonException;
import com.imsimarket.b2b.client.okhttp.AccessTokenInterceptor;
import com.imsimarket.b2b.client.okhttp.HttpAuthenticator;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

public class ImsimarketApiClientImpl implements ImsimarketApiClient {

    private static final Logger log = LoggerFactory.getLogger(ImsimarketApiClientImpl.class);
    public static final String authEndpoint = "https://mit.imsipay.com/auth/token";

    private final OkHttpClient httpClient;

    public ImsimarketApiClientImpl(String username, String password) {
        this(new OkHttpClient(), username, password);
    }

    public ImsimarketApiClientImpl(OkHttpClient okHttpClient, String username, String password) {
        TokenProvider tokenProvider = new TokenProviderImpl(okHttpClient, authEndpoint, username, password);
        this.httpClient = okHttpClient.newBuilder()
                .authenticator(new HttpAuthenticator(tokenProvider))
                .addInterceptor(new AccessTokenInterceptor(tokenProvider))
                .build();
    }

    @Override
    public ApiVersionDTO getApiVersion() {
        Request request = new Request.Builder()
                .url("https://mit.imsipay.com/api/v1")
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return new ObjectMapper().readValue(checkResponse(response), ApiVersionDTO.class);
        } catch (JsonProcessingException e) {
            throw new JsonException(e);
        } catch (IOException e) {
            throw new HttpNetworkException(e);
        }
    }

    @Override
    public SimDTO getSimByIccid(String iccid) {
        String url = "https://mit.imsipay.com/api/v1/sim/iccid/" + iccid;
        Request request = new Request.Builder()
                .url(url)
                .build();
        return parseResponseAndGetSimDTO(request);
    }

    @Override
    public SimDTO getSimByImsi(String imsi) {
        String url = "https://mit.imsipay.com/api/v1/sim/imsi/" + imsi;
        Request request = new Request.Builder()
                .url(url)
                .build();
        return parseResponseAndGetSimDTO(request);
    }

    @Override
    public SimDTO getSimByPhoneNumber(String msisdn) {
        String url = "https://mit.imsipay.com/api/v1/sim/msisdn/" + msisdn;
        Request request = new Request.Builder()
                .url(url)
                .build();
        return parseResponseAndGetSimDTO(request);
    }

    @Override
    public void addBalance(String type, String param, BigDecimal amount) {
        String url = "https://mit.imsipay.com/api/v1/balance/topup";

        AddBalanceDTO dto = new AddBalanceDTO();
        dto.setType(type);
        dto.setParam(param);
        dto.setAmount(amount);

        String json;
        try {
            json = new ObjectMapper().writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new JsonException(e);
        }

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        log.debug("REQUEST: " + json);

        try (Response response = httpClient.newCall(request).execute()) {
            checkResponse(response);
            log.debug("SUCCESS");
        } catch (JsonProcessingException e) {
            throw new JsonException(e);
        } catch (IOException e) {
            throw new HttpNetworkException(e);
        }
    }

    private SimDTO parseResponseAndGetSimDTO(Request request) {
        try (Response response = httpClient.newCall(request).execute()) {
            return new ObjectMapper().readValue(checkResponse(response), SimDTO.class);
        } catch (JsonProcessingException e) {
            throw new JsonException(e);
        } catch (IOException e) {
            throw new HttpNetworkException(e);
        }
    }

    private String checkResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            String error = "Request failed: (code " + response.code() + ")";
            log.debug(error);
            throw new IOException(error);
        }

        return Objects.requireNonNull(response.body()).string();
    }
}
