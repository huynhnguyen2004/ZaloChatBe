package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.response.AdminDashBoardResponse;
import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UserService service;
    @GetMapping("/dashboard")
    public ApiResponse<AdminDashBoardResponse>getDashBoard(){
        return ApiResponse.<AdminDashBoardResponse>builder()
                .status(200)
                .message("lay thong ke thanh cong")
                .result(service.getStatistic())
                .build();
    }
}
