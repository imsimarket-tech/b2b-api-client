package com.imsimarket.b2b.client;

import com.imsimarket.b2b.client.dto.ApiVersionDTO;
import com.imsimarket.b2b.client.dto.SimDTO;

import java.math.BigDecimal;

public interface ImsimarketApiClient {
    ApiVersionDTO getApiVersion();
    SimDTO getSimByIccid(String iccid);
    SimDTO getSimByImsi(String imsi);
    SimDTO getSimByPhoneNumber(String msisdn);
    void addBalance(String type, String param, BigDecimal amount);
    void addBalance(String type, String param, BigDecimal amount, boolean test);
}
