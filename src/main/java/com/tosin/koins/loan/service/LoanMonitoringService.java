package com.tosin.koins.loan.service;

public interface LoanMonitoringService {

    void markOverdueLoans();

    void sendRepaymentReminders();
}