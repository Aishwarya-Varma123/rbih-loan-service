package com.rbih.loan.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanApplicationRepository extends JpaRepository<LoanApplicationEntity, UUID> {
}
