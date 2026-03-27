package com.huynh.ZaloCloneBe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huynh.ZaloCloneBe.config.JwtProperties;
import com.huynh.ZaloCloneBe.dto.request.UpdatePassword;
import com.huynh.ZaloCloneBe.dto.request.UpdateRequest;

import com.huynh.ZaloCloneBe.dto.response.*;

import com.huynh.ZaloCloneBe.entity.RelationshipStatus;
import com.huynh.ZaloCloneBe.entity.StatusRequest;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.mapper.UserMapper;
import com.huynh.ZaloCloneBe.repository.FriendRepository;
import com.huynh.ZaloCloneBe.repository.FriendRequestRepository;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import com.huynh.ZaloCloneBe.until.PhoneUntil;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

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
    @Autowired
    private FileService fileService;
    @Autowired
    private FriendRequestRepository friendRequestRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private JwtProperties jwtProperties;
    private ObjectMapper objectMapper=new ObjectMapper();


    public UserResponse getCurrentUser(String token) throws Exception {

        String jwt = token.substring(7);


        SignedJWT signedJWT = SignedJWT.parse(jwt);
        if(!signedJWT.verify(new MACVerifier(jwtProperties.getSecret()))){
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        Date expiry = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expiry.before(new Date())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }

        Long userId = Long.parseLong(signedJWT.getJWTClaimsSet().getSubject());



        User user = repository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UserResponse userResponse=mapper.toDto(user);

        return userResponse;
    }
    public PageResponse<UserResponse>getCustomer(int page,int size){
        Pageable pageable= PageRequest.of(page,size, Sort.by("lastname").ascending());
        Page<User>userPage=repository.findAllCustomer(pageable);
        List<UserResponse>responseList=new ArrayList<>();
        for(User user:userPage.getContent()){
            responseList.add(mapper.toDto(user));
        }
        return PageResponse.<UserResponse>builder()
                .content(responseList)
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .build();

    }
    public List<SearchResponse> search(String token, String key) throws Exception{
        if(token==null||token.isBlank()){
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        String jwt=token.substring(7);
        SignedJWT signedJwt=SignedJWT.parse(jwt);
        if(!signedJwt.verify(new MACVerifier(jwtProperties.getSecret()))){
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        Date expiry = signedJwt.getJWTClaimsSet().getExpirationTime();
        if (expiry.before(new Date())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
        Long userId=Long.parseLong(signedJwt.getJWTClaimsSet().getSubject());
        String phone= PhoneUntil.formatPhone(key);
        Optional<User> optionalUser = repository.findByPhone(phone);

        if (optionalUser.isEmpty()) {
            return List.of();
        }
        User u = optionalUser.get();

        if (u.getId().equals(userId)) {
            return List.of();
        }
        boolean isFriend= friendRepository.existsFriend(userId,u.getId());

        return List.of(
                SearchResponse.builder()
                .id(u.getId())
                .firstname(u.getFirstname())
                .lastname(u.getLastname())
                .avatarUrl(u.getAvatarUrl())
                .phone(u.getPhone())
                .online(u.isOnline())
                .isFriend(isFriend)
                .build()
                );
    }
    @Transactional
    public UserResponse updateAvatar(Long userId, String avatarUrl) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setAvatarUrl(avatarUrl);
        return mapper.toDto(user);
    }
    @Transactional
    public UserResponse updateCover(Long userId, String coverUrl) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setCoverUrl(coverUrl);
        return mapper.toDto(user);
    }
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateRequest request){
        User user=repository.findById(userId).orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        if(request.getFirstname()!=null){
            user.setFirstname(request.getFirstname());
        }
        if(request.getLastname()!=null){
            user.setLastname(request.getLastname());
        }
        if(request.getBirthday()!=null){
            user.setBirthday(request.getBirthday());
        }
        if(request.getGender()!=null){
            user.setGender(request.getGender());
        }
        repository.save(user);
        return mapper.toDto(user);
    }
    @Transactional
    public UpdatePasswordResponse updatePassWord(Long userId, UpdatePassword request){
        User user=repository.findById(userId).orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        String oldpass=user.getPassword();


        if(!passwordEncoder.matches( request.getOldPassword(),user.getPassword())){
            throw new AppException(ErrorCode.PASSWORD_WRONG);
        }
        if(passwordEncoder.matches( request.getNewPassword(), user.getPassword())){
            throw new AppException(ErrorCode.PASSWORD_SAME_AS_OLD);
        }

        String hashpass=passwordEncoder.encode(request.getNewPassword());
        user.setPassword(hashpass);

        UpdatePasswordResponse response=mapper.toDtoPass(user);
        response.setOldPass(oldpass);
        repository.save(user);
        return response;


    }
    public RelationshipStatus getRelationshipStatus(Long meId, Long otherId) {

        if (friendRepository.existsFriend(meId, otherId)) {
            return RelationshipStatus.FRIEND;
        }

        if (friendRequestRepository
                .existsBySenderIdAndReceiverIdAndStatus(
                        meId, otherId, StatusRequest.PENDING)) {
            return RelationshipStatus.SENT_REQUEST;
        }

        if (friendRequestRepository
                .existsBySenderIdAndReceiverIdAndStatus(
                        otherId, meId, StatusRequest.PENDING)) {
            return RelationshipStatus.RECEIVED_REQUEST;
        }

        return RelationshipStatus.NONE;
    }
    public UserProfileResponse getUserProfile(Long meId, Long otherId) throws Exception {

        String key = "user:profile:" + otherId;

        String cacheUser = redisTemplate.opsForValue().get(key);

        UserProfileResponse profile;

        if (cacheUser != null) {
            profile = objectMapper.readValue(cacheUser, UserProfileResponse.class);

        } else {

            User user = repository.findById(otherId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            profile = UserProfileResponse.builder()
                    .id(user.getId())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .avatarUrl(user.getAvatarUrl())
                    .coverUrl(user.getCoverUrl())
                    .gender(user.getGender())
                    .online(user.isOnline())
                    .build();

            redisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(profile),
                    java.time.Duration.ofMinutes(30)
            );
        }

        RelationshipStatus status = getRelationshipStatus(meId, otherId);

        profile.setRelationshipStatus(status);

        return profile;
    }
    @Transactional
    public void lockUser(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!user.getStatus()) {
            throw new AppException(ErrorCode.USER_ALREADY_LOCKED);
        }

        repository.lockUser(userId);
    }
    @Transactional
    public void unlockUser(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus()) {
            throw new AppException(ErrorCode.USER_ALREADY_ACTIVE);
        }

        repository.unlockUser(userId);
    }
    public PageResponse<UserResponse>searchCustomer(String key,int page,int size){
        Pageable pageable=PageRequest.of(page,size,Sort.by("createdAt").descending());
        Page<User>list=repository.searchCustomer(key,pageable);
        List<UserResponse>responseList=new ArrayList<>();
        for(User user:list.getContent()){
            responseList.add(mapper.toDto(user));
        }
        return PageResponse.<UserResponse>builder()
                .content(responseList)
                .page(list.getNumber())
                .size(list.getSize())
                .totalPages(list.getTotalPages())
                .totalElements(list.getTotalElements())
                .first(list.isFirst())
                .last(list.isLast())
                .build();


    }
    public PageResponse<UserResponse>filterCustomer(Boolean status,int page,int size){
        Pageable pageable=PageRequest.of(page,size,Sort.by("lastname").ascending());
        Page<User>list=repository.filterCustomer(status,pageable);
        List<UserResponse>responseList=new ArrayList<>();
        for(User user:list.getContent()){
            responseList.add(mapper.toDto(user));
        }
        return PageResponse.<UserResponse>builder()
                .content(responseList)
                .page(list.getNumber())
                .size(list.getSize())
                .totalPages(list.getTotalPages())
                .totalElements(list.getTotalElements())
                .first(list.isFirst())
                .last(list.isLast())
                .build();

    }
    public UserResponse getUserDetail(Long userId){
        User user=repository.findById(userId).orElseThrow(
                ()->new AppException(ErrorCode.USER_NOT_FOUND)
        );
        return mapper.toDto(user);
    }
    public AdminDashBoardResponse getStatistic() {


        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date todayStart = cal.getTime();

        Calendar endCal = (Calendar) cal.clone();
        endCal.add(Calendar.DAY_OF_MONTH, 1);
        Date todayEnd = endCal.getTime();

        return AdminDashBoardResponse.builder()
                .totalUsers(repository.countByRole("Customer"))
                .activeUsers(repository.countByRoleAndStatusTrue("Customer"))
                .lockedUsers(repository.countByRoleAndStatusFalse("Customer"))
                .onlineUsers(repository.countByRoleAndOnlineTrue("Customer"))
                .newUsers(repository.countNewUsers(todayStart, todayEnd))
                .dailyGrowth(repository.dailyGrowth())
                .monthlyGrowth(repository.monthlyGrowth())
                .build();
    }






}
