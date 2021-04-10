package com.imsimarket.b2b.client.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddBalanceDTO {
    private String type;
    private String param;
    private BigDecimal amount;
}
