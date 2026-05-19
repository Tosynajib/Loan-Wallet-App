package com.tosin.koins.loan.service;

import com.tosin.koins.loan.dto.ApplyLoanRequest;
import com.tosin.koins.loan.dto.LoanActionResponse;
import com.tosin.koins.loan.dto.LoanResponse;
import com.tosin.koins.loan.dto.RepayLoanRequest;

import java.util.List;
import java.util.UUID;

public interface LoanService {

    LoanResponse applyForLoan(ApplyLoanRequest request);

    List<LoanResponse> getMyLoans();

    LoanResponse getMyLoanById(UUID loanId);

    List<LoanResponse> getAllLoans();

    LoanActionResponse approveLoan(UUID loanId);

    LoanActionResponse disburseLoan(UUID loanId);

    LoanActionResponse repayLoan(UUID loanId, RepayLoanRequest request);
}