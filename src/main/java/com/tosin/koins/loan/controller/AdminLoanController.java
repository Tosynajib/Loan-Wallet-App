package com.tosin.koins.loan.controller;

import com.tosin.koins.common.response.ApiResponse;
import com.tosin.koins.loan.dto.LoanActionResponse;
import com.tosin.koins.loan.dto.LoanResponse;
import com.tosin.koins.loan.facade.LoanFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/loans")
@RequiredArgsConstructor
@Tag(name = "Admin Loans", description = "Admin loan approval and disbursement APIs")
public class AdminLoanController {

    private final LoanFacade loanFacade;

    @GetMapping
    @Operation(summary = "Admin: list all loans")
    public ApiResponse<List<LoanResponse>> getAllLoans() {
        return loanFacade.getAllLoans();
    }

    @PatchMapping("/{loanId}/approve")
    @Operation(summary = "Admin: approve loan")
    public ApiResponse<LoanActionResponse> approveLoan(@PathVariable UUID loanId) {
        return loanFacade.approveLoan(loanId);
    }

    @PatchMapping("/{loanId}/disburse")
    @Operation(summary = "Admin: disburse approved loan")
    public ApiResponse<LoanActionResponse> disburseLoan(@PathVariable UUID loanId) {
        return loanFacade.disburseLoan(loanId);
    }
}