package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.dto.response.FriendResponse;
import com.huynh.ZaloCloneBe.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api/friends")

public class FriendController {
    @Autowired
    private FriendService friendService;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<List<FriendResponse>> getAllFriends(@RequestHeader("Authorization") String token,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(required = false) String lastName,
                                                           @RequestParam(required = false) Long lastId) throws Exception{
        return ApiResponse.<List<FriendResponse>>builder()
                .status(200)
                .message("Lấy danh sách bạn bè thành công")
                .result(friendService.getAllFriends(token,size,lastName,lastId))
                .build();
    }
    @DeleteMapping("/unfriend")
    public ApiResponse<Void> unfriend(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id) {

        friendService.unFriend(user1Id, user2Id);
        return ApiResponse.<Void>builder()
                .status(200)
                .message("Hủy kết bạn thành công")
                .build();
    }

}

