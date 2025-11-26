package com.huynh.ZaloCloneBe.exception;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_EXISTED(1001,"User existed"),
    USER_NOTFOUND(1002,"User NOT FOUND"),
    UNAUTHORIZED(1003,"Login faill"),
    REQUEST_FRIEND_ERROR(1004,"khong the gui loi moi chinh minh"),
    SEND_REQUEST_ERROR(1005,"Da gui loi moi roi"),
    SEND_NOTFOUND(1006,"SEND NOT FOUND"),
    RECEIVE_NOTFOUND(1007,"rECEIVE NOT FOUND")
    ;

    private int code;
    private String messenger;
}
