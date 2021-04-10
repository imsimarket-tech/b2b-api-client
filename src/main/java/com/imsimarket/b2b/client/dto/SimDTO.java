package com.imsimarket.b2b.client.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SimDTO {
    private String iccid;
    private String imsi;
    private String msisdn;
    private BigDecimal balance;
}
