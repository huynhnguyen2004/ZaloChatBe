package com.huynh.ZaloCloneBe.controller;

import com.huynh.ZaloCloneBe.dto.request.SendFriendRequest;
import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import com.huynh.ZaloCloneBe.dto.response.SendFriendResponse;
import com.huynh.ZaloCloneBe.service.FriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/friend")

public class FriendRequestController {
    @Autowired
    private FriendRequestService service;

    @PostMapping("/send")
    public ApiResponse<SendFriendResponse> sendRequest(@RequestBody  SendFriendRequest request) {
        return ApiResponse.<SendFriendResponse>builder()
                .code(1001)
                .messenge("gui loi moi thanh cong")
                .result(service.sendRequest(request))
                .build();
    }


}
