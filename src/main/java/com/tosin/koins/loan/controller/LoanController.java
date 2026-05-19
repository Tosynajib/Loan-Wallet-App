package com.tosin.koins.loan.controller;

import com.tosin.koins.common.response.ApiResponse;
import com.tosin.koins.loan.dto.ApplyLoanRequest;
import com.tosin.koins.loan.dto.LoanActionResponse;
import com.tosin.koins.loan.dto.LoanResponse;
import com.tosin.koins.loan.dto.RepayLoanRequest;
import com.tosin.koins.loan.facade.LoanFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Loan application, status and repayment APIs")
public class LoanController {

    private final LoanFacade loanFacade;

    @PostMapping("/apply")
    @Operation(summary = "Apply for a loan")
    public ApiResponse<LoanResponse> applyForLoan(@Valid @RequestBody ApplyLoanRequest request) {
        return loanFacade.applyForLoan(request);
    }

    @GetMapping("/me")
    @Operation(summary = "Get authenticated user's loans")
    public ApiResponse<List<LoanResponse>> getMyLoans() {
        return loanFacade.getMyLoans();
    }

    @GetMapping("/{loanId}")
    @Operation(summary = "Get authenticated user's loan details")
    public ApiResponse<LoanResponse> getMyLoanById(@PathVariable UUID loanId) {
        return loanFacade.getMyLoanById(loanId);
    }

    @PostMapping("/{loanId}/repay")
    @Operation(summary = "Repay a disbursed loan")
    public ApiResponse<LoanActionResponse> repayLoan(
            @PathVariable UUID loanId,
            @Valid @RequestBody RepayLoanRequest request
    ) {
        return loanFacade.repayLoan(loanId, request);
    }
}