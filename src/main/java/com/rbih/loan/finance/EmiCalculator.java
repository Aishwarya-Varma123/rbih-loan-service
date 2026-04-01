package com.rbih.loan.finance;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Component
public class EmiCalculator {

    private static final int INTERMEDIATE_SCALE = 12;
    private static final MathContext MC = new MathContext(INTERMEDIATE_SCALE, RoundingMode.HALF_UP);

    /**
     * EMI = P * r * (1+r)^n / ((1+r)^n - 1)
     * where r is the monthly interest rate (annual percent / 12 / 100).
     */
    public BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualInterestRatePercent, int tenureMonths) {
        if (tenureMonths <= 0) {
            throw new IllegalArgumentException("Tenure must be positive");
        }
        BigDecimal monthlyRate = annualInterestRatePercent
                .divide(BigDecimal.valueOf(12), INTERMEDIATE_SCALE, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), INTERMEDIATE_SCALE, RoundingMode.HALF_UP);

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(tenureMonths), 2, RoundingMode.HALF_UP);
        }

        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal powN = onePlusR.pow(tenureMonths, MC);
        BigDecimal numerator = principal.multiply(monthlyRate, MC).multiply(powN, MC);
        BigDecimal denominator = powN.subtract(BigDecimal.ONE, MC);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal totalPayable(BigDecimal emi, int tenureMonths) {
        return emi.multiply(BigDecimal.valueOf(tenureMonths)).setScale(2, RoundingMode.HALF_UP);
    }
}
