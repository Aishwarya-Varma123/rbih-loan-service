package com.rbih.loan.finance;

import com.rbih.loan.domain.EmploymentType;
import com.rbih.loan.domain.RiskBand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterestRateCalculatorTest {

    private InterestRateCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new InterestRateCalculator();
    }

    @Test
    void basePlusLowRiskSalariedSmallLoan_isTwelvePercent() {
        BigDecimal rate = calculator.calculateFinalAnnualRatePercent(
                RiskBand.LOW, EmploymentType.SALARIED, new BigDecimal("500000"));
        assertEquals(new BigDecimal("12.00"), rate);
    }

    @Test
    void mediumRiskAddsOnePointFivePercent() {
        BigDecimal rate = calculator.calculateFinalAnnualRatePercent(
                RiskBand.MEDIUM, EmploymentType.SALARIED, new BigDecimal("500000"));
        assertEquals(new BigDecimal("13.50"), rate);
    }

    @Test
    void highRiskSelfEmployedLoanAboveTenLakh_stacksPremiums() {
        BigDecimal rate = calculator.calculateFinalAnnualRatePercent(
                RiskBand.HIGH, EmploymentType.SELF_EMPLOYED, new BigDecimal("1000001"));
        assertEquals(new BigDecimal("16.50"), rate);
    }
}
