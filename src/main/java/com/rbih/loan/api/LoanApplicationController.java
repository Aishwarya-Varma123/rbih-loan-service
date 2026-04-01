package com.rbih.loan.api;

import com.rbih.loan.api.dto.CreateLoanApplicationRequest;
import com.rbih.loan.api.dto.LoanApplicationResponse;
import com.rbih.loan.service.LoanApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/applications")
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    public LoanApplicationController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    @PostMapping
    public ResponseEntity<LoanApplicationResponse> createApplication(
            @Valid @RequestBody CreateLoanApplicationRequest request) {
        LoanApplicationResponse response = loanApplicationService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
