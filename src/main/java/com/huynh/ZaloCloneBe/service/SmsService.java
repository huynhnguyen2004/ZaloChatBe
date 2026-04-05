package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.dto.request.SendOtpRequest;
import com.huynh.ZaloCloneBe.dto.request.VerifyOtpRequest;
import com.huynh.ZaloCloneBe.dto.response.VerifyTokenResponse;
import com.huynh.ZaloCloneBe.entity.OtpPurpose;
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



    public void sendOtp(SendOtpRequest request) throws Exception {

        String formatPhone = PhoneUntil.formatPhone(request.getPhone());

        String otp = OtpUntil.generateOtp();

        OtpPurpose purpose = request.getOtpPurpose();

        String otpKey = "otp:" + purpose.name() + ":" + formatPhone;

        String cooldownKey = "cooldown:" + purpose.name() + ":" + formatPhone;

        switch (purpose) {
            case REGISTER:
                if (userRepository.existsByPhone(formatPhone)) {
                    throw new AppException(ErrorCode.USER_EXISTED);
                }
                break;

            case RESET_PASSWORD:
                if (!userRepository.existsByPhone(formatPhone)) {
                    throw new AppException(ErrorCode.USER_NOT_FOUND);
                }
                break;
        }

        if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            throw new AppException(ErrorCode.OTP_COOLDOWN);
        }

        redisTemplate.opsForValue().set(
                otpKey,
                otp,
                Duration.ofMinutes(2)
        );

        redisTemplate.opsForValue().set(
                cooldownKey,
                "1",
                Duration.ofSeconds(50)
        );

//         sendSms(formatPhone, "Your OTP: " + otp);

        System.out.println("Your OTP: " + otp);
    }

    public VerifyTokenResponse verifyOtp(VerifyOtpRequest request) {

        String formatPhone = PhoneUntil.formatPhone(request.getPhone());

        String purpose=request.getOtpPurpose().name();
        String key = "otp:"+purpose+":"+ formatPhone;

        String attemptKey = "attempt:"+purpose+":" + formatPhone;

        String otp = redisTemplate.opsForValue().get(key);

        if (otp == null) {
            redisTemplate.delete(attemptKey);
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }

        if (!request.getOtp().equals(otp)) {

            Long attempt = redisTemplate.opsForValue().increment(attemptKey);
            redisTemplate.expire(attemptKey, Duration.ofMinutes(5));

            if (attempt >= 5) {
                throw new AppException(ErrorCode.OTP_BLOCKED);
            }
            throw new AppException(ErrorCode.OTP_WRONG);

        }
        String verifyToken = UUID.randomUUID().toString();

        redisTemplate.delete(key);

        redisTemplate.delete(attemptKey);

        redisTemplate.opsForValue().set(
                "verify:"+purpose+":" + verifyToken,
                formatPhone,
                Duration.ofMinutes(5)
        );
        return new VerifyTokenResponse(verifyToken);
    }
}