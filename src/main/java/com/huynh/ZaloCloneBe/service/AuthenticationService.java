package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.config.JwtProperties;
import com.huynh.ZaloCloneBe.dto.request.AuthenRequest;
import com.huynh.ZaloCloneBe.dto.response.AuthenResponse;
import com.huynh.ZaloCloneBe.dto.response.ResultLogin;
import com.huynh.ZaloCloneBe.dto.response.UserResponse;
import com.huynh.ZaloCloneBe.entity.RefreshToken;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.mapper.UserMapper;
import com.huynh.ZaloCloneBe.repository.RefreshTokenRepository;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    private final Map<String, Integer> failCount = new ConcurrentHashMap<>();

    public String generateAccessToken(User user) throws Exception {

        JWSHeader header=new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId().toString())
                .claim("role", user.getRole())
                .issueTime(new Date())
                .expirationTime(
                        new Date(System.currentTimeMillis() + jwtProperties.getAccessExpire())
                )
                .build();

        JWSSigner signer = new MACSigner(jwtProperties.getSecret());


        SignedJWT signedJWT = new SignedJWT(
                header,
                claimsSet
        );

        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    public String generateRefreshToken(User user, String refreshTokenId) throws Exception {

        JWSHeader header=new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId().toString())
                .jwtID(refreshTokenId)
                .issueTime(new Date())
                .expirationTime(
                        new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpire())
                )
                .build();


        JWSSigner signer = new MACSigner(jwtProperties.getSecret());


        SignedJWT signedJWT = new SignedJWT(
                header,
                claimsSet
        );

        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    public UserResponse updataStatus(Long id) throws Exception{
        User user=repository.findById(id).orElseThrow(()->new AppException(ErrorCode.USER_NOTFOUND));
        user.setOnline(false);
        user.setLastOnline(new Date());
        User saved=repository.save(user);
        return userMapper.toDto(saved);
    }
    public ResultLogin login(AuthenRequest request) throws Exception {
        int failed = failCount.getOrDefault(request.getPhone(), 0);
        String phone = request.getPhone();

        User user = repository.findByPhone(phone)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        if(!user.getStatus()){
            throw new AppException(ErrorCode.STATUS_LOCK);
        }
        if (failed >= 3) {
            if (request.getCaptchaToken() == null ||
                    !captchaService.verify(request.getCaptchaToken())) {

                throw new AppException(ErrorCode.CAPTCHA_INVALID);
            }
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            failCount.put(request.getPhone(), failed+1);
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        failCount.remove(request.getPhone());
        user.setOnline(true);
        user.setLastOnline(null);
        User saved=repository.save(user);
        UserResponse userResponse = userMapper.toDto(saved);

        refreshTokenRepository.deleteByUserId(saved.getId());


        RefreshToken refreshToken = RefreshToken.builder()
                .user(saved)
                .expiresAt(
                        new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpire())
                )
                .revoked(false)
                .build();

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        String accessToken = generateAccessToken(saved);
        String refreshTokenJwt = generateRefreshToken(saved, savedToken.getId());

        return ResultLogin.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenJwt)
                .userResponse(userResponse)
                .build();
    }



}
