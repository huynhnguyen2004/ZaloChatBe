package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.request.UserRequest;
import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.dto.response.SearchResponse;
import com.huynh.ZaloCloneBe.dto.response.UserResponse;
import com.huynh.ZaloCloneBe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
   @Autowired
    private UserService service;
   @PostMapping("/register")
   public ApiResponse<UserResponse>createUser(@RequestBody UserRequest request){
       return ApiResponse.<UserResponse>builder()
               .code(1001)
               .messenge("Tạo user thành công")
               .result(service.createUser(request))
               .build();
   }
    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser(@RequestHeader("Authorization") String token) throws Exception {
        UserResponse userResponse = service.getCurrentUser(token);

        return ApiResponse.<UserResponse>builder()
                .code(1001)
                .messenge("Lấy thông tin người dùng hiện tại thành công")
                .result(userResponse)
                .build();
    }
    @GetMapping("/search")
    public ApiResponse<List<SearchResponse>> search(
            @RequestParam Long userId,
            @RequestParam String key) {

        return ApiResponse.<List<SearchResponse>>builder()
                .code(1001)
                .messenge("Tìm kiếm thành công")
                .result(service.search(userId, key))
                .build();
    }


}
