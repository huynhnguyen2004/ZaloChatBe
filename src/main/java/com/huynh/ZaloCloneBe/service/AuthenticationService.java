package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.config.JwtProperties;
import com.huynh.ZaloCloneBe.dto.request.AuthenRequest;
import com.huynh.ZaloCloneBe.dto.request.ResetPassWordRequest;
import com.huynh.ZaloCloneBe.dto.request.RegisterRequest;
import com.huynh.ZaloCloneBe.dto.response.ResultLogin;
import com.huynh.ZaloCloneBe.dto.response.UserResponse;
import com.huynh.ZaloCloneBe.entity.OtpPurpose;
import com.huynh.ZaloCloneBe.entity.RefreshToken;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.mapper.UserMapper;
import com.huynh.ZaloCloneBe.repository.RefreshTokenRepository;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import com.huynh.ZaloCloneBe.until.PhoneUntil;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private UserMapper mapper;
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
    @Autowired
    private StringRedisTemplate redisTemplate;

    public String generateAccessToken(User user,Boolean isRememberMe) throws Exception {

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId().toString())
                .claim("role", user.getRole())
                .claim("rememberMe",isRememberMe)
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

    public String generateRefreshToken(User user,long refreshExpire,Boolean isRememberMe) throws Exception {

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId().toString())
                .claim("rememberMe",isRememberMe)
                .issueTime(new Date())
                .expirationTime(
                        new Date(System.currentTimeMillis() + refreshExpire)
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

    @Transactional
    public void logout(String refreshToken) throws Exception {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken).orElseThrow(
                () -> new AppException(ErrorCode.TOKEN_NOT_FOUND)
        );


        Long userId = tokenEntity.getUser().getId();
        User user = repository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setOnline(false);
        user.setLastOnline(new Date());
        refreshTokenRepository.revokeByToken(refreshToken);
        repository.save(user);
        redisTemplate.delete("user:profile:"+userId);

    }

    public ResultLogin login(AuthenRequest request) throws Exception {
        String key = "login_fail:" + request.getPhone();
        String failStr = redisTemplate.opsForValue().get(key);
        int failed = failStr == null ? 0 : Integer.parseInt(failStr);
        if (failed >= 3) {
            if (request.getCaptchaToken() == null) {
                throw new AppException(ErrorCode.CAPTCHA_REQUIRED);
            }
            if (!captchaService.verify(request.getCaptchaToken())) {

                throw new AppException(ErrorCode.CAPTCHA_INVALID);
            }

        }
        User user = repository.findByPhone(PhoneUntil.formatPhone(request.getPhone()))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!user.getStatus()) {
            throw new AppException(ErrorCode.USER_ALREADY_LOCKED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            Long count = redisTemplate.opsForValue().increment(key);
            if (count == 1) {
                redisTemplate.expire(key, 5, TimeUnit.MINUTES);
            }
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        redisTemplate.delete(key);
        user.setOnline(true);
        user.setLastOnline(null);
        User saved = repository.save(user);
        long refreshExpire = request.getIsRememberMe()
                ? 30L * 24 * 60 * 60 * 1000
                : jwtProperties.getRefreshExpire();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(saved)
                .expiresAt(
                        new Date(System.currentTimeMillis() + refreshExpire)
                )
                .revoked(false)
                .build();


        String accessToken = generateAccessToken(saved,request.getIsRememberMe());
        String refreshTokenJwt = generateRefreshToken(saved,refreshExpire,request.getIsRememberMe());
        refreshToken.setToken(refreshTokenJwt);
        refreshTokenRepository.save(refreshToken);

        return ResultLogin.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenJwt)
                .refreshExpire(refreshExpire)
                .build();
    }

    @Transactional
    public ResultLogin refresh(String refreshToken) throws Exception {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        SignedJWT jwt = SignedJWT.parse(refreshToken);

        if (!jwt.verify(new MACVerifier(jwtProperties.getSecret()))) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        Date expiry = jwt.getJWTClaimsSet().getExpirationTime();

        if (expiry.before(new Date())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_NOT_FOUND));

        if (refreshTokenEntity.getRevoked()) {
            throw new AppException(ErrorCode.TOKEN_REVOKED);
        }
        User user = refreshTokenEntity.getUser();

        refreshTokenEntity.setRevoked(true);
        refreshTokenRepository.save(refreshTokenEntity);

        Boolean rememberMe = (Boolean) jwt.getJWTClaimsSet().getClaim("rememberMe");
        if(rememberMe==null){
            rememberMe=false;
        }
        long refreshExpire = rememberMe
                ? 30L * 24 * 60 * 60 * 1000
                : jwtProperties.getRefreshExpire();
        String newAccess = generateAccessToken(user,rememberMe);

        String newRefresh = generateRefreshToken(user,refreshExpire,rememberMe);

        refreshTokenEntity.setToken(newRefresh);
        refreshTokenEntity.setExpiresAt(
                new Date(System.currentTimeMillis() + refreshExpire)
        );
        refreshTokenEntity.setRevoked(false);

        refreshTokenRepository.save(refreshTokenEntity);

        return ResultLogin.builder()
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .refreshExpire(refreshExpire)
                .build();
    }



    public UserResponse register(RegisterRequest request) {

        String key="verify:"+ OtpPurpose.REGISTER.name()+":"+request.getVerifyTokenOtp();

        String phone=redisTemplate.opsForValue().get(key);

        if(phone==null){
            throw new AppException(ErrorCode.OTP_NOT_FOUND);
        }
        if(repository.existsByPhone(phone)){
            throw new AppException(ErrorCode.USER_EXISTED);
        }


        User user = User.builder()
                .phone(phone)
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .birthday(request.getBirthday())
                .gender(request.getGender())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(new Date())
                .role("Customer")
                .status(true)
                .build();

        User saved = repository.save(user);

        redisTemplate.delete(key);
        return mapper.toDto(saved);
    }
    public void resetPassword(ResetPassWordRequest request){
        String key="verify:"+ OtpPurpose.RESET_PASSWORD.name()+":"+request.getVerifyTokenOtp();

        String phone=redisTemplate.opsForValue().get(key);

        if(phone==null){
            throw new AppException(ErrorCode.OTP_NOT_FOUND);
        }

        User user=repository.findByPhone(phone).orElseThrow(
                ()-> new AppException(ErrorCode.USER_NOT_FOUND)
        );


        user.setPassword(passwordEncoder.encode(request.getPassword()));

        repository.save(user);

        redisTemplate.delete(key);
    }


}
