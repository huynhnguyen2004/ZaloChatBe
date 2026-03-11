package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.dto.response.FriendResponse;
import com.huynh.ZaloCloneBe.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
                .message("Lấy danh sách bạn bè thành công")
                .result(friendService.getAllFriends(id))
                .build();
    }
    @DeleteMapping("/unfriend")
    public ApiResponse<Void> unfriend(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id) {

        friendService.unFriend(user1Id, user2Id);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Hủy kết bạn thành công")
                .build();
    }

}

