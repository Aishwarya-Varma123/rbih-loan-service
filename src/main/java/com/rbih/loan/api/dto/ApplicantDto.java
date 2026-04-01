package com.rbih.loan.api.dto;

import com.rbih.loan.domain.EmploymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ApplicantDto(
        @NotBlank(message = "Applicant name is required")
        String name,

        @NotNull(message = "Age is required")
        @Min(value = 21, message = "Age must be at least 21")
        @Max(value = 60, message = "Age must be at most 60")
        Integer age,

        @NotNull(message = "Monthly income is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Monthly income must be greater than 0")
        BigDecimal monthlyIncome,

        @NotNull(message = "Employment type is required")
        EmploymentType employmentType,

        @NotNull(message = "Credit score is required")
        @Min(value = 300, message = "Credit score must be at least 300")
        @Max(value = 900, message = "Credit score must be at most 900")
        Integer creditScore
) {
}
