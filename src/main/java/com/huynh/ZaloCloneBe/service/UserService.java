package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.dto.request.UserRequest;
import com.huynh.ZaloCloneBe.dto.response.UserResponse;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.mapper.UserMapper;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper mapper;
    public UserResponse createUser(UserRequest request){
        if(repository.existsByPhone(request.getPhone())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user=mapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(new Date());
        user.setRole("Customer");
        User saved = repository.save(user);
        return mapper.toDto(saved);
    }
    public UserResponse getCurrentUser(String token) throws Exception {

        String jwt = token.substring(7);


        SignedJWT signedJWT = SignedJWT.parse(jwt);
        String phone = signedJWT.getJWTClaimsSet().getSubject();


        User user = repository.findByPhone(phone)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));


        return mapper.toDto(user);
    }
}
