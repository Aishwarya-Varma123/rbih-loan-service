package com.rbih.loan.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CreateLoanApplicationRequest(
        @NotNull(message = "Applicant details are required")
        @Valid
        ApplicantDto applicant,

        @NotNull(message = "Loan details are required")
        @Valid
        LoanDto loan
) {
}
