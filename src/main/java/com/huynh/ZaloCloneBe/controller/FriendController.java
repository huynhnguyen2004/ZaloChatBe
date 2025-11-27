package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.dto.response.FriendResponse;
import com.huynh.ZaloCloneBe.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping ("/api/friends")

public class FriendController {
    @Autowired
    private FriendService friendService;

    @GetMapping
    public ApiResponse<List<FriendResponse>> getFriends(@RequestParam Long id) {
        return ApiResponse.<List<FriendResponse>>builder()
                .code(1000)
                .messenge("Lấy danh sách bạn bè thành công")
                .result(friendService.getAllFriends(id))
                .build();
    }
}

