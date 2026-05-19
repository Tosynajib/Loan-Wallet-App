package com.tosin.koins.loan.facade;

import com.tosin.koins.common.response.ApiResponse;
import com.tosin.koins.common.response.ApplicationResponseFactory;
import com.tosin.koins.loan.dto.ApplyLoanRequest;
import com.tosin.koins.loan.dto.LoanActionResponse;
import com.tosin.koins.loan.dto.LoanResponse;
import com.tosin.koins.loan.dto.RepayLoanRequest;
import com.tosin.koins.loan.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LoanFacadeImpl implements LoanFacade {

    private final LoanService loanService;
    private final ApplicationResponseFactory responseFactory;

    @Override
    public ApiResponse<LoanResponse> applyForLoan(ApplyLoanRequest request) {
        return responseFactory.success("Loan application submitted successfully", loanService.applyForLoan(request));
    }

    @Override
    public ApiResponse<List<LoanResponse>> getMyLoans() {
        return responseFactory.success("Loans retrieved successfully", loanService.getMyLoans());
    }

    @Override
    public ApiResponse<LoanResponse> getMyLoanById(UUID loanId) {
        return responseFactory.success("Loan retrieved successfully", loanService.getMyLoanById(loanId));
    }

    @Override
    public ApiResponse<List<LoanResponse>> getAllLoans() {
        return responseFactory.success("All loans retrieved successfully", loanService.getAllLoans());
    }

    @Override
    public ApiResponse<LoanActionResponse> approveLoan(UUID loanId) {
        return responseFactory.success("Loan approved successfully", loanService.approveLoan(loanId));
    }

    @Override
    public ApiResponse<LoanActionResponse> disburseLoan(UUID loanId) {
        return responseFactory.success("Loan disbursed successfully", loanService.disburseLoan(loanId));
    }

    @Override
    public ApiResponse<LoanActionResponse> repayLoan(UUID loanId, RepayLoanRequest request) {
        return responseFactory.success("Loan repayment successful", loanService.repayLoan(loanId, request));
    }
}