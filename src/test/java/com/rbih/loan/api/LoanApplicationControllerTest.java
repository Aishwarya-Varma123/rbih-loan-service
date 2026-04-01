package com.rbih.loan.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbih.loan.api.dto.CreateLoanApplicationRequest;
import com.rbih.loan.domain.ApplicationStatus;
import com.rbih.loan.domain.EmploymentType;
import com.rbih.loan.domain.LoanPurpose;
import com.rbih.loan.service.LoanApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoanApplicationController.class)
@Import(GlobalExceptionHandler.class)
class LoanApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoanApplicationService loanApplicationService;

    @Test
    void validationFails_returns400() throws Exception {
        String body = """
                {
                  "applicant": {
                    "name": "X",
                    "age": 18,
                    "monthlyIncome": 75000,
                    "employmentType": "SALARIED",
                    "creditScore": 720
                  },
                  "loan": {
                    "amount": 500000,
                    "tenureMonths": 36,
                    "purpose": "PERSONAL"
                  }
                }
                """;

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void validRequest_returns201() throws Exception {
        var req = new CreateLoanApplicationRequest(
                new com.rbih.loan.api.dto.ApplicantDto(
                        "A", 30, new BigDecimal("75000"), EmploymentType.SALARIED, 720),
                new com.rbih.loan.api.dto.LoanDto(new BigDecimal("500000"), 36, LoanPurpose.PERSONAL));

        when(loanApplicationService.createApplication(any(CreateLoanApplicationRequest.class)))
                .thenReturn(new com.rbih.loan.api.dto.LoanApplicationResponse(
                        UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        ApplicationStatus.APPROVED,
                        com.rbih.loan.domain.RiskBand.LOW,
                        new com.rbih.loan.api.dto.OfferDto(
                                new BigDecimal("12.00"), 36, new BigDecimal("1000.00"), new BigDecimal("36000.00")),
                        null));

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}
