package com.rbih.loan.finance;

import com.rbih.loan.domain.RiskBand;
import org.springframework.stereotype.Component;

@Component
public class RiskBandClassifier {

    /**
     * Credit score must be at least 600 before calling (otherwise application is rejected).
     */
    public RiskBand classify(int creditScore) {
        if (creditScore >= 750) {
            return RiskBand.LOW;
        }
        if (creditScore >= 650) {
            return RiskBand.MEDIUM;
        }
        return RiskBand.HIGH;
    }
}
