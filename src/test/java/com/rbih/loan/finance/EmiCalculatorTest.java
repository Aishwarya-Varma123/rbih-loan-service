package com.rbih.loan.finance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmiCalculatorTest {

    private EmiCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new EmiCalculator();
    }

    @Test
    void emi_matchesKnownCase_twelvePercentTwelveMonths() {
        BigDecimal emi = calculator.calculateEmi(new BigDecimal("100000"), new BigDecimal("12"), 12);
        assertEquals(new BigDecimal("8884.88"), emi);
    }

    @Test
    void singleMonth_emiIsPrincipalPlusOneMonthInterest() {
        BigDecimal emi = calculator.calculateEmi(new BigDecimal("100000"), new BigDecimal("12"), 1);
        assertEquals(new BigDecimal("101000.00"), emi);
    }

    @Test
    void totalPayable_isEmiTimesTenure() {
        BigDecimal emi = new BigDecimal("8884.88");
        assertEquals(new BigDecimal("106618.56"), calculator.totalPayable(emi, 12));
    }
}
