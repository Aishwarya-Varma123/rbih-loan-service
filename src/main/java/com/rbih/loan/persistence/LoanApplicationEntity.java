package com.rbih.loan.persistence;

import com.rbih.loan.domain.ApplicationStatus;
import com.rbih.loan.domain.EmploymentType;
import com.rbih.loan.domain.LoanPurpose;
import com.rbih.loan.domain.RejectionReason;
import com.rbih.loan.domain.RiskBand;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "loan_applications")
public class LoanApplicationEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String applicantName;

    @Column(nullable = false)
    private int applicantAge;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyIncome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType;

    @Column(nullable = false)
    private int creditScore;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal loanAmount;

    @Column(nullable = false)
    private int tenureMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanPurpose loanPurpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Enumerated(EnumType.STRING)
    private RiskBand riskBand;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "loan_application_rejection_reasons", joinColumns = @JoinColumn(name = "application_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    private List<RejectionReason> rejectionReasons = new ArrayList<>();

    @Column(precision = 10, scale = 2)
    private BigDecimal offerInterestRatePercent;

    private Integer offerTenureMonths;

    @Column(precision = 19, scale = 2)
    private BigDecimal offerEmi;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalPayable;

    @Column(nullable = false)
    private Instant createdAt;

    protected LoanApplicationEntity() {
    }

    public LoanApplicationEntity(UUID id, String applicantName, int applicantAge, BigDecimal monthlyIncome,
            EmploymentType employmentType, int creditScore, BigDecimal loanAmount, int tenureMonths,
            LoanPurpose loanPurpose, ApplicationStatus status, RiskBand riskBand,
            List<RejectionReason> rejectionReasons, BigDecimal offerInterestRatePercent, Integer offerTenureMonths,
            BigDecimal offerEmi, BigDecimal totalPayable, Instant createdAt) {
        this.id = id;
        this.applicantName = applicantName;
        this.applicantAge = applicantAge;
        this.monthlyIncome = monthlyIncome;
        this.employmentType = employmentType;
        this.creditScore = creditScore;
        this.loanAmount = loanAmount;
        this.tenureMonths = tenureMonths;
        this.loanPurpose = loanPurpose;
        this.status = status;
        this.riskBand = riskBand;
        this.rejectionReasons = rejectionReasons != null ? new ArrayList<>(rejectionReasons) : new ArrayList<>();
        this.offerInterestRatePercent = offerInterestRatePercent;
        this.offerTenureMonths = offerTenureMonths;
        this.offerEmi = offerEmi;
        this.totalPayable = totalPayable;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public int getApplicantAge() {
        return applicantAge;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public int getTenureMonths() {
        return tenureMonths;
    }

    public LoanPurpose getLoanPurpose() {
        return loanPurpose;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public RiskBand getRiskBand() {
        return riskBand;
    }

    public List<RejectionReason> getRejectionReasons() {
        return rejectionReasons;
    }

    public BigDecimal getOfferInterestRatePercent() {
        return offerInterestRatePercent;
    }

    public Integer getOfferTenureMonths() {
        return offerTenureMonths;
    }

    public BigDecimal getOfferEmi() {
        return offerEmi;
    }

    public BigDecimal getTotalPayable() {
        return totalPayable;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
