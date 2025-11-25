package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.request.UserRequest;
import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.dto.response.UserResponse;
import com.huynh.ZaloCloneBe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
