package com.rbih.loan.api.dto;

import com.rbih.loan.domain.LoanPurpose;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LoanDto(
        @NotNull(message = "Loan amount is required")
        @DecimalMin(value = "10000", inclusive = true, message = "Loan amount must be at least 10,000")
        @DecimalMax(value = "5000000", inclusive = true, message = "Loan amount must be at most 50,00,000")
        BigDecimal amount,

        @NotNull(message = "Tenure in months is required")
        @Min(value = 6, message = "Tenure must be at least 6 months")
        @Max(value = 360, message = "Tenure must be at most 360 months")
        Integer tenureMonths,

        @NotNull(message = "Loan purpose is required")
        LoanPurpose purpose
) {
}
