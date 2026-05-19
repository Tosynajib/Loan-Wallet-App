package com.tosin.koins.loan.facade;

import com.tosin.koins.common.response.ApiResponse;
import com.tosin.koins.loan.dto.ApplyLoanRequest;
import com.tosin.koins.loan.dto.LoanActionResponse;
import com.tosin.koins.loan.dto.LoanResponse;
import com.tosin.koins.loan.dto.RepayLoanRequest;

import java.util.List;
import java.util.UUID;

public interface LoanFacade {

    ApiResponse<LoanResponse> applyForLoan(ApplyLoanRequest request);

    ApiResponse<List<LoanResponse>> getMyLoans();

    ApiResponse<LoanResponse> getMyLoanById(UUID loanId);

    ApiResponse<List<LoanResponse>> getAllLoans();

    ApiResponse<LoanActionResponse> approveLoan(UUID loanId);

    ApiResponse<LoanActionResponse> disburseLoan(UUID loanId);

    ApiResponse<LoanActionResponse> repayLoan(UUID loanId, RepayLoanRequest request);
}