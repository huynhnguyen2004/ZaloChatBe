package com.huynh.ZaloCloneBe.controller;


import com.huynh.ZaloCloneBe.dto.response.*;
import com.huynh.ZaloCloneBe.service.FriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/friendrequest")

public class FriendRequestController {
    @Autowired
    private FriendRequestService service;

    @PostMapping("/send")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<SendFriendResponse> sendRequest(@RequestHeader("Authorization") String token,@RequestParam Long userId) throws Exception{
        return ApiResponse.<SendFriendResponse>builder()
                .status(200)
                .message("gui loi moi thanh cong")
                .result(service.sendRequest(token,userId))
                .build();
    }
    @PutMapping("/accept")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<AcceptedFriendResponse> acceptFriend(
            @RequestHeader("Authorization") String token,
            @RequestParam Long userId
    ) throws Exception {
        return ApiResponse.<AcceptedFriendResponse>builder()
                .status(200)
                .message("Chấp nhận lời mời kết bạn")
                .result(service.acceptFriend(token, userId))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<PageResponse<ListSendFriendResponse>>getAllFriendRequest(@RequestHeader("Authorization") String token,
                                                                                @RequestParam(defaultValue = "10") int size,
                                                                                @RequestParam(required = false)Long lastId
    )
    throws Exception{
        return ApiResponse.<PageResponse<ListSendFriendResponse>>builder()
                .status(200)
                .message("Lay danh sach gui loi moi thanh cong")
                .result(service.getAllSendFriend(token,size,lastId))
                .build();
    }
    @PutMapping("/reject")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> rejectRequest(@RequestHeader("Authorization") String token,@RequestParam Long userId) throws Exception {
        service.rejectRequest(token,userId);
        return ApiResponse.<Void>builder()
                .status(200)
                .message("reject thanh cong")
                .build();
    }


    @PutMapping("/cancele")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> canceleRequest(@RequestHeader("Authorization") String token,@RequestParam Long userId) throws Exception{
        service.cancelRequest(token,userId);
        return ApiResponse.<Void>builder()
                .status(200)
                .message("cancele thanh cong")
                .build();

    }


}
