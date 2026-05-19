package com.tosin.koins.integration.sms;

public interface SmsProvider {

    void sendSms(String phoneNumber, String message);
}
