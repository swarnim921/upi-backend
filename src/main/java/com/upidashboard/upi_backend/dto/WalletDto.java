package com.upidashboard.upi_backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletDto {
    private String walletId;
    private double balance;
    private String currency;
}
