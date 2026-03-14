package com.huynh.ZaloCloneBe.service;

import com.vonage.client.VonageClient;
import com.vonage.client.sms.messages.TextMessage;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SmsService {

    @Value("${vonage.apiKey}")
    private String apiKey;

    @Value("${vonage.apiSecret}")
    private String apiSecret;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void sendSms(String phone, String message) throws Exception {

        VonageClient client = VonageClient.builder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .build();

        TextMessage textMessage = new TextMessage(
                "ZaloClone",
                phone,
                message
        );

        client.getSmsClient().submitMessage(textMessage);

    }

    public String formatPhone(String phone){

        phone = phone.trim();

        if(phone.startsWith("0")){
            phone = "84" + phone.substring(1);
        }

        if(!phone.startsWith("84")){
            throw new AppException(ErrorCode.IVALID_PHONE);
        }

        return phone;
    }

    public void sendOtp(String phone) throws Exception {

        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        String formatPhone = formatPhone(phone);

        String key = "otp:" + formatPhone;

        redisTemplate.opsForValue().set(
                key,
                otp,
                Duration.ofMinutes(3)
        );

        sendSms(formatPhone, "Your OTP: " + otp);
    }
}