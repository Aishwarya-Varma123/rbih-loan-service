package com.rbih.loan.finance;

import com.rbih.loan.domain.EmploymentType;
import com.rbih.loan.domain.LoanConstants;
import com.rbih.loan.domain.RiskBand;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class InterestRateCalculator {

    private static final BigDecimal LOAN_SIZE_THRESHOLD = new BigDecimal("1000000");

    public BigDecimal calculateFinalAnnualRatePercent(RiskBand riskBand, EmploymentType employmentType,
            BigDecimal loanAmount) {
        BigDecimal rate = LoanConstants.BASE_ANNUAL_INTEREST_RATE_PERCENT;
        rate = rate.add(riskPremium(riskBand));
        rate = rate.add(employmentPremium(employmentType));
        rate = rate.add(loanSizePremium(loanAmount));
        return rate.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal riskPremium(RiskBand riskBand) {
        return switch (riskBand) {
            case LOW -> BigDecimal.ZERO;
            case MEDIUM -> new BigDecimal("1.50");
            case HIGH -> new BigDecimal("3.00");
        };
    }

    private BigDecimal employmentPremium(EmploymentType employmentType) {
        return switch (employmentType) {
            case SALARIED -> BigDecimal.ZERO;
            case SELF_EMPLOYED -> new BigDecimal("1.00");
        };
    }

    private BigDecimal loanSizePremium(BigDecimal loanAmount) {
        if (loanAmount.compareTo(LOAN_SIZE_THRESHOLD) > 0) {
            return new BigDecimal("0.50");
        }
        return BigDecimal.ZERO;
    }
}
