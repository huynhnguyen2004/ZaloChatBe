package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.request.AcceptedFriendRequest;
import com.huynh.ZaloCloneBe.dto.request.SendFriendRequest;
import com.huynh.ZaloCloneBe.dto.response.AcceptedFriendResponse;
import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.dto.response.ListSendFriendResponse;
import com.huynh.ZaloCloneBe.dto.response.SendFriendResponse;
import com.huynh.ZaloCloneBe.service.FriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friendrequest")

public class FriendRequestController {
    @Autowired
    private FriendRequestService service;

    @PostMapping("/send")
    public ApiResponse<SendFriendResponse> sendRequest(@RequestBody  SendFriendRequest request) {
        return ApiResponse.<SendFriendResponse>builder()
                .status(200)
                .message("gui loi moi thanh cong")
                .result(service.sendRequest(request))
                .build();
    }
    @PutMapping("/accept")
    public ApiResponse<AcceptedFriendResponse> acceptFriend(
            @RequestParam Long meId,
            @RequestParam Long otherId
    ) {
        return ApiResponse.<AcceptedFriendResponse>builder()
                .status(200)
                .message("Chấp nhận lời mời kết bạn")
                .result(service.acceptFriend(meId, otherId))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ListSendFriendResponse>>getAllFriendRequest(@RequestParam("id") Long id){
        return ApiResponse.<List<ListSendFriendResponse>>builder()
                .status(200)
                .message("Lay danh sach gui loi moi thanh cong")
                .result(service.getAllSendFriend(id))
                .build();
    }
    @PutMapping("/reject")
    public ApiResponse<Void> rejectRequest(@RequestParam Long meId,@RequestParam Long userId) {
        service.rejectRequest(meId,userId);
        return ApiResponse.<Void>builder()
                .status(200)
                .message("reject thanh cong")
                .build();
    }


    @PutMapping("/cancele")
    public ApiResponse<Void> canceleRequest(@RequestParam Long meId,@RequestParam Long userId) {
        service.cancelRequest(meId,userId);
        return ApiResponse.<Void>builder()
                .status(200)
                .message("reject thanh cong")
                .build();

    }


}
