package com.rbih.loan.finance;

import com.rbih.loan.domain.RiskBand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RiskBandClassifierTest {

    private RiskBandClassifier classifier;

    @BeforeEach
    void setUp() {
        classifier = new RiskBandClassifier();
    }

    @Test
    void low_whenScoreAtLeast750() {
        assertEquals(RiskBand.LOW, classifier.classify(750));
        assertEquals(RiskBand.LOW, classifier.classify(900));
    }

    @Test
    void medium_whenScoreBetween650And749() {
        assertEquals(RiskBand.MEDIUM, classifier.classify(650));
        assertEquals(RiskBand.MEDIUM, classifier.classify(700));
        assertEquals(RiskBand.MEDIUM, classifier.classify(749));
    }

    @Test
    void high_whenScoreBetween600And649() {
        assertEquals(RiskBand.HIGH, classifier.classify(600));
        assertEquals(RiskBand.HIGH, classifier.classify(620));
        assertEquals(RiskBand.HIGH, classifier.classify(649));
    }
}
