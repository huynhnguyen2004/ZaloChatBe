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
    REQUEST_NOTFOUND(1008,"REQUEST_NOTFOUND"),
    REQUEST_ALREADY_ACCEPTED(1009,"REQUEST_ALREADY_ACCEPTED"),
    SEND_REQUEST_ERROR(1005,"Da gui loi moi roi"),
    SEND_NOTFOUND(1006,"SEND NOT FOUND"),
    RECEIVE_NOTFOUND(1007,"rECEIVE NOT FOUND"),
    FRIEND_ALREADY(1010,"FRIEND_ALREADY"),
    CONTENT_NULL(1011,"CONTENT NOT NULL"),
    FRIEND_notfound(1012,"FRIEND_not found"),
    ;

    private int code;
    private String messenger;
}
