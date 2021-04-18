package com.imsimarket.b2b.client;

import com.imsimarket.b2b.client.dto.ApiVersionDTO;
import com.imsimarket.b2b.client.dto.SimDTO;

import java.math.BigDecimal;

public class SimpleApiTest {

    public static final String USERNAME = "YOUR API USERNAME";
    public static final String PASSWORD = "YOUR API PASSWORD";
    public static final String TEST_IMSI = "REAL IMSI NUMBER";

    public static void main(String[] args) {
        ImsimarketApiClient apiClient = new ImsimarketApiClientImpl(USERNAME, PASSWORD);

        System.out.println("RUNNING API CALLS\n");

        System.out.println("Getting API version");
        ApiVersionDTO version = apiClient.getApiVersion();
        System.out.println("Product: " + version.getProduct() + ", version: " + version.getVersion() + ", reseller: " + version.getReseller());

        System.out.println("Getting IMSI 260060145035266");
        SimDTO sim0 = apiClient.getSimByImsi(TEST_IMSI);
        System.out.println("Result: " + "IMSI: " + sim0.getImsi() + ", ICCID: " + sim0.getIccid() + ", MSISDN: " + sim0.getMsisdn());

        System.out.println("Getting MSISDN " + sim0.getMsisdn());
        SimDTO sim1 = apiClient.getSimByPhoneNumber(sim0.getMsisdn());
        System.out.println("Result: " + "IMSI: " + sim1.getImsi() + ", ICCID: " + sim1.getIccid() + ", MSISDN: " + sim1.getMsisdn());

        System.out.println("Getting ICCID " + sim0.getIccid());
        SimDTO sim2 = apiClient.getSimByIccid(sim0.getIccid());
        System.out.println("Result: " + "IMSI: " + sim2.getImsi() + ", ICCID: " + sim2.getIccid() + ", MSISDN: " + sim2.getMsisdn());

        //This will send a real balance top-up request, use with caution
        System.out.println("Adding funds to balance");
        apiClient.addBalance("ICCID", sim0.getIccid(), BigDecimal.valueOf(0.1), true);
    }
}
