package com.rbih.loan.service;

import com.rbih.loan.api.dto.ApplicantDto;
import com.rbih.loan.api.dto.CreateLoanApplicationRequest;
import com.rbih.loan.api.dto.LoanApplicationResponse;
import com.rbih.loan.api.dto.LoanDto;
import com.rbih.loan.api.dto.OfferDto;
import com.rbih.loan.domain.ApplicationStatus;
import com.rbih.loan.domain.RejectionReason;
import com.rbih.loan.domain.RiskBand;
import com.rbih.loan.finance.EmiCalculator;
import com.rbih.loan.finance.InterestRateCalculator;
import com.rbih.loan.finance.RiskBandClassifier;
import com.rbih.loan.persistence.LoanApplicationEntity;
import com.rbih.loan.persistence.LoanApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LoanApplicationService {

    private static final BigDecimal MAX_AGE_PLUS_TENURE = new BigDecimal("65");
    private static final int TENURE_SCALE = 10;

    private final LoanApplicationRepository repository;
    private final RiskBandClassifier riskBandClassifier;
    private final InterestRateCalculator interestRateCalculator;
    private final EmiCalculator emiCalculator;

    public LoanApplicationService(LoanApplicationRepository repository,
            RiskBandClassifier riskBandClassifier,
            InterestRateCalculator interestRateCalculator,
            EmiCalculator emiCalculator) {
        this.repository = repository;
        this.riskBandClassifier = riskBandClassifier;
        this.interestRateCalculator = interestRateCalculator;
        this.emiCalculator = emiCalculator;
    }

    @Transactional
    public LoanApplicationResponse createApplication(CreateLoanApplicationRequest request) {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();
        ApplicantDto applicant = request.applicant();
        LoanDto loan = request.loan();

        List<RejectionReason> reasons = new ArrayList<>();

        if (applicant.creditScore() < 600) {
            reasons.add(RejectionReason.CREDIT_SCORE_BELOW_600);
        }

        BigDecimal tenureYears = BigDecimal.valueOf(loan.tenureMonths())
                .divide(BigDecimal.valueOf(12), TENURE_SCALE, RoundingMode.HALF_UP);
        BigDecimal agePlusTenure = BigDecimal.valueOf(applicant.age()).add(tenureYears);
        if (agePlusTenure.compareTo(MAX_AGE_PLUS_TENURE) > 0) {
            reasons.add(RejectionReason.AGE_TENURE_LIMIT_EXCEEDED);
        }

        RiskBand riskBand = null;
        BigDecimal finalRatePercent = null;
        BigDecimal emi = null;

        if (reasons.isEmpty()) {
            riskBand = riskBandClassifier.classify(applicant.creditScore());
            finalRatePercent = interestRateCalculator.calculateFinalAnnualRatePercent(
                    riskBand, applicant.employmentType(), loan.amount());
            emi = emiCalculator.calculateEmi(loan.amount(), finalRatePercent, loan.tenureMonths());

            BigDecimal income = applicant.monthlyIncome();
            BigDecimal sixtyPercent = income.multiply(new BigDecimal("0.60")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal fiftyPercent = income.multiply(new BigDecimal("0.50")).setScale(2, RoundingMode.HALF_UP);

            if (emi.compareTo(sixtyPercent) > 0) {
                reasons.add(RejectionReason.EMI_EXCEEDS_60_PERCENT);
            } else if (emi.compareTo(fiftyPercent) > 0) {
                reasons.add(RejectionReason.EMI_EXCEEDS_50_PERCENT);
            }
        }

        boolean approved = reasons.isEmpty();
        ApplicationStatus status = approved ? ApplicationStatus.APPROVED : ApplicationStatus.REJECTED;

        OfferDto offerDto = null;
        BigDecimal totalPayable = null;
        if (approved) {
            totalPayable = emiCalculator.totalPayable(emi, loan.tenureMonths());
            offerDto = new OfferDto(finalRatePercent, loan.tenureMonths(), emi, totalPayable);
        }

        LoanApplicationEntity entity = new LoanApplicationEntity(
                id,
                applicant.name(),
                applicant.age(),
                applicant.monthlyIncome(),
                applicant.employmentType(),
                applicant.creditScore(),
                loan.amount(),
                loan.tenureMonths(),
                loan.purpose(),
                status,
                approved ? riskBand : null,
                approved ? List.of() : reasons,
                approved ? finalRatePercent : null,
                approved ? loan.tenureMonths() : null,
                approved ? emi : null,
                approved ? totalPayable : null,
                createdAt
        );

        repository.save(entity);

        return toResponse(entity, offerDto);
    }

    private LoanApplicationResponse toResponse(LoanApplicationEntity entity, OfferDto offerDto) {
        List<String> rejectionStrings = null;
        if (entity.getStatus() == ApplicationStatus.REJECTED) {
            rejectionStrings = entity.getRejectionReasons().stream()
                    .map(Enum::name)
                    .toList();
        }
        return new LoanApplicationResponse(
                entity.getId(),
                entity.getStatus(),
                entity.getRiskBand(),
                offerDto,
                rejectionStrings
        );
    }
}
