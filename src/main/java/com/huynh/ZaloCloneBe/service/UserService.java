package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.dto.request.UpdatePassword;
import com.huynh.ZaloCloneBe.dto.request.UpdateRequest;
import com.huynh.ZaloCloneBe.dto.request.UserRequest;
import com.huynh.ZaloCloneBe.dto.response.*;
import com.huynh.ZaloCloneBe.entity.FriendRequest;
import com.huynh.ZaloCloneBe.entity.RelationshipStatus;
import com.huynh.ZaloCloneBe.entity.StatusRequest;
import com.huynh.ZaloCloneBe.entity.User;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.mapper.UserMapper;
import com.huynh.ZaloCloneBe.repository.FriendRepository;
import com.huynh.ZaloCloneBe.repository.FriendRequestRepository;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public  boolean hasSpecialCharacter(String password) {
        if (password == null) return false;
        return password.matches(".*[^a-zA-Z0-9].*");
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
    public UserResponse createUser(UserRequest request){
        if(repository.existsByPhone(request.getPhone())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if(hasSpecialCharacter(request.getPassword())||request.getPassword().length()<6){
            throw new AppException(ErrorCode.PASS_VALID);
        }
        User user=mapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(new Date());
        user.setRole("Customer");
        user.setStatus(true);
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
    @Transactional
    public UserResponse updateAvatar(Long userId, String avatarUrl) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        user.setAvatarUrl(avatarUrl);
        return mapper.toDto(user);
    }
    @Transactional
    public UserResponse updateCover(Long userId, String coverUrl) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        user.setCoverUrl(coverUrl);
        return mapper.toDto(user);
    }
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateRequest request){
        User user=repository.findById(userId).orElseThrow(()->new AppException(ErrorCode.USER_NOTFOUND));
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
        User user=repository.findById(userId).orElseThrow(()->new AppException(ErrorCode.USER_NOTFOUND));
        String oldpass=user.getPassword();
        if(request.getOldPassword()==null){
            throw new AppException(ErrorCode.OLDPASS_NULL);
        }
        if(request.getNewPassword()==null){
            throw new AppException(ErrorCode.NEWPASS_NULL);
        }
        if(!passwordEncoder.matches( request.getOldPassword(),user.getPassword())){
            throw new AppException(ErrorCode.PASS_ERROR);
        }
        if(passwordEncoder.matches( request.getNewPassword(), user.getPassword())){
            throw new AppException(ErrorCode.PASS_DIF);
        }
        if(hasSpecialCharacter(request.getNewPassword())||request.getNewPassword().length()<6){
            throw new AppException(ErrorCode.PASS_VALID);
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
    public UserProfileResponse getUserProfile(Long meId, Long otherId) {

        User user = repository.findById(otherId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        RelationshipStatus status = getRelationshipStatus(meId, otherId);

        return UserProfileResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .avatarUrl(user.getAvatarUrl())
                .coverUrl(user.getCoverUrl())
                .gender(user.getGender())
                .online(user.isOnline())
                .relationshipStatus(status)
                .build();
    }
    @Transactional
    public void lockUser(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        if (!user.getStatus()) {
            throw new AppException(ErrorCode.USER_ALREADY_LOCKED);
        }

        repository.lockUser(userId);
    }
    @Transactional
    public void unlockUser(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

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
                ()->new AppException(ErrorCode.USER_NOTFOUND)
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
