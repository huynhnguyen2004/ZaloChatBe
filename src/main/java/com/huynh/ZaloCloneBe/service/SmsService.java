package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.dto.response.VerifyTokenResponse;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import com.huynh.ZaloCloneBe.until.OtpUntil;
import com.huynh.ZaloCloneBe.until.PhoneUntil;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.messages.TextMessage;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class SmsService {

    @Value("${vonage.apiKey}")
    private String apiKey;

    @Value("${vonage.apiSecret}")
    private String apiSecret;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserRepository userRepository;
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


    public void sendOtp(String phone) throws Exception {

        String formatPhone = PhoneUntil.formatPhone(phone);

        if(userRepository.existsByPhone(formatPhone)){
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        String cooldownKey="cooldown:"+formatPhone;

        Boolean exist=redisTemplate.hasKey(cooldownKey);

        if(Boolean.TRUE.equals(exist)){
            throw new AppException(ErrorCode.OTP_COOLDOWN);
        }

        String otp = OtpUntil.generateOtp();

        String key = "otp:" + formatPhone;

        redisTemplate.opsForValue().set(
                key,
                otp,
                Duration.ofMinutes(2)
        );
        redisTemplate.opsForValue().set(
                cooldownKey,
                "1",
                Duration.ofSeconds(50)
        );

//        sendSms(formatPhone, "Your OTP: " + otp);
        System.out.print("Your OTP: " + otp);
    }
    public VerifyTokenResponse verifyOtp(String phone, String inputOtp) {

        String formatPhone = PhoneUntil.formatPhone(phone);

        String key = "otp:" + formatPhone;

        String attemptKey = "attempt:" + formatPhone;

        String otp = redisTemplate.opsForValue().get(key);

        if (otp == null) {
            throw new AppException(ErrorCode.otp_exprired);
        }

        if (!inputOtp.equals(otp)) {

            Long attempt = redisTemplate.opsForValue().increment(attemptKey);
            redisTemplate.expire(attemptKey, Duration.ofMinutes(5));

            if (attempt >= 5) {
                throw new AppException(ErrorCode.OTP_BLOCKED);
            }
            throw new AppException(ErrorCode.OTP_WRONG);

        }
        String verifyToken= UUID.randomUUID().toString();
        redisTemplate.delete(key);
        redisTemplate.delete(attemptKey);


        redisTemplate.opsForValue().set(
                "verify:"+verifyToken,
                formatPhone,
                Duration.ofMinutes(5)
        );
        return new VerifyTokenResponse(verifyToken);
    }
}