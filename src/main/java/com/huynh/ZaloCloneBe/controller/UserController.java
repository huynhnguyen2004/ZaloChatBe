package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.request.UpdatePassword;
import com.huynh.ZaloCloneBe.dto.request.UpdateRequest;
import com.huynh.ZaloCloneBe.dto.request.UserRequest;
import com.huynh.ZaloCloneBe.dto.response.*;
import com.huynh.ZaloCloneBe.service.FileService;
import com.huynh.ZaloCloneBe.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService service;
    @Autowired
    private FileService fileService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser(@RequestHeader("Authorization") String token) throws Exception {
        UserResponse userResponse = service.getCurrentUser(token);

        return ApiResponse.<UserResponse>builder()
                .status(200)
                .message("Lấy thông tin người dùng hiện tại thành công")
                .result(userResponse)
                .build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<SearchResponse>> search(
            @RequestHeader("Authorization") String token,
            @RequestParam String key) throws Exception {

        return ApiResponse.<List<SearchResponse>>builder()
                .status(200)
                .message("Tìm kiếm thành công")
                .result(service.search(token, key))
                .build();
    }

    @PostMapping("/uploads/avatar")
    public ApiResponse<UserResponse> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long userId
    ) throws Exception {

        String avatarUrl = fileService.uploadAvatar(file, userId);


        return ApiResponse.<UserResponse>builder()
                .status(200)
                .message("thêm ảnh đại diện thành công")
                .result(service.updateAvatar(userId, avatarUrl))
                .build();

    }

    @PostMapping("/uploads/cover")
    public ApiResponse<UserResponse> uploadCover(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long userId
    ) throws Exception {

        String coverUrl = fileService.uploadCover(file, userId);


        return ApiResponse.<UserResponse>builder()
                .status(200)
                .message("thêm ảnh bìa thành công")
                .result(service.updateCover(userId, coverUrl))
                .build();

    }

    @PutMapping("/editInfor")
    public ApiResponse<UserResponse> updateProfile(
            @RequestParam Long userId,
            @RequestBody UpdateRequest request
    ) {
        return ApiResponse.<UserResponse>builder()
                .status(200)
                .message("Sua thong tin thanh cong")
                .result(service.updateProfile(userId, request))
                .build();
    }

    @PutMapping("/editPass")
    public ApiResponse<UpdatePasswordResponse> updateProfile(
            @RequestParam Long userId,
           @Valid @RequestBody UpdatePassword request
    ) {
        return ApiResponse.<UpdatePasswordResponse>builder()
                .status(200)
                .message("Đổi mật khẩu thanh cong")
                .result(service.updatePassWord(userId, request))
                .build();
    }

    @GetMapping("/seen")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<UserProfileResponse> viewUserProfile (
            @RequestHeader("Authorization") String token,
            @RequestParam Long otherId
    )throws Exception {
        return ApiResponse.<UserProfileResponse>builder()
                .status(200)
                .message("Xem trang cá nhân")
                .result(service.getUserProfile(token, otherId))
                .build();
    }

    @GetMapping("/customer")
    public ApiResponse<PageResponse<UserResponse>> getAllCustomer(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size

    ) {
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .status(200)
                .message("Lay danh sach khach hang thanh cong")
                .result(service.getCustomer(page, size))
                .build();
    }

    @PutMapping("/lock")
    public ApiResponse<Void> lock(@RequestParam Long userId) {
        service.lockUser(userId);
        return ApiResponse.<Void>builder()
                .status(200)
                .message("Đã khóa thành công")
                .build();
    }

    @PutMapping("/unlock")
    public ApiResponse<Void> unlock(@RequestParam Long userId) {
        service.unlockUser(userId);
        return ApiResponse.<Void>builder()
                .status(200)
                .message("Đã kích hoạt thành công")
                .build();
    }

    @GetMapping("/search/customer")
    public ApiResponse<PageResponse<UserResponse>> searchCustomer(@RequestParam String key,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "5") int size) {

        return ApiResponse.<PageResponse<UserResponse>>builder()
                .status(200)
                .message("tim kiem khach hang thanh cong")
                .result(service.searchCustomer(key, page, size))
                .build();
    }

    @GetMapping("/filter/status")
    public ApiResponse<PageResponse<UserResponse>> filterCustomer(@RequestParam Boolean status,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "5") int size) {
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .status(200)
                .message("loc khach hang thanh cong")
                .result(service.filterCustomer(status, page, size))
                .build();
    }

    @GetMapping("/detail")
    public ApiResponse<UserResponse> getDetailUser(@RequestParam Long userId) {
        return ApiResponse.<UserResponse>builder()
                .status(200)
                .message("lay chi tiet khach hang thanh cong")
                .result(service.getUserDetail(userId))
                .build();
    }


}
