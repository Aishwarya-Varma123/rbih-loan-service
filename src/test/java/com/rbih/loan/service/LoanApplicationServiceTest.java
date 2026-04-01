package com.rbih.loan.service;

import com.rbih.loan.api.dto.ApplicantDto;
import com.rbih.loan.api.dto.CreateLoanApplicationRequest;
import com.rbih.loan.api.dto.LoanApplicationResponse;
import com.rbih.loan.api.dto.LoanDto;
import com.rbih.loan.domain.ApplicationStatus;
import com.rbih.loan.domain.EmploymentType;
import com.rbih.loan.domain.LoanPurpose;
import com.rbih.loan.domain.RejectionReason;
import com.rbih.loan.domain.RiskBand;
import com.rbih.loan.finance.EmiCalculator;
import com.rbih.loan.finance.InterestRateCalculator;
import com.rbih.loan.finance.RiskBandClassifier;
import com.rbih.loan.persistence.LoanApplicationEntity;
import com.rbih.loan.persistence.LoanApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {

    @Mock
    private LoanApplicationRepository repository;

    private LoanApplicationService service;

    @BeforeEach
    void setUp() {
        service = new LoanApplicationService(
                repository,
                new RiskBandClassifier(),
                new InterestRateCalculator(),
                new EmiCalculator());
    }

    @Test
    void rejectsWhenCreditScoreBelow600() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateLoanApplicationRequest req = request(
                new ApplicantDto("A", 30, new BigDecimal("75000"), EmploymentType.SALARIED, 599),
                new LoanDto(new BigDecimal("500000"), 36, LoanPurpose.PERSONAL));

        LoanApplicationResponse res = service.createApplication(req);

        assertEquals(ApplicationStatus.REJECTED, res.status());
        assertNull(res.riskBand());
        assertTrue(res.rejectionReasons().contains(RejectionReason.CREDIT_SCORE_BELOW_600.name()));
    }

    @Test
    void rejectsWhenEmiExceedsSixtyPercentOfIncome() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateLoanApplicationRequest req = request(
                new ApplicantDto("A", 30, new BigDecimal("5000"), EmploymentType.SALARIED, 750),
                new LoanDto(new BigDecimal("500000"), 36, LoanPurpose.PERSONAL));

        LoanApplicationResponse res = service.createApplication(req);

        assertEquals(ApplicationStatus.REJECTED, res.status());
        assertTrue(res.rejectionReasons().contains(RejectionReason.EMI_EXCEEDS_60_PERCENT.name()));
    }

    @Test
    void rejectsWhenAgePlusTenureExceeds65() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateLoanApplicationRequest req = request(
                new ApplicantDto("A", 64, new BigDecimal("75000"), EmploymentType.SALARIED, 720),
                new LoanDto(new BigDecimal("500000"), 36, LoanPurpose.PERSONAL));

        LoanApplicationResponse res = service.createApplication(req);

        assertEquals(ApplicationStatus.REJECTED, res.status());
        assertTrue(res.rejectionReasons().contains(RejectionReason.AGE_TENURE_LIMIT_EXCEEDED.name()));
    }

    @Test
    void approvesWhenAllRulesPass() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateLoanApplicationRequest req = request(
                new ApplicantDto("A", 30, new BigDecimal("75000"), EmploymentType.SALARIED, 750),
                new LoanDto(new BigDecimal("500000"), 36, LoanPurpose.PERSONAL));

        LoanApplicationResponse res = service.createApplication(req);

        assertEquals(ApplicationStatus.APPROVED, res.status());
        assertEquals(RiskBand.LOW, res.riskBand());
        assertNotNull(res.offer());
        assertEquals(36, res.offer().tenureMonths());
        assertNull(res.rejectionReasons());
    }

    @Test
    void persistsDecision() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateLoanApplicationRequest req = request(
                new ApplicantDto("A", 30, new BigDecimal("75000"), EmploymentType.SALARIED, 750),
                new LoanDto(new BigDecimal("500000"), 36, LoanPurpose.PERSONAL));

        service.createApplication(req);

        ArgumentCaptor<LoanApplicationEntity> captor = ArgumentCaptor.forClass(LoanApplicationEntity.class);
        verify(repository).save(captor.capture());
        assertEquals(ApplicationStatus.APPROVED, captor.getValue().getStatus());
    }

    private static CreateLoanApplicationRequest request(ApplicantDto applicant, LoanDto loan) {
        return new CreateLoanApplicationRequest(applicant, loan);
    }
}
