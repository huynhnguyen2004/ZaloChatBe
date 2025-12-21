package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.dto.request.AuthenRequest;
import com.huynh.ZaloCloneBe.dto.response.AuthenResponse;
import com.huynh.ZaloCloneBe.dto.response.UserResponse;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.mapper.UserMapper;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.JwsHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;

    private static final String SECRET = "Bgtov/9iSc1HWhK6xD/VnqXcMbcEiRl/vNxT+nLhTICOzNkeY5qu+8eE6fjv7fDh";


    public UserResponse updataStatus(Long id) throws Exception{
        User user=repository.findById(id).orElseThrow(()->new AppException(ErrorCode.USER_NOTFOUND));
        user.setOnline(false);
        User saved=repository.save(user);
        return userMapper.toDto(saved);
    }
    public AuthenResponse login(AuthenRequest request) throws Exception {
        String phone = request.getPhone();

        User user = repository.findByPhone(phone)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        user.setOnline(true);
        User saved=repository.save(user);
        UserResponse userResponse = userMapper.toDto(saved);
        String token = generateToken(user.getPhone());

        return AuthenResponse.builder()
                .token(token)
                .user(userResponse)
                .build();
    }






    public String generateToken(String phone) throws Exception {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet payload = new JWTClaimsSet.Builder()
                .subject(phone)
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + 3600 * 1000))
                .build();

        SignedJWT signedJWT = new SignedJWT(jwsHeader, payload);
        signedJWT.sign(new MACSigner(SECRET.getBytes()));
        return signedJWT.serialize();
    }
}
