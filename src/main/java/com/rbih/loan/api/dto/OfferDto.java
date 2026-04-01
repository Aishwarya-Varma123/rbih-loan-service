package com.rbih.loan.api.dto;

import java.math.BigDecimal;

public record OfferDto(
        BigDecimal interestRate,
        int tenureMonths,
        BigDecimal emi,
        BigDecimal totalPayable
) {
}
