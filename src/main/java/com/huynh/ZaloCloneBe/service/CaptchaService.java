package com.huynh.ZaloCloneBe.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CaptchaService {

    @Value("${recaptcha.secret}")
    private String secretKey;

    @Value("${recaptcha.verify-url}")
    private String verifyUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean verify(String token) {
        String url = verifyUrl +
                "?secret=" + secretKey +
                "&response=" + token;

        Map<String, Object> response =
                restTemplate.postForObject(url, null, Map.class);

        return response != null && Boolean.TRUE.equals(response.get("success"));
    }
}
