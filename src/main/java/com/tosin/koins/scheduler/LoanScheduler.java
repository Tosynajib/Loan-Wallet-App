package com.tosin.koins.scheduler;

import com.tosin.koins.loan.service.LoanMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled loan tasks.
 *
 * This class only triggers scheduled jobs.
 * Business logic lives in LoanMonitoringService.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoanScheduler {

    private final LoanMonitoringService loanMonitoringService;

    /**
     * Runs every day at 1:00 AM.
     * Marks overdue repayment schedules and defaulted loans.
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void markOverdueLoans() {
        log.info("Starting scheduled job: mark overdue loans");
        loanMonitoringService.markOverdueLoans();
        log.info("Completed scheduled job: mark overdue loans");
    }

    /**
     * Runs every day at 9:00 AM.
     * Sends reminders for repayments due tomorrow.
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendRepaymentReminders() {
        log.info("Starting scheduled job: send repayment reminders");
        loanMonitoringService.sendRepaymentReminders();
        log.info("Completed scheduled job: send repayment reminders");
    }
}