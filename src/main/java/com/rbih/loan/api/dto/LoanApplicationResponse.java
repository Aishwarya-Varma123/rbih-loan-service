package com.rbih.loan.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rbih.loan.domain.ApplicationStatus;
import com.rbih.loan.domain.RiskBand;

import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoanApplicationResponse(
        UUID applicationId,
        ApplicationStatus status,
        RiskBand riskBand,
        OfferDto offer,
        List<String> rejectionReasons
) {
}
