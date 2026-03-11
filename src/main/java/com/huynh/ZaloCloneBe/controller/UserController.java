package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.request.UpdatePassword;
import com.huynh.ZaloCloneBe.dto.request.UpdateRequest;
import com.huynh.ZaloCloneBe.dto.request.UserRequest;
import com.huynh.ZaloCloneBe.dto.response.*;
import com.huynh.ZaloCloneBe.service.FileService;
import com.huynh.ZaloCloneBe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
                .code(1001)
                .message("Lấy thông tin người dùng hiện tại thành công")
                .result(userResponse)
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<List<SearchResponse>> search(
            @RequestParam Long userId,
            @RequestParam String key) {

        return ApiResponse.<List<SearchResponse>>builder()
                .code(1001)
                .message("Tìm kiếm thành công")
                .result(service.search(userId, key))
                .build();
    }

    @PostMapping("/uploads/avatar")
    public ApiResponse<UserResponse> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long userId
    ) throws Exception {

        String avatarUrl = fileService.uploadAvatar(file, userId);


        return ApiResponse.<UserResponse>builder()
                .code(1001)
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
                .code(1001)
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
                .code(1001)
                .message("Sua thong tin thanh cong")
                .result(service.updateProfile(userId, request))
                .build();
    }

    @PutMapping("/editPass")
    public ApiResponse<UpdatePasswordResponse> updateProfile(
            @RequestParam Long userId,
            @RequestBody UpdatePassword request
    ) {
        return ApiResponse.<UpdatePasswordResponse>builder()
                .code(1001)
                .message("Đổi mật khẩu thanh cong")
                .result(service.updatePassWord(userId, request))
                .build();
    }

    @GetMapping("/seen")
    public ApiResponse<UserProfileResponse> viewUserProfile(
            @RequestParam Long meId,
            @RequestParam Long otherId
    ) {
        return ApiResponse.<UserProfileResponse>builder()
                .code(1001)
                .message("Xem trang cá nhân")
                .result(service.getUserProfile(meId, otherId))
                .build();
    }

    @GetMapping("/customer")
    public ApiResponse<PageResponse<UserResponse>> getAllCustomer(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size

    ) {
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .code(1001)
                .message("Lay danh sach khach hang thanh cong")
                .result(service.getCustomer(page, size))
                .build();
    }

    @PutMapping("/lock")
    public ApiResponse<Void> lock(@RequestParam Long userId) {
        service.lockUser(userId);
        return ApiResponse.<Void>builder()
                .code(1001)
                .message("Đã khóa thành công")
                .build();
    }

    @PutMapping("/unlock")
    public ApiResponse<Void> unlock(@RequestParam Long userId) {
        service.unlockUser(userId);
        return ApiResponse.<Void>builder()
                .code(1001)
                .message("Đã kích hoạt thành công")
                .build();
    }

    @GetMapping("/search/customer")
    public ApiResponse<PageResponse<UserResponse>> searchCustomer(@RequestParam String key,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "5") int size) {

        return ApiResponse.<PageResponse<UserResponse>>builder()
                .code(1001)
                .message("tim kiem khach hang thanh cong")
                .result(service.searchCustomer(key, page, size))
                .build();
    }

    @GetMapping("/filter/status")
    public ApiResponse<PageResponse<UserResponse>> filterCustomer(@RequestParam Boolean status,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "5") int size) {
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .code(1001)
                .message("loc khach hang thanh cong")
                .result(service.filterCustomer(status, page, size))
                .build();
    }

    @GetMapping("/detail")
    public ApiResponse<UserResponse> getDetailUser(@RequestParam Long userId) {
        return ApiResponse.<UserResponse>builder()
                .code(1001)
                .message("lay chi tiet khach hang thanh cong")
                .result(service.getUserDetail(userId))
                .build();
    }


}
