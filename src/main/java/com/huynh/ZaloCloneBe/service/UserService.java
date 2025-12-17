package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.dto.request.UserRequest;
import com.huynh.ZaloCloneBe.dto.response.SearchResponse;
import com.huynh.ZaloCloneBe.dto.response.UserResponse;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.mapper.UserMapper;
import com.huynh.ZaloCloneBe.repository.FriendRepository;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class  UserService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper mapper;
    @Autowired
    private FriendRepository friendRepository;
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
    public List<SearchResponse>search(Long userId, String key){
        repository.findById(userId).orElseThrow(()-> new AppException(ErrorCode.USER_NOTFOUND));
        List<User> lst=repository.search(key);

        List<SearchResponse>searchResponseList=new ArrayList<>();
        for(User u:lst){
            boolean isFriend= friendRepository.existsFriend(userId,u.getId());
             Long id=u.getId();
             String firstname=u.getFirstname();
             String phone=u.getPhone();
            String avatarUrl=u.getAvatarUrl();
            String lastname=u.getLastname();
            boolean online=u.isOnline();
             Date createdAt=u.getCreatedAt();
            String role=u.getRole();
            Boolean isfr=isFriend;
            searchResponseList.add(new SearchResponse(id,firstname,phone,avatarUrl,lastname,online,createdAt,role,isfr));
        }

        return searchResponseList;
    }
}
